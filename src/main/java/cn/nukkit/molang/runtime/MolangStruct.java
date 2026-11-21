package cn.nukkit.molang.runtime;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import javax.annotation.Nullable;
import java.util.*;

/**
 * 成员布局与值存储分离的 Molang 结构。副本共享不可变布局，值和嵌套结构独立。
 */
public final class MolangStruct {
    private static final int INLINE_SLOTS = 4;
    private static final int EMPTY = 0;
    private static final int NUMBER = 1;
    private static final int REFERENCE = 2;
    private static final int DENSE_REFERENCES = 1 << (INLINE_SLOTS * 2);
    // 每个成员类型占两位，引用类型的高位为 1。
    private static final int INLINE_REFERENCE_BITS = 0b10101010;
    private static final int MAX_CACHED_PATHS = 128;
    private static final int MAX_RETAINED_EMPTY_SLOTS = 256;

    private Layout layout = Layout.EMPTY;
    private int inlineTypes;
    private float number0;
    private float number1;
    private float number2;
    private float number3;
    @Nullable
    private Object firstReference;
    @Nullable
    private Object secondReference;
    @Nullable
    private Overflow overflow;

    /**
     * 绑定属于当前运行时的成员访问，不再遍历编译表达式共享的多态缓存。
     * 结构和绑定访问均由宿主约束在线程内使用。
     */
    public BoundPath bind(MolangPath path) {
        return new BoundPath(this, path, -1);
    }

    int persistentRootSlot(MolangPath path) {
        return layout.find(path.part(0));
    }

    float nestedNumber(int rootSlot, MolangPath path) {
        Object member = getReference(rootSlot);
        if (!(member instanceof MolangStruct child)) {
            return 0;
        }
        return path.size() == 2 ? child.getNumber(child.nestedLeafSlot(path)) : child.getFloat(path, 1);
    }

    long nestedOperand(int rootSlot, MolangPath path) {
        Object member = getReference(rootSlot);
        if (!(member instanceof MolangStruct child)) {
            return MolangValues.MISSING_OPERAND;
        }
        return path.size() == 2 ? child.getOperandOrMissing(child.nestedLeafSlot(path)) : child.getOperandOrMissing(path, 1);
    }

    private int nestedLeafSlot(MolangPath path) {
        MolangPath.Slot hint = path.nestedLeafHint();
        if (hint == MolangPath.SATURATED) {
            return layout.findFrequent(path.part(1));
        }
        return hint != null && hint.layout() == layout ? ~hint.index() : slot(path, 1, false);
    }

    int persistentSlot(MolangPath path) {
        return path.size() == 1 ? persistentRootSlot(path) : -1;
    }

    BoundPath bindPersistent(MolangPath path) {
        return new BoundPath(this, path, persistentRootSlot(path));
    }

    /**
     * 一次声明本层成员布局；声明不赋值，缺失值仍与数字零区分。
     */
    public MolangStruct declareMembers(Collection<String> names) {
        Collection<String> normalized = new LinkedHashSet<>();
        for (String name : names) {
            String member = name.toLowerCase(Locale.ROOT);
            if (member.isEmpty() || member.indexOf('.') >= 0) {
                throw new IllegalArgumentException("Expected a direct Molang member name: " + name);
            }
            if (layout.find(member) < 0) {
                normalized.add(member);
            }
        }
        if (normalized.isEmpty()) {
            return this;
        }
        layout = layout.appendAll(normalized);
        ensureOverflowCapacity(layout.size());
        return this;
    }

    /**
     * 仅供运行时接管刚创建且没有外部别名的数组，避免扩容后重复复制。
     */
    void setOwnedReference(String path, @Nullable Object value) {
        String normalized = path.toLowerCase(Locale.ROOT);
        MolangStruct current = this;
        String name = normalized;
        if (normalized.indexOf('.') < 0) {
            validateFlatAssignmentPath(normalized, path);
        } else {
            String[] names = assignmentPath(normalized, path).names();
            for (int i = 0; i < names.length - 1; i++) {
                current = current.child(names[i]);
            }
            name = names[names.length - 1];
        }
        current.setReference(name, value);
    }

