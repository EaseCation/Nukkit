package cn.nukkit.molang.runtime;

import javax.annotation.Nullable;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * 一个程序绑定到一个 runtime 的访问视图，槽位数组不与其它实体或线程共享。
 */
final class MolangBoundRuntime extends MolangRuntimeForwarder {
    private static final MolangQueryBinding[] NO_MORE_QUERIES = {};
    private static final MolangStruct.BoundPath[] NO_PATHS = {};

    private final MolangRuntimeBuilder.MolangRuntimeImpl owner;
    private final MolangBindingPlan plan;
    @Nullable
    private final MolangPath firstVariable;
    private final int firstVariableSlot;
    private final MolangStruct.BoundPath[] variables;
    private final MolangStruct.BoundPath[] contexts;
    private final MolangStruct.BoundPath[] arrays;
    @Nullable
    private ElementBinding lastElementBinding;
    @Nullable
    private ElementBinding[] elementBindings;
    @Nullable
    private NumericElementCopy firstElementCopy;
    // 将首个查询直接绑定到实例字段。单查询程序可以省去槽位数组，多查询程序共享同一分派规则。
    @Nullable
    private final MolangQueryRef firstQuery;
    @Nullable
    private final MolangQueryBinding firstBinding;
    private final MolangQueryBinding[] remainingQueries;
    @Nullable
    private Map<MolangStruct, MolangStruct.BoundPath[]> temporaryBindings;
    @Nullable
    private MolangStruct lastTemps;
    @Nullable
    private MolangStruct.BoundPath[] lastTempBindings;

    MolangBoundRuntime(MolangRuntimeBuilder.MolangRuntimeImpl owner, MolangBindingPlan plan) {
        super(owner);
        this.owner = owner;
        this.plan = plan;
        MolangPath first = null;
        int fixedSlot = -1;
        for (int index = 0; index < plan.pathCount(); index++) {
            MolangPath path = plan.path(index);
            fixedSlot = owner.variables.persistentSlot(path);
            if (fixedSlot >= 0) {
                first = path;
                break;
            }
        }
        firstVariableSlot = fixedSlot;
        firstVariable = first;
        int paths = plan.pathCount();
        variables = paths == 0 ? NO_PATHS : new MolangStruct.BoundPath[paths];
        contexts = paths == 0 ? NO_PATHS : new MolangStruct.BoundPath[paths];
        arrays = paths == 0 ? NO_PATHS : new MolangStruct.BoundPath[paths];
        int count = plan.queryCount();
        firstQuery = count == 0 ? null : plan.query(0);
        firstBinding = firstQuery == null ? null : owner.binding(firstQuery.name());
        remainingQueries = count <= 1 ? NO_MORE_QUERIES : new MolangQueryBinding[count - 1];
        for (int index = 1; index < count; index++) {
            remainingQueries[index - 1] = owner.binding(plan.query(index).name());
        }
    }

    @Override
    public long queryOperand0(MolangQueryRef query) {
        return query(query).queryOperand0();
    }

    @Override
    public long queryOperand1(MolangQueryRef query, @Nullable Object a) {
        return query(query).queryOperand1(a);
    }

    @Override
    public long queryOperand2(MolangQueryRef query, @Nullable Object a, @Nullable Object b) {
        return query(query).queryOperand2(a, b);
    }

    @Override
    public long queryOperand3(MolangQueryRef query, @Nullable Object a, @Nullable Object b, @Nullable Object c) {
        return query(query).queryOperand3(a, b, c);
    }

    boolean matches(MolangBindingPlan expected, MolangRuntime runtime) {
        return plan == expected && owner == runtime;
    }

    @Nullable
    private MolangStruct.BoundPath binding(MolangPath path, MolangStruct root, MolangStruct.BoundPath[] entries) {
        int index = path.bindingIndex();
        if (index < 0 || index >= entries.length) {
            return null;
        }
        MolangStruct.BoundPath binding = entries[index];
        if (binding != null) {
            return binding.matches(path) ? binding : null;
        }
        if (plan.path(index) != path) {
            return null;
        }
        // 变量根和数组根的布局只追加字段，临时作用域清理时可能重置布局。
        binding = root == owner.variables || root == owner.arrays || root == owner.contextValues ? root.bindPersistent(path) : root.bind(path);
        entries[index] = binding;
        return binding;
    }

