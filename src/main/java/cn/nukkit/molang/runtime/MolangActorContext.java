package cn.nukkit.molang.runtime;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BooleanSupplier;

/**
 * 将实体私有变量与公开发布值分开。宿主在明确的帧边界调用 publish()。
 */
public final class MolangActorContext {
    private static final int MAX_NAMED_PATHS = 128;

    private final MolangRuntime local;
    private final MolangPath[] publicVariables;
    @Nullable
    private final String singleRoot;
    private final int numberSlot;
    private final BooleanSupplier alive;
    private final MolangEntityReference reference;
    private volatile Map<String, MolangPath> namedPaths = Map.of();
    private volatile MolangRuntime published;
    private volatile boolean valid = true;

    public MolangActorContext(MolangRuntime local, Collection<String> publicVariables) {
        this(local, publicVariables, () -> true);
    }

    public MolangActorContext(MolangRuntime local, Collection<String> publicVariables, BooleanSupplier alive) {
        this.local = Objects.requireNonNull(local, "local");
        this.publicVariables = publicVariables.stream()
                .map(MolangActorContext::variableName)
                .distinct()
                .map(MolangPath::new)
                .toArray(MolangPath[]::new);
        singleRoot = this.publicVariables.length == 1 && this.publicVariables[0].size() == 1 ? this.publicVariables[0].name() : null;
        numberSlot = singleRoot != null && local instanceof MolangRuntimeBuilder.MolangRuntimeImpl owner ? owner.variables.persistentSlot(this.publicVariables[0]) : -1;
        this.alive = Objects.requireNonNull(alive, "alive");
        published = new PublishedRuntime(singleRoot == null ? new MolangStruct() : null);
        WeakReference<MolangActorContext> owner = new WeakReference<>(this);
        reference = () -> {
            MolangActorContext context = owner.get();
            return context != null && context.valid && context.alive.getAsBoolean() ? context.published : null;
        };
    }

    public MolangRuntime local() {
        return local;
    }

    public MolangEntityReference publicReference() {
        return reference;
    }

    public void publish() {
        if (!valid || !alive.getAsBoolean() || publicVariables.length == 0) {
            return;
        }
        boolean builtin = MolangValues.isBuiltinStorage(local);
        if (singleRoot != null) {
            if (numberSlot >= 0) {
                long bits = ((MolangRuntimeBuilder.MolangRuntimeImpl) local).variables.numberBitsOrMissing(numberSlot);
                if (bits != MolangValues.MISSING_OPERAND) {
                    published = new PublishedNumber(Float.intBitsToFloat((int) bits));
                    return;
                }
            }
            Object value = readPublicVariable(publicVariables[0], builtin);
            published = value instanceof Number || value instanceof Boolean ? new PublishedNumber(MolangValues.toFloat(value)) : new PublishedRuntime(MolangValues.copy(value));
            return;
        }
        MolangStruct snapshot = new MolangStruct();
        for (MolangPath path : publicVariables) {
            Object value = readPublicVariable(path, builtin);
            if (value != null) {
                snapshot.set(path, value);
            }
        }
        published = new PublishedRuntime(snapshot);
    }

    @Nullable
    private Object readPublicVariable(MolangPath path, boolean builtin) {
        return builtin ? local.getVariableValue(path) : local.getVariableValue(path.name());
    }

    @Nullable
    private MolangPath namedPath(String name) {
        String normalized = name.toLowerCase(Locale.ROOT);
        if (singleRoot != null) {
            if (singleRoot.equals(normalized)) {
                return publicVariables[0];
            }
            int end = singleRoot.length();
            if (normalized.length() <= end || normalized.charAt(end) != '.' || !normalized.startsWith(singleRoot)) {
                return null;
            }
        }
        MolangPath cached = namedPaths.get(normalized);
        if (cached != null) {
            return cached;
        }
        if (normalized.isEmpty() || normalized.startsWith(".") || normalized.endsWith(".") || normalized.contains("..")) {
            return null;
        }
        return cacheNamedPath(normalized);
    }