    void setOwnedReference(MolangPath path, @Nullable Object value) {
        MolangStruct current = this;
        int last = path.size() - 1;
        for (int step = 0; step < last; step++) {
            current = current.child(path, step);
        }
        current.setReference(current.slot(path, last, value != null), value);
    }

    @Nullable
    public Object get(String path) {
        if (path.indexOf('.') < 0) {
            return getMember(directMember(path));
        }
        return getNested(path.toLowerCase(Locale.ROOT));
    }

    @Nullable
    public Object get(MolangPath path) {
        int direct = path.directSlot(layout);
        if (direct >= 0) {
            return getMember(direct);
        }
        return get(path, 0);
    }

    @Nullable
    Object get(MolangPath path, int first) {
        MolangStruct current = this;
        int last = path.size() - 1;
        for (int i = first; i < last; i++) {
            Object member = current.getReference(current.slot(path, i, false));
            if (!(member instanceof MolangStruct nested)) {
                return null;
            }
            current = nested;
        }
        return current.getMember(current.slot(path, last, false));
    }

    public float getFloat(MolangPath path) {
        int direct = path.directSlot(layout);
        if (direct >= 0) {
            return type(direct) == NUMBER ? number(direct) : 0;
        }
        return getFloat(path, 0);
    }

    private float getFloat(MolangPath path, int first) {
        MolangStruct current = this;
        int last = path.size() - 1;
        for (int i = first; i < last; i++) {
            Object member = current.getReference(current.slot(path, i, false));
            if (!(member instanceof MolangStruct nested)) {
                return 0;
            }
            current = nested;
        }
        int slot = current.slot(path, last, false);
        return current.type(slot) == NUMBER ? current.number(slot) : 0;
    }

    public long getOperand(MolangPath path) {
        long value = getOperandOrMissing(path);
        return value == MolangValues.MISSING_OPERAND ? 0L : value;
    }

    long getOperandOrMissing(MolangPath path) {
        int direct = path.directSlot(layout);
        if (direct >= 0) {
            return getOperandOrMissing(direct);
        }
        return getOperandOrMissing(path, 0);
    }

    long getOperandOrMissing(MolangPath path, int first) {
        MolangStruct current = this;
        int last = path.size() - 1;
        for (int i = first; i < last; i++) {
            Object member = current.getReference(current.slot(path, i, false));
            if (!(member instanceof MolangStruct nested)) {
                return MolangValues.MISSING_OPERAND;
            }
            current = nested;
        }
        int slot = current.slot(path, last, false);
        return current.getOperandOrMissing(slot);
    }

    long getOperandOrMissing(int slot) {
        return switch (type(slot)) {
            case NUMBER -> Integer.toUnsignedLong(Float.floatToRawIntBits(number(slot)));
            case REFERENCE -> MolangValues.numericOperand(reference(slot));
            default -> MolangValues.MISSING_OPERAND;
        };
    }

    long equalityBitsOrMissing(int slot) {
        return switch (type(slot)) {
            case NUMBER -> Integer.toUnsignedLong(Float.floatToRawIntBits(number(slot)));
            case REFERENCE -> MolangValues.equalityBits(reference(slot));
            default -> MolangValues.MISSING_OPERAND;
        };
    }

    long numberBitsOrMissing(int slot) {
        return type(slot) == NUMBER ? Integer.toUnsignedLong(Float.floatToRawIntBits(number(slot))) : MolangValues.MISSING_OPERAND;
    }

    boolean copyNumber(MolangPath target, MolangPath source) {
        MolangStruct current = this;
        int last = source.size() - 1;
        for (int i = 0; i < last; i++) {
            Object member = current.getReference(current.slot(source, i, false));
            if (!(member instanceof MolangStruct nested)) {
                return false;
            }
            current = nested;
        }
        int slot = current.slot(source, last, false);
        if (current.type(slot) != NUMBER) {
            return false;
        }
        // 先取值再写入，目标覆盖源的祖先时仍使用原来的标量。
        float value = current.number(slot);
        set(target, value);
        return true;
    }

