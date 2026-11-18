package cn.nukkit.molang.runtime;

import javax.annotation.Nullable;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * 一个程序绑定到一个 runtime 的访问视图，槽位数组不与其它实体或线程共享。
 */
final class MolangBoundRuntime extends MolangRuntimeForwarder {
    private final MolangRuntimeBuilder.MolangRuntimeImpl owner;
    private final MolangBindingPlan plan;
    private final MolangStruct.BoundPath[] variables;
    private final MolangStruct.BoundPath[] contexts;
    private final MolangStruct.BoundPath[] arrays;
    private final MolangQueryBinding[] queries;

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
    private final Map<MolangStruct, MolangStruct.BoundPath[]> temporaryBindings = new IdentityHashMap<>();
    @Nullable
    private MolangStruct lastTemps;
    @Nullable
    private MolangStruct.BoundPath[] lastTempBindings;

    MolangBoundRuntime(MolangRuntimeBuilder.MolangRuntimeImpl owner, MolangBindingPlan plan) {
        super(owner);
        this.owner = owner;
        this.plan = plan;
        variables = new MolangStruct.BoundPath[plan.pathCount()];
        contexts = new MolangStruct.BoundPath[plan.pathCount()];
        arrays = new MolangStruct.BoundPath[plan.pathCount()];
        queries = new MolangQueryBinding[plan.queryCount()];
        for (int index = 0; index < queries.length; index++) {
            queries[index] = owner.binding(plan.query(index).name());
        }
    }

    @Nullable
    private MolangStruct.BoundPath binding(MolangPath path, MolangStruct root, MolangStruct.BoundPath[] entries) {
        int index = path.bindingIndex();
        if (index < 0 || index >= entries.length || plan.path(index) != path) {
            return null;
        }
        MolangStruct.BoundPath binding = entries[index];
        if (binding == null) {
            binding = root.bind(path);
            entries[index] = binding;
        }
        return binding;
    }

    @Nullable
    private MolangStruct.BoundPath tempBinding(MolangPath path) {
        MolangStruct root = owner.currentTemps();
        if (root != lastTemps) {
            lastTemps = root;
            lastTempBindings = temporaryBindings.computeIfAbsent(root, ignored -> new MolangStruct.BoundPath[plan.pathCount()]);
        }
        return binding(path, root, lastTempBindings);
    }

    private MolangQueryBinding query(MolangQueryRef query) {
        int index = query.bindingIndex();
        return index >= 0 && index < queries.length && plan.query(index) == query ? queries[index] : owner.binding(query.name());
    }

    @Override
    @Nullable
    public Object getVariableValue(MolangPath path) {
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
        return MolangValues.getElement(getVariableValue(path), index);
    }

    @Override
    public float getVariableElementNumber(MolangPath path, int index) {
        return MolangValues.getElementFloat(getVariableValue(path), index);
    }

    @Override
    public long getVariableElementOperand(MolangPath path, int index) {
        return MolangValues.getElementOperand(getVariableValue(path), index);
    }

    @Nullable
    private MolangStruct.BoundPath arrayTarget(MolangPath path) {
        MolangStruct.BoundPath variable = binding(path, owner.variables, variables);
        if (variable == null || variable.get() != null) {
            return variable;
        }
        MolangStruct.BoundPath array = binding(path, owner.arrays, arrays);
        return array.get() != null ? array : variable;
    }

    @Override
    public void setVariableElementValue(MolangPath path, int index, @Nullable Object value) {
        MolangStruct.BoundPath binding = arrayTarget(path);
        if (binding == null) {
            owner.setVariableElementValue(path, index, value);
        } else {
            Object old = binding.get();
            Object updated = MolangArrays.write(old, index, value);
            if (updated != old) {
                binding.setOwnedReference(updated);
            }
            owner.variableChanged();
        }
    }

    @Override
    public void setVariableElementNumber(MolangPath path, int index, float value) {
        MolangStruct.BoundPath binding = arrayTarget(path);
        if (binding == null) {
            owner.setVariableElementNumber(path, index, value);
        } else {
            Object old = binding.get();
            Object updated = MolangArrays.writeNumber(old, index, value);
            if (updated != old) {
                binding.setOwnedReference(updated);
            }
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