    // 字符串入口共用预解析路径。并发读取不同快照时，缓存不修改任何已发布的结构。
    private synchronized MolangPath cacheNamedPath(String name) {
        if (namedPaths.isEmpty()) {
            namedPaths = new ConcurrentHashMap<>();
        }
        if (namedPaths.size() < MAX_NAMED_PATHS) {
            return namedPaths.computeIfAbsent(name, MolangPath::new);
        }
        MolangPath cached = namedPaths.get(name);
        return cached != null ? cached : new MolangPath(name);
    }

    public void invalidate() {
        valid = false;
    }

    private static String variableName(String source) {
        String name = Objects.requireNonNull(source, "public variable").toLowerCase(Locale.ROOT);
        if (name.startsWith("variable.")) {
            name = name.substring("variable.".length());
        } else if (name.startsWith("v.")) {
            name = name.substring("v.".length());
        }
        return name;
    }

    /**
     * 公开快照的数值运算可直接读取已规范化的变量槽。
     */
    @SuppressWarnings("unused")
    public static boolean isPublishedView(MolangRuntime runtime) {
        return runtime instanceof PublishedRuntime;
    }

    /**
     * 箭头读取完成后写入持久变量；赋值结果被丢弃时，已发布的数值可直接进入内建数值槽。
     */
    @SuppressWarnings("unused")
    public static void assignArrow(MolangRuntime evaluation, MolangRuntime destination, @Nullable Object reference, MolangPath source, MolangPath target) {
        MolangRuntime view = MolangValues.asRuntime(reference);
        if (view == null) {
            destination.setVariableValue(target, null);
            return;
        }
        evaluation.invalidateQueries();
        if (view instanceof PublishedNumber number && number.matchesRoot(source) && MolangValues.isBuiltinStorage(destination)) {
            destination.setVariableNumber(target, number.number);
        } else {
            destination.setVariableValue(target, view.getVariableValue(source));
        }
    }

    // 初次、二次读取共用哨兵；只有持续复用的快照才为其前两条嵌套路径分配缓存。
    private record OperandCache(@Nullable MolangPath first, long firstValue, @Nullable MolangPath second, long secondValue) {
        private static final OperandCache ONCE = new OperandCache(null, 0, null, 0);
        private static final OperandCache TWICE = new OperandCache(null, 0, null, 0);
    }

    private final class PublishedNumber extends PublishedRuntime {
        private final float number;

        private PublishedNumber(float number) {
            super(null);
            this.number = number;
        }

        @Override
        Object rootValue() {
            return number;
        }

        @Override
        float rootNumber() {
            return number;
        }
    }

    private class PublishedRuntime implements MolangRuntime {
        @Nullable
        private final Object snapshot;
        @Nullable
        private volatile OperandCache operands;

        private PublishedRuntime(@Nullable Object snapshot) {
            this.snapshot = snapshot;
        }

        final boolean matchesRoot(MolangPath path) {
            return path.size() == 1 && path.part(0).equals(singleRoot);
        }

        @Nullable
        Object rootValue() {
            return snapshot;
        }

        float rootNumber() {
            return 0;
        }

        @Nullable
        private Object read(MolangPath path) {
            if (singleRoot == null) {
                return ((MolangStruct) snapshot).get(path);
            }
            if (!singleRoot.equals(path.part(0))) {
                return null;
            }
            if (path.size() == 1) {
                return rootValue();
            }
            return snapshot instanceof MolangStruct root ? root.get(path, 1) : null;
        }

        @Override
        @Nullable
        public Object getHost() {
            return local.getHost();
        }

        @Override
        public MolangQuerySchema querySchema() {
            return local.querySchema();
        }

        @Override
        public void beginEvaluation() {
            local.beginEvaluation();
        }

        @Override
        public void endEvaluation() {
            local.endEvaluation();
        }

        @Override
        public void beginFrame(long frameId) {
            local.beginFrame(frameId);
        }

        @Override
        public void endFrame() {
            local.endFrame();
        }

        @Override
        public void invalidateQueries() {
            local.invalidateQueries();
        }