    public void set(MolangPath path, float value) {
        int direct = path.directSlot(layout);
        if (direct >= 0) {
            setNumber(direct, value);
            return;
        }
        setUncachedNumber(path, value);
    }

    private void setUncachedNumber(MolangPath path, float value) {
        MolangStruct current = this;
        int last = path.size() - 1;
        for (int i = 0; i < last; i++) {
            current = current.child(path, i);
        }
        current.setNumber(current.slot(path, last, true), value);
    }

    public void set(MolangPath path, @Nullable Object value) {
        if (value instanceof Number number) {
            set(path, number.floatValue());
            return;
        }
        if (value instanceof Boolean bool) {
            set(path, bool ? 1 : 0);
            return;
        }
        Object assigned = MolangValues.copy(value);
        MolangStruct current = this;
        int last = path.size() - 1;
        for (int i = 0; i < last; i++) {
            current = current.child(path, i);
        }
        current.setReference(current.slot(path, last, assigned != null), assigned);
    }

    private int slot(MolangPath path, int step, boolean create) {
        MolangPath.Slot cached = path.lookup(step, layout);
        if (cached == MolangPath.SATURATED) {
            String name = path.part(step);
            int slot = layout.findFrequent(name);
            return slot < 0 && create ? appendSlot(name) : slot;
        }
        if (cached != null && (cached.index() >= 0 || !create)) {
            return cached.index();
        }
        int slot = create ? ensureSlot(path.part(step)) : layout.find(path.part(step));
        path.cache(step, layout, slot);
        return slot;
    }

    @Nullable
    private Object getNested(String normalized) {
        MemberPath parsed = memberPath(normalized);
        if (!parsed.valid()) {
            return null;
        }
        MolangStruct current = this;
        String[] names = parsed.names();
        for (int i = 0; i < names.length - 1; i++) {
            Object member = current.getReference(names[i]);
            if (!(member instanceof MolangStruct nested)) {
                return null;
            }
            current = nested;
        }
        return current.getMember(names[names.length - 1]);
    }

    public float getFloat(String path) {
        if (path.indexOf('.') < 0) {
            int slot = directMember(path);
            return type(slot) == NUMBER ? number(slot) : 0;
        }
        return getNestedFloat(path.toLowerCase(Locale.ROOT));
    }

    private float getNestedFloat(String normalized) {
        MemberPath parsed = memberPath(normalized);
        if (!parsed.valid()) {
            return 0;
        }
        MolangStruct current = this;
        String[] names = parsed.names();
        for (int i = 0; i < names.length - 1; i++) {
            Object member = current.getReference(names[i]);
            if (!(member instanceof MolangStruct nested)) {
                return 0;
            }
            current = nested;
        }
        return current.getNumber(names[names.length - 1]);
    }

    public void set(String path, float value) {
        // 已有的规范化直接成员可以立即写入，嵌套及大小写不同的名字走常规路径。
        int existing = layout.find(path);
        if (existing >= 0) {
            setNumber(existing, value);
            return;
        }
        setPathNumber(path, value);
    }

    private void setPathNumber(String path, float value) {
        if (overflow != null && overflow.paths != null) {
            MemberPath cached = overflow.paths.get(path);
            if (cached != null && cached.valid()) {
                setNested(cached.names(), value);
                return;
            }
        }
        String normalized = path.toLowerCase(Locale.ROOT);
        if (normalized.indexOf('.') < 0) {
            validateFlatAssignmentPath(normalized, path);
            setNumber(normalized, value);
        } else {
            setNested(assignmentPath(normalized, path).names(), value);
        }
    }