    @Nullable
    private MolangStruct.BoundPath tempBinding(MolangPath path) {
        if (variables.length == 0) {
            return null;
        }
        MolangStruct root = owner.currentTemps();
        if (root != lastTemps) {
            if (lastTemps == null) {
                lastTempBindings = new MolangStruct.BoundPath[variables.length];
            } else {
                if (temporaryBindings == null) {
                    // 普通程序只接触一个临时根，出现第二个根时才为嵌套作用域保存两组绑定。
                    temporaryBindings = new IdentityHashMap<>(2);
                    temporaryBindings.put(lastTemps, lastTempBindings);
                }
                lastTempBindings = temporaryBindings.computeIfAbsent(root, _ -> new MolangStruct.BoundPath[variables.length]);
            }
            lastTemps = root;
        }
        return binding(path, root, lastTempBindings);
    }

    private MolangQueryBinding query(MolangQueryRef query) {
        int index = query.bindingIndex();
        if (index == 0) {
            return query == firstQuery ? firstBinding : owner.binding(query.name());
        }
        return index <= remainingQueries.length && plan.query(index) == query ? remainingQueries[index - 1] : owner.binding(query.name());
    }

    @Nullable
    MolangRuntime linkSingleEvaluationQuery(MolangBindingPlan expected, int slot, MolangNumberContextQuery[] targets) {
        if (plan != expected || targets.length != 1 || slot < 0 || slot >= plan.queryCount()) {
            return null;
        }
        MolangQueryRef ref = plan.query(slot);
        MolangNumberContextQuery target = ref.foreignContext() ? null : query(ref).singleEvaluationTarget();
        if (target == null) {
            return null;
        }
        targets[0] = target;
        return owner;
    }

    @Nullable
    MolangRuntime linkProperties(MolangBindingPlan expected, int[] slots, MolangNumberContextQuery[] targets) {
        if (plan != expected || slots.length == 0 || slots.length != targets.length) {
            return null;
        }
        for (int i = 0; i < slots.length; i++) {
            MolangQueryRef ref = plan.query(slots[i]);
            MolangNumberProperty.Cell property = ref.foreignContext() ? null : query(ref).property();
            if (property == null) {
                return null;
            }
            targets[i] = property;
        }
        return owner;
    }

    boolean linkVariables(MolangBindingPlan expected, int[] paths, int[] slots) {
        return linkVariables(expected, paths, slots, false);
    }

    boolean linkVariables(MolangBindingPlan expected, int[] paths, int[] slots, boolean roots) {
        if (plan != expected || paths.length != slots.length) {
            return false;
        }
        boolean found = false;
        for (int i = 0; i < paths.length; i++) {
            MolangPath path = plan.path(paths[i]);
            slots[i] = roots ? owner.variables.persistentRootSlot(path) : owner.variables.persistentSlot(path);
            found |= slots[i] >= 0;
        }
        return found;
    }

    @Nullable
    MolangRuntime linkNumberQueries(MolangBindingPlan expected, int[] slots, MolangNumberContextQuery[] targets) {
        return linkNumberQueries(expected, slots, targets, false);
    }

    @Nullable
    MolangRuntime linkNumberQueries(MolangBindingPlan expected, int[] slots, MolangNumberContextQuery[] targets, boolean unary) {
        if (plan != expected || slots.length != targets.length) {
            return null;
        }
        for (int index = 0; index < slots.length; index++) {
            MolangQueryRef ref = plan.query(slots[index]);
            MolangQueryBinding actual = query(ref);
            MolangNumberContextQuery target = unary && ref.definition().minArguments() == 1 ? actual.linkedNumber1(ref.definition()) : actual.linkedNumber0();
            if (ref.foreignContext() || target == null) {
                return null;
            }
            targets[index] = target;
        }
        return owner;
    }

    @Override
    @Nullable
    public Object getVariableValue(MolangPath path) {
        if (path == firstVariable) {
            Object value = owner.variables.getMember(firstVariableSlot);
            return value != null ? value : owner.arrays.get(path);
        }
        MolangStruct.BoundPath binding = binding(path, owner.variables, variables);
        if (binding == null) {
            return owner.getVariableValue(path);
        }
        Object value = binding.get();
        return value != null ? value : binding(path, owner.arrays, arrays).get();
    }