        @Override
        public float getVariable(String name) {
            MolangPath path = namedPath(name);
            return path == null ? 0 : getVariableNumber(path);
        }

        @Override
        @Nullable
        public Object getVariableValue(String name) {
            // 公开复杂值不能经宿主 query 的可变参数别名反向修改发布快照。
            MolangPath path = namedPath(name);
            return path == null ? null : getVariableValue(path);
        }

        @Override
        public float getVariableNumber(String name) {
            return getVariable(name);
        }

        @Override
        public float getVariableNumber(MolangPath path) {
            return Float.intBitsToFloat((int) readOperand(path));
        }

        @Override
        public long getVariableOperand(MolangPath path) {
            if (path.size() == 1) {
                return readOperand(path);
            }
            OperandCache cache = operands;
            if (cache != null) {
                if (cache.first == path) {
                    return cache.firstValue;
                }
                if (cache.second == path) {
                    return cache.secondValue;
                }
            }
            long value = readOperand(path);
            if (cache == null) {
                operands = OperandCache.ONCE;
            } else if (cache == OperandCache.ONCE) {
                operands = OperandCache.TWICE;
            } else if (cache == OperandCache.TWICE) {
                operands = new OperandCache(path, value, null, 0);
            } else if (cache.second == null) {
                operands = new OperandCache(cache.first, cache.firstValue, path, value);
            }
            return value;
        }

        private long readOperand(MolangPath path) {
            if (singleRoot == null) {
                return ((MolangStruct) snapshot).getOperand(path);
            }
            if (!singleRoot.equals(path.part(0))) {
                return 0;
            }
            if (path.size() == 1) {
                return snapshot instanceof String ? MolangValues.STRING_OPERAND : Integer.toUnsignedLong(Float.floatToRawIntBits(rootNumber()));
            }
            if (snapshot instanceof MolangStruct root) {
                long operand = root.getOperandOrMissing(path, 1);
                return operand == MolangValues.MISSING_OPERAND ? 0 : operand;
            }
            return 0;
        }

        @Override
        @Nullable
        public Object getVariableValue(MolangPath path) {
            return MolangValues.copy(read(path));
        }

        @Override
        public void setVariable(String name, float value) {
            throw readOnly();
        }

        @Override
        public void setVariableValue(String name, @Nullable Object value) {
            throw readOnly();
        }

        @Override
        public float getTemp(String name) {
            return local.getTemp(name);
        }

        @Override
        public void setTemp(String name, float value) {
            local.setTemp(name, value);
        }

        @Override
        @Nullable
        public Object getTempValue(String name) {
            return local.getTempValue(name);
        }

        @Override
        public void setTempValue(String name, @Nullable Object value) {
            local.setTempValue(name, value);
        }

        @Override
        public float getContext(String name) {
            return local.getContext(name);
        }

        @Override
        @Nullable
        public Object getContextValue(String name) {
            return local.getContextValue(name);
        }

        @Override
        public QueryHandler resolveQuery(String name) {
            return local.resolveQuery(name);
        }

        @Override
        @Nullable
        public Object queryValue(String name, Object... args) {
            return local.queryValue(name, args);
        }

        @Override
        public float getThis() {
            return local.getThis();
        }

        @Override
        public float getVariableElement(String name, int index) {
            return MolangValues.toFloat(getVariableElementValue(name, index));
        }

        @Override
        @Nullable
        public Object getVariableElementValue(String name, int index) {
            MolangPath path = namedPath(name);
            return path == null ? null : MolangValues.copy(MolangValues.getElement(read(path), index));
        }

        @Override
        public void setVariableElement(String name, int index, float value) {
            throw readOnly();
        }

        @Override
        public void setVariableElementValue(String name, int index, @Nullable Object value) {
            throw readOnly();
        }

        @Override
        public float log(float value) {
            return local.log(value);
        }

        @Override
        public float debugOutput(float value) {
            return local.debugOutput(value);
        }

        @Override
        public boolean supportsStableVariableReads() {
            return true;
        }

        private UnsupportedOperationException readOnly() {
            return new UnsupportedOperationException("Published Molang variables are read-only");
        }
    }
}