    private void setNested(String[] names, float value) {
        MolangStruct current = this;
        for (int i = 0; i < names.length - 1; i++) {
            current = current.child(names[i]);
        }
        current.setNumber(names[names.length - 1], value);
    }

    public void set(String path, @Nullable Object value) {
        if (value instanceof Number number) {
            set(path, number.floatValue());
            return;
        }
        if (value instanceof Boolean bool) {
            set(path, bool ? 1 : 0);
            return;
        }
        String normalized = path.toLowerCase(Locale.ROOT);
        MemberPath parsed = null;
        if (normalized.indexOf('.') < 0) {
            validateFlatAssignmentPath(normalized, path);
        } else {
            parsed = assignmentPath(normalized, path);
        }
        // 在修改目标之前复制，允许把父结构赋给其后代而不会产生循环引用。
        Object assigned = MolangValues.copy(value);
        MolangStruct current = this;
        String name = normalized;
        if (parsed != null) {
            String[] names = parsed.names();
            for (int i = 0; i < names.length - 1; i++) {
                current = current.child(names[i]);
            }
            name = names[names.length - 1];
        }
        current.setReference(name, assigned);
    }

    public MolangStruct copy() {
        MolangStruct result = new MolangStruct();
        result.layout = layout;
        result.inlineTypes = inlineTypes;
        result.number0 = number0;
        result.number1 = number1;
        result.number2 = number2;
        result.number3 = number3;
        if (layout.size() > INLINE_SLOTS) {
            result.overflow = overflow.copy();
        }
        if ((inlineTypes & DENSE_REFERENCES) != 0) {
            Object[] copiedReferences = ((Object[]) firstReference).clone();
            for (int i = 0; i < copiedReferences.length; i++) {
                Object value = copiedReferences[i];
                if (value != null) {
                    copiedReferences[i] = MolangValues.copy(value);
                }
            }
            result.firstReference = copiedReferences;
        } else if (firstReference != null) {
            // 复制嵌套值时可能回调并修改源，先捕获两个引用，再按槽顺序复制。
            Object second = secondReference;
            result.firstReference = MolangValues.copy(firstReference);
            result.secondReference = MolangValues.copy(second);
        }
        return result;
    }

    void clear() {
        inlineTypes &= DENSE_REFERENCES;
        if (layout.size() > MAX_RETAINED_EMPTY_SLOTS) {
            // 临时作用域反复执行不同脚本时，不永久保留无界增长的名字布局。
            layout = Layout.EMPTY;
            overflow = null;
        } else if (overflow != null) {
            overflow.clear();
        }
        if (inlineTypes != 0 && layout != Layout.EMPTY) {
            Arrays.fill((Object[]) firstReference, null);
        } else {
            inlineTypes = 0;
            firstReference = null;
            secondReference = null;
        }
    }

    private MolangStruct child(String name) {
        Object member = getReference(name);
        if (member instanceof MolangStruct nested) {
            return nested;
        }
        MolangStruct nested = new MolangStruct();
        setReference(name, nested);
        return nested;
    }

    private MolangStruct child(MolangPath path, int step) {
        int slot = slot(path, step, true);
        Object member = getReference(slot);
        if (member instanceof MolangStruct nested) {
            return nested;
        }
        MolangStruct nested = new MolangStruct();
        setReference(slot, nested);
        return nested;
    }

    @Nullable
    private Object getMember(String name) {
        return getMember(layout.find(name));
    }

    @Nullable
    Object getMember(int slot) {
        return switch (type(slot)) {
            case NUMBER -> number(slot);
            case REFERENCE -> reference(slot);
            default -> null;
        };
    }

    @Nullable
    private Object getReference(String name) {
        return getReference(layout.find(name));
    }

    @Nullable
    private Object getReference(int slot) {
        return type(slot) == REFERENCE ? reference(slot) : null;
    }

    private Object reference(int slot) {
        if ((inlineTypes & DENSE_REFERENCES) != 0) {
            return ((Object[]) firstReference)[slot];
        }
        return hasEarlierReference(slot) ? secondReference : firstReference;
    }