    @Override
    public float getVariableNumber(MolangPath path) {
        MolangStruct.BoundPath binding = binding(path, owner.variables, variables);
        return binding != null ? binding.getNumber() : owner.getVariableNumber(path);
    }

    @Override
    public long getVariableOperand(MolangPath path) {
        if (path == firstVariable) {
            long value = owner.variables.getOperandOrMissing(firstVariableSlot);
            return value != MolangValues.MISSING_OPERAND ? value : owner.arrays.getOperand(path);
        }
        MolangStruct.BoundPath binding = binding(path, owner.variables, variables);
        if (binding == null) {
            return owner.getVariableOperand(path);
        }
        long value = binding.getOperandOrMissing();
        return value != MolangValues.MISSING_OPERAND ? value : binding(path, owner.arrays, arrays).getOperand();
    }

    @Override
    public void setVariableValue(MolangPath path, @Nullable Object value) {
        MolangStruct.BoundPath binding = binding(path, owner.variables, variables);
        if (binding == null) {
            owner.setVariableValue(path, value);
        } else {
            binding.set(value);
            owner.variableChanged();
        }
    }

    @Override
    public void setVariableNumber(MolangPath path, float value) {
        if (path == firstVariable && firstVariableSlot >= 0) {
            owner.variables.setNumber(firstVariableSlot, value);
            owner.variableChanged();
            return;
        }
        MolangStruct.BoundPath binding = binding(path, owner.variables, variables);
        if (binding == null) {
            owner.setVariableNumber(path, value);
        } else {
            binding.setNumber(value);
            owner.variableChanged();
        }
    }

    @Override
    public void copyVariableValue(MolangPath target, MolangPath source) {
        MolangStruct.BoundPath sourceBinding = binding(source, owner.variables, variables);
        MolangStruct.BoundPath targetBinding = binding(target, owner.variables, variables);
        if (sourceBinding == null || targetBinding == null) {
            owner.copyVariableValue(target, source);
            return;
        }
        if (!sourceBinding.copyNumberTo(targetBinding)) {
            targetBinding.set(getVariableValue(source));
        }
        owner.variableChanged();
    }

    @Override
    @Nullable
    public Object getTempValue(MolangPath path) {
        MolangStruct.BoundPath binding = tempBinding(path);
        return binding != null ? binding.get() : owner.getTempValue(path);
    }

    @Override
    public float getTempNumber(MolangPath path) {
        MolangStruct.BoundPath binding = tempBinding(path);
        return binding != null ? binding.getNumber() : owner.getTempNumber(path);
    }

    @Override
    public long getTempOperand(MolangPath path) {
        MolangStruct.BoundPath binding = tempBinding(path);
        return binding != null ? binding.getOperand() : owner.getTempOperand(path);
    }

    @Override
    public void setTempValue(MolangPath path, @Nullable Object value) {
        MolangStruct.BoundPath binding = tempBinding(path);
        if (binding == null) {
            owner.setTempValue(path, value);
        } else {
            binding.set(value);
            owner.tempChanged();
        }
    }

    @Override
    public void setTempNumber(MolangPath path, float value) {
        MolangStruct.BoundPath binding = tempBinding(path);
        if (binding == null) {
            owner.setTempNumber(path, value);
        } else {
            binding.setNumber(value);
            owner.tempChanged();
        }
    }

    @Override
    @Nullable
    public Object getContextValue(MolangPath path) {
        MolangStruct.BoundPath binding = binding(path, owner.contextValues, contexts);
        return binding != null ? binding.get() : owner.getContextValue(path);
    }

    @Override
    public float getContextNumber(MolangPath path) {
        MolangStruct.BoundPath binding = binding(path, owner.contextValues, contexts);
        return binding != null ? binding.getNumber() : owner.getContextNumber(path);
    }

    @Override
    public long getContextOperand(MolangPath path) {
        MolangStruct.BoundPath binding = binding(path, owner.contextValues, contexts);
        return binding != null ? binding.getOperand() : owner.getContextOperand(path);
    }