    private boolean hasEarlierReference(int slot) {
        int precedingTypes = (1 << (slot * 2)) - 1;
        return (inlineTypes & INLINE_REFERENCE_BITS & precedingTypes) != 0;
    }

    private void removeReference(int slot) {
        if ((inlineTypes & DENSE_REFERENCES) != 0) {
            ((Object[]) firstReference)[slot] = null;
        } else {
            if (!hasEarlierReference(slot)) {
                firstReference = secondReference;
            }
            secondReference = null;
        }
    }

    private int directMember(String name) {
        int slot = layout.find(name);
        if (slot < 0) {
            String normalized = name.toLowerCase(Locale.ROOT);
            // String identity is sufficient here because this only detects whether normalization changed the immutable value.
            if (normalized != name) {
                return layout.find(normalized);
            }
        }
        return slot;
    }

    private float getNumber(String name) {
        return getNumber(layout.find(name));
    }

    float getNumber(int slot) {
        return type(slot) == NUMBER ? number(slot) : 0;
    }

    private int type(int slot) {
        if (slot < 0) {
            return EMPTY;
        }
        if (slot < INLINE_SLOTS) {
            return (inlineTypes >>> (slot * 2)) & 3;
        }
        return overflow.types[slot - INLINE_SLOTS];
    }

    private float number(int slot) {
        return switch (slot) {
            case 0 -> number0;
            case 1 -> number1;
            case 2 -> number2;
            case 3 -> number3;
            default -> overflow.numbers[slot - INLINE_SLOTS];
        };
    }

    private void setNumber(String name, float value) {
        setNumber(ensureSlot(name), value);
    }

    void setNumber(int slot, float value) {
        int previous = type(slot);
        if (previous != NUMBER) {
            if (previous == REFERENCE) {
                removeReference(slot);
            }
            setType(slot, NUMBER);
        }
        switch (slot) {
            case 0 -> number0 = value;
            case 1 -> number1 = value;
            case 2 -> number2 = value;
            case 3 -> number3 = value;
            default -> overflow.numbers[slot - INLINE_SLOTS] = value;
        }
    }

    private void setReference(String name, @Nullable Object value) {
        setReference(value == null ? layout.find(name) : ensureSlot(name), value);
    }

    private void setReference(int slot, @Nullable Object value) {
        if (slot < 0) {
            return;
        }
        if (value == null) {
            if (type(slot) == REFERENCE) {
                removeReference(slot);
            }
            setType(slot, EMPTY);
            return;
        }
        if ((inlineTypes & DENSE_REFERENCES) != 0) {
            Object[] entries = (Object[]) firstReference;
            if (slot >= entries.length) {
                entries = Arrays.copyOf(entries, Math.max(layout.size(), entries.length * 2));
                firstReference = entries;
            }
            entries[slot] = value;
        } else if (slot < INLINE_SLOTS && (secondReference == null || type(slot) == REFERENCE)) {
            if (hasEarlierReference(slot)) {
                secondReference = value;
            } else {
                if (type(slot) != REFERENCE) {
                    secondReference = firstReference;
                }
                firstReference = value;
            }
        } else {
            Object[] entries = new Object[Math.max(INLINE_SLOTS, layout.size())];
            for (int i = 0; i < INLINE_SLOTS; i++) {
                if (type(i) == REFERENCE) {
                    entries[i] = reference(i);
                }
            }
            entries[slot] = value;
            inlineTypes |= DENSE_REFERENCES;
            firstReference = entries;
            secondReference = null;
        }
        setType(slot, REFERENCE);
    }

    private int ensureSlot(String name) {
        int slot = layout.find(name);
        if (slot >= 0) {
            return slot;
        }
        return appendSlot(name);
    }

    private int appendSlot(String name) {
        int slot = layout.size();
        layout = layout.append(name);
        ensureOverflowCapacity(layout.size());
        return slot;
    }

    private void ensureOverflowCapacity(int totalSlots) {
        if (totalSlots > INLINE_SLOTS) {
            int required = totalSlots - INLINE_SLOTS;
            if (overflow == null) {
                overflow = new Overflow(Math.max(INLINE_SLOTS, required));
            } else {
                overflow.ensureCapacity(required);
            }
        }
    }

    private void setType(int slot, int type) {
        if (slot < INLINE_SLOTS) {
            int shift = slot * 2;
            inlineTypes = (inlineTypes & ~(3 << shift)) | (type << shift);
        } else {
            int offset = slot - INLINE_SLOTS;
            int previous = overflow.types[offset];
            if (previous == EMPTY && type != EMPTY) {
                overflow.size++;
            } else if (previous != EMPTY && type == EMPTY) {
                overflow.size--;
            }
            overflow.types[offset] = (byte) type;
        }
    }

    private MemberPath assignmentPath(String normalized, String original) {
        MemberPath parsed = memberPath(normalized);
        if (!parsed.valid()) {
            throw new IllegalArgumentException("Invalid Molang member path: " + original);
        }
        return parsed;
    }

    private MemberPath memberPath(String normalized) {
        if (overflow == null) {
            overflow = new Overflow();
        }
        Map<String, MemberPath> cache = overflow.paths;
        if (cache == null) {
            cache = new HashMap<>();
            overflow.paths = cache;
        }
        MemberPath parsed = cache.get(normalized);
        if (parsed != null) {
            return parsed;
        }
        boolean valid = !normalized.startsWith(".") && !normalized.endsWith(".") && !normalized.contains("..");
        parsed = new MemberPath(valid ? normalized.split("\\.") : new String[0], valid);
        // 路径只保存名称，不保存结构引用；父成员覆盖后仍从当前根重新查找。
        if (cache.size() < MAX_CACHED_PATHS) {
            cache.put(normalized, parsed);
        }
        return parsed;
    }