    @Override
    public void setContextValue(MolangPath path, @Nullable Object value) {
        MolangStruct.BoundPath binding = binding(path, owner.contextValues, contexts);
        if (binding == null) {
            owner.setContextValue(path, value);
        } else {
            binding.set(value);
            owner.contextChanged();
        }
    }

    @Override
    public void setContextNumber(MolangPath path, float value) {
        MolangStruct.BoundPath binding = binding(path, owner.contextValues, contexts);
        if (binding == null) {
            owner.setContextNumber(path, value);
        } else {
            binding.setNumber(value);
            owner.contextChanged();
        }
    }

    @Override
    @Nullable
    public Object getArrayValue(MolangPath path) {
        MolangStruct.BoundPath binding = binding(path, owner.arrays, arrays);
        if (binding == null) {
            return owner.getArrayValue(path);
        }
        Object value = binding.get();
        return value != null ? value : binding(path, owner.variables, variables).get();
    }

    @Override
    @Nullable
    public Object getVariableElementValue(MolangPath path, int index) {
        return MolangValues.getElement(elementSource(path), index);
    }

    @Override
    public float getVariableElementNumber(MolangPath path, int index) {
        return MolangValues.getElementFloat(elementSource(path), index);
    }

    @Override
    public long getVariableElementOperand(MolangPath path, int index) {
        return MolangValues.getElementOperand(elementSource(path), index);
    }

    @Nullable
    private Object elementSource(MolangPath path) {
        ElementBinding entry = elementBinding(path);
        return entry == null ? owner.getVariableValue(path) : entry.source();
    }

    @Nullable
    private ElementBinding elementBinding(MolangPath path) {
        if (lastElementBinding != null && lastElementBinding.path == path) {
            return lastElementBinding;
        }
        int index = path.bindingIndex();
        if (index < 0 || index >= plan.pathCount() || plan.path(index) != path) {
            return null;
        }
        if (elementBindings == null) {
            elementBindings = new ElementBinding[plan.pathCount()];
        }
        ElementBinding entry = elementBindings[index];
        if (entry == null) {
            entry = new ElementBinding(path, binding(path, owner.variables, variables), binding(path, owner.arrays, arrays));
            elementBindings[index] = entry;
        }
        lastElementBinding = entry;
        return entry;
    }

    private record ElementBinding(MolangPath path, MolangStruct.BoundPath variable, MolangStruct.BoundPath array) {
        @Nullable
        private Object source() {
            return source(variable.get());
        }

        @Nullable
        private Object source(@Nullable Object variableValue) {
            return variableValue != null ? variableValue : array.get();
        }

        private void writeNumber(int index, float value) {
            Object variableValue = variable.get();
            Object previous = source(variableValue);
            replaceIfChanged(variableValue, previous, MolangArrays.writeNumber(previous, index, value));
        }

        private void writeValue(int index, @Nullable Object value) {
            Object variableValue = variable.get();
            Object previous = source(variableValue);
            replaceIfChanged(variableValue, previous, MolangArrays.write(previous, index, value));
        }

        private void replaceIfChanged(@Nullable Object variableValue, @Nullable Object previous, @Nullable Object updated) {
            if (updated != previous) {
                // 深复制可能回调并修改宿主；扩容的新容器仍写回本次选中的根，两侧均缺失时创建变量。
                MolangStruct.BoundPath target = variableValue != null || previous == null ? variable : array;
                target.setOwnedReference(updated);
            }
        }
    }