    private static void validateFlatAssignmentPath(String normalized, String original) {
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Invalid Molang member path: " + original);
        }
    }

    private record MemberPath(String[] names, boolean valid) {
    }

    /**
     * 每个执行实例的槽位访问器。只缓存布局，不缓存可被父字段覆盖的中间结构。
     */
    public static final class BoundPath {
        private final MolangStruct root;
        private final MolangPath path;
        private final boolean flat;
        private final int fixedRootSlot;
        @Nullable
        private Layout cachedLayout;
        private int cachedIndex;
        @Nullable
        private final Layout[] layouts;
        @Nullable
        private final int[] slots;

        private BoundPath(MolangStruct root, MolangPath path, int fixedRootSlot) {
            this.fixedRootSlot = fixedRootSlot;
            this.root = root;
            this.path = path;
            flat = path.size() == 1;
            // 两段路径的根槽已固定时，只有叶槽需要按当前子结构的布局重新定位。
            boolean needsSteps = !flat && (path.size() != 2 || fixedRootSlot < 0);
            layouts = needsSteps ? new Layout[path.size()] : null;
            slots = needsSteps ? new int[path.size()] : null;
        }

        @Nullable
        public Object get() {
            if (flat) {
                return root.getMember(flatSlot(false));
            }
            MolangStruct parent = parent(false);
            return parent != null ? parent.getMember(slot(parent, path.size() - 1, false)) : null;
        }

        public float getNumber() {
            if (flat) {
                int slot = flatSlot(false);
                return root.type(slot) == NUMBER ? root.number(slot) : 0;
            }
            MolangStruct parent = parent(false);
            if (parent == null) {
                return 0;
            }
            int slot = slot(parent, path.size() - 1, false);
            return parent.type(slot) == NUMBER ? parent.number(slot) : 0;
        }

        long numberBitsOrMissing() {
            MolangStruct parent = flat ? root : parent(false);
            if (parent == null) {
                return MolangValues.MISSING_OPERAND;
            }
            int slot = flat ? flatSlot(false) : slot(parent, path.size() - 1, false);
            return parent.numberBitsOrMissing(slot);
        }

        public long getOperand() {
            long value = getOperandOrMissing();
            return value == MolangValues.MISSING_OPERAND ? 0 : value;
        }

        long getOperandOrMissing() {
            if (flat) {
                return root.getOperandOrMissing(flatSlot(false));
            }
            MolangStruct parent = parent(false);
            if (parent == null) {
                return MolangValues.MISSING_OPERAND;
            }
            int slot = slot(parent, path.size() - 1, false);
            return parent.getOperandOrMissing(slot);
        }

        public void setNumber(float value) {
            if (flat) {
                root.setNumber(flatSlot(true), value);
                return;
            }
            MolangStruct parent = parent(true);
            parent.setNumber(slot(parent, path.size() - 1, true), value);
        }

        public void set(@Nullable Object value) {
            if (value instanceof Number number) {
                setNumber(number.floatValue());
                return;
            }
            if (value instanceof Boolean bool) {
                setNumber(bool ? 1 : 0);
                return;
            }
            Object copied = MolangValues.copy(value);
            if (flat) {
                root.setReference(flatSlot(copied != null), copied);
                return;
            }
            MolangStruct parent = parent(true);
            parent.setReference(slot(parent, path.size() - 1, copied != null), copied);
        }

        void setOwnedReference(@Nullable Object value) {
            if (flat) {
                root.setReference(flatSlot(value != null), value);
                return;
            }
            MolangStruct parent = parent(true);
            parent.setReference(slot(parent, path.size() - 1, value != null), value);
        }

        public boolean copyNumberTo(BoundPath target) {
            if (flat) {
                int slot = flatSlot(false);
                if (root.type(slot) != NUMBER) {
                    return false;
                }
                target.setNumber(root.number(slot));
                return true;
            }
            MolangStruct parent = parent(false);
            if (parent == null) {
                return false;
            }
            int slot = slot(parent, path.size() - 1, false);
            if (parent.type(slot) != NUMBER) {
                return false;
            }
            float value = parent.number(slot);
            target.setNumber(value);
            return true;
        }

        @Nullable
        private MolangStruct parent(boolean create) {
            MolangStruct current = root;
            for (int step = 0; step < path.size() - 1; step++) {
                int slot = step == 0 && fixedRootSlot >= 0 ? fixedRootSlot : slot(current, step, create);
                Object member = current.getReference(slot);
                if (member instanceof MolangStruct nested) {
                    current = nested;
                } else if (create) {
                    MolangStruct nested = new MolangStruct();
                    current.setReference(slot, nested);
                    current = nested;
                } else {
                    return null;
                }
            }
            return current;
        }

        private int slot(MolangStruct structure, int step, boolean create) {
            if (layouts == null) {
                return cachedSlot(structure, step, create);
            }
            if (layouts[step] == structure.layout && (slots[step] >= 0 || !create)) {
                return slots[step];
            }
            int slot = create ? structure.ensureSlot(path.part(step)) : structure.layout.find(path.part(step));
            slots[step] = slot;
            layouts[step] = structure.layout;
            return slot;
        }

        boolean matches(MolangPath other) {
            return path == other;
        }

        private int flatSlot(boolean create) {
            return fixedRootSlot >= 0 ? fixedRootSlot : cachedSlot(root, 0, create);
        }

        private int cachedSlot(MolangStruct structure, int step, boolean create) {
            if (cachedLayout == structure.layout && (cachedIndex >= 0 || !create)) {
                return cachedIndex;
            }
            String name = path.part(step);
            cachedIndex = create ? structure.ensureSlot(name) : structure.layout.find(name);
            cachedLayout = structure.layout;
            return cachedIndex;
        }
    }

    /**
     * 名称到槽位的不可变映射。新增字段创建新布局，已有副本继续共享原布局。
     */
    private static final class Layout {
        private static final int MAX_INTERNED_LAYOUTS = 1024;
        private static final int MAX_INTERNED_SLOTS = 64;

        private static final Map<List<String>, Layout> INTERNED = new LinkedHashMap<>(16, 0.75f, true);
        private static final Layout EMPTY = new Layout(new String[0]);

        private final String[] names;
        private volatile int frequentSlot = -1;
        @Nullable
        private final Object2IntMap<String> slots;
        @Nullable
        private volatile Layout lastAppend;

        private Layout(String[] names) {
            this.names = names;
            if (names.length > INLINE_SLOTS) {
                Object2IntMap<String> index = new Object2IntOpenHashMap<>(names.length);
                index.defaultReturnValue(-1);
                for (int i = 0; i < names.length; i++) {
                    index.put(names[i], i);
                }
                slots = index;
            } else {
                slots = null;
            }
        }

        private int size() {
            return names.length;
        }

        private int findFrequent(String name) {
            int known = frequentSlot;
            if (known >= 0 && name.equals(names[known])) {
                return known;
            }
            int slot = find(name);
            if (known < 0 && slot >= 0) {
                frequentSlot = slot;
            }
            return slot;
        }

        private int find(String name) {
            if (slots != null) {
                return slots.getInt(name);
            }
            for (int i = 0; i < names.length; i++) {
                if (name.equals(names[i])) {
                    return i;
                }
            }
            return -1;
        }

        private Layout append(String name) {
            Layout cached = lastAppend;
            if (cached != null && name.equals(cached.names[names.length])) {
                return cached;
            }
            String[] expanded = Arrays.copyOf(names, names.length + 1);
            expanded[names.length] = name;
            Layout next = intern(expanded);
            // 临时结构经常按相同顺序新增字段。只记住一个小布局后继，限制共享布局链的长度。
            if (expanded.length <= INLINE_SLOTS) {
                lastAppend = next;
            }
            return next;
        }

        private Layout appendAll(Collection<String> added) {
            String[] expanded = Arrays.copyOf(names, names.length + added.size());
            int index = names.length;
            for (String name : added) {
                expanded[index++] = name;
            }
            return intern(expanded);
        }

        private static Layout intern(String[] expanded) {
            if (expanded.length > MAX_INTERNED_SLOTS) {
                return new Layout(expanded);
            }
            // 同样的冷路径声明共享身份，使不同实体可复用已编译路径的槽位缓存。
            List<String> key = List.of(expanded);
            synchronized (INTERNED) {
                Layout existing = INTERNED.get(key);
                if (existing != null) {
                    return existing;
                }
                Layout created = new Layout(expanded);
                if (INTERNED.size() == MAX_INTERNED_LAYOUTS) {
                    INTERNED.remove(INTERNED.keySet().iterator().next());
                }
                INTERNED.put(key, created);
                return created;
            }
        }
    }

    private static final class Overflow {
        private static final float[] NO_NUMBERS = {};
        private static final byte[] NO_TYPES = {};

        private float[] numbers;
        private byte[] types;
        private int size;
        @Nullable
        private Map<String, MemberPath> paths;

        private Overflow() {
            this(NO_NUMBERS, NO_TYPES, 0);
        }

        private Overflow(int capacity) {
            numbers = new float[capacity];
            types = new byte[capacity];
        }

        private Overflow(float[] numbers, byte[] types, int size) {
            this.numbers = numbers;
            this.types = types;
            this.size = size;
        }

        private void ensureCapacity(int capacity) {
            if (capacity > types.length) {
                int grown = Math.max(Math.max(INLINE_SLOTS, capacity), types.length * 2);
                numbers = Arrays.copyOf(numbers, grown);
                types = Arrays.copyOf(types, grown);
            }
        }

        private Overflow copy() {
            return new Overflow(numbers.clone(), types.clone(), size);
        }

        private void clear() {
            if (size != 0) {
                Arrays.fill(types, (byte) EMPTY);
                size = 0;
            }
        }
    }
}