    boolean copyNumericVariableToElement(MolangPath target, MolangPath index, MolangPath source) {
        NumericElementCopy copy = firstElementCopy;
        if (copy == null) {
            int indexSlot = owner.variables.persistentSlot(index);
            int valueSlot = owner.variables.persistentSlot(source);
            if (indexSlot >= 0 && valueSlot >= 0) {
                ElementBinding entry = elementBinding(target);
                if (entry != null) {
                    copy = new NumericElementCopy(entry, index, source, indexSlot, valueSlot);
                    firstElementCopy = copy;
                }
            }
        }
        if (copy != null && copy.target.path == target && copy.indexPath == index && copy.sourcePath == source) {
            return copy.writeNumber(owner);
        }
        MolangStruct.BoundPath indexBinding = binding(index, owner.variables, variables);
        MolangStruct.BoundPath sourceBinding = binding(source, owner.variables, variables);
        if (indexBinding == null || sourceBinding == null) {
            return false;
        }
        long offset = indexBinding.numberBitsOrMissing();
        long value = sourceBinding.numberBitsOrMissing();
        if (offset == MolangValues.MISSING_OPERAND || value == MolangValues.MISSING_OPERAND) {
            return false;
        }
        setVariableElementNumber(target, (int) Float.intBitsToFloat((int) offset), Float.intBitsToFloat((int) value));
        return true;
    }

    private record NumericElementCopy(ElementBinding target, MolangPath indexPath, MolangPath sourcePath, int indexSlot, int valueSlot) {
        private boolean writeNumber(MolangRuntimeBuilder.MolangRuntimeImpl owner) {
            long offset = owner.variables.numberBitsOrMissing(indexSlot);
            long value = owner.variables.numberBitsOrMissing(valueSlot);
            if (offset == MolangValues.MISSING_OPERAND || value == MolangValues.MISSING_OPERAND) {
                return false;
            }
            target.writeNumber((int) Float.intBitsToFloat((int) offset), Float.intBitsToFloat((int) value));
            owner.variableChanged();
            return true;
        }
    }

    @Override
    public void setVariableElementValue(MolangPath path, int index, @Nullable Object value) {
        ElementBinding entry = elementBinding(path);
        if (entry == null) {
            owner.setVariableElementValue(path, index, value);
        } else {
            entry.writeValue(index, value);
            owner.variableChanged();
        }
    }

    @Override
    public void setVariableElementNumber(MolangPath path, int index, float value) {
        ElementBinding entry = elementBinding(path);
        if (entry == null) {
            owner.setVariableElementNumber(path, index, value);
        } else {
            entry.writeNumber(index, value);
            owner.variableChanged();
        }
    }

    @Override
    public void setTempElementValue(MolangPath path, int index, @Nullable Object value) {
        MolangStruct.BoundPath binding = tempBinding(path);
        if (binding == null) {
            owner.setTempElementValue(path, index, value);
        } else {
            Object old = binding.get();
            Object updated = MolangArrays.write(old, index, value);
            if (updated != old) {
                binding.setOwnedReference(updated);
            }
            owner.tempChanged();
        }
    }

    @Override
    public void setTempElementNumber(MolangPath path, int index, float value) {
        MolangStruct.BoundPath binding = tempBinding(path);
        if (binding == null) {
            owner.setTempElementNumber(path, index, value);
        } else {
            Object old = binding.get();
            Object updated = MolangArrays.writeNumber(old, index, value);
            if (updated != old) {
                binding.setOwnedReference(updated);
            }
            owner.tempChanged();
        }
    }


    @Override
    @Nullable
    public Object queryValue0(MolangQueryRef query) {
        return query(query).queryValue0();
    }

    @Override
    public float queryNumber0(MolangQueryRef query) {
        return query(query).queryNumber0();
    }

    @Override
    @Nullable
    public Object queryValue1(MolangQueryRef query, @Nullable Object a) {
        return query(query).queryValue1(a);
    }

    @Override
    public float queryNumber1(MolangQueryRef query, float a) {
        return query(query).queryNumber1(a);
    }

    @Override
    @Nullable
    public Object queryValue2(MolangQueryRef query, @Nullable Object a, @Nullable Object b) {
        return query(query).queryValue2(a, b);
    }

    @Override
    public float queryNumber2(MolangQueryRef query, float a, float b) {
        return query(query).queryNumber2(a, b);
    }

    @Override
    @Nullable
    public Object queryValue3(MolangQueryRef query, @Nullable Object a, @Nullable Object b, @Nullable Object c) {
        return query(query).queryValue3(a, b, c);
    }

    @Override
    public float queryNumber3(MolangQueryRef query, float a, float b, float c) {
        return query(query).queryNumber3(a, b, c);
    }

    @Override
    @Nullable
    public Object queryValue(MolangQueryRef query, Object... args) {
        return query(query).queryValue(args);
    }
}
