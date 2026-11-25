package cn.nukkit.molang.runtime;

import cn.nukkit.molang.MolangExpression;

import java.util.List;
import javax.annotation.Nullable;

/**
 * 一个已编译程序所需的路径和查询，绑定数组的索引仅在该计划内有效。
 */
public final class MolangBindingPlan {
    public static final MolangBindingPlan EMPTY = new MolangBindingPlan(List.of(), List.of());

    private final List<MolangPath> paths;
    private final List<MolangQueryRef> queries;

    public MolangBindingPlan(List<MolangPath> paths, List<MolangQueryRef> queries) {
        this.paths = List.copyOf(paths);
        this.queries = List.copyOf(queries);
    }

    public int pathCount() {
        return paths.size();
    }

    public MolangPath path(int index) {
        return paths.get(index);
    }

    public int queryCount() {
        return queries.size();
    }

    public MolangQueryRef query(int index) {
        return queries.get(index);
    }

    /**
     * 查询该计划的绑定视图是否确实属于指定宿主，供共用求值作用域的批次验证。
     */
    public boolean matchesView(MolangRuntime runtime, MolangRuntime view) {
        return view instanceof MolangBoundRuntime bound && bound.matches(this, runtime);
    }

    /**
     * 为代码生成器绑定零参数数值回调，返回实际宿主。仅内建视图且全部查询合同都匹配时成功。
     */
    @Nullable
    public MolangRuntime linkNumberQueries(MolangRuntime runtime, int[] slots, MolangNumberContextQuery[] targets) {
        return runtime instanceof MolangBoundRuntime view ? view.linkNumberQueries(this, slots, targets) : null;
    }

    /**
     * 为整个新求值作用域中唯一的零参数数值读取链接宿主处理器。调用前需由编译器证明该合同。
     */
    @Nullable
    public MolangRuntime linkSingleEvaluationQuery(MolangRuntime runtime, int slot, MolangNumberContextQuery[] targets) {
        return runtime instanceof MolangBoundRuntime view ? view.linkSingleEvaluationQuery(this, slot, targets) : null;
    }

    /**
     * 填写当前宿主的直接变量槽号；尚未声明的变量保留为 -1。
     */
    public boolean linkVariables(MolangRuntime runtime, int[] paths, int[] slots) {
        return runtime instanceof MolangBoundRuntime view && view.linkVariables(this, paths, slots);
    }

    /**
     * 绑定持久变量的首段槽号。执行时仍从该槽读取当前子结构，父字段被替换后立即生效。
     */
    public boolean linkVariableRoots(MolangRuntime runtime, int[] paths, int[] slots) {
        return runtime instanceof MolangBoundRuntime view && view.linkVariables(this, paths, slots, true);
    }

    /**
     * 生成的绑定执行对象只读访问持久变量与同名数组；可变结构根只保存在私有字段。
     */
    @SuppressWarnings("unused")
    public abstract static class LinkedVariables {
        private final MolangStruct variables;
        private final MolangStruct arrays;

        protected LinkedVariables(MolangRuntime runtime) {
            if (!(runtime instanceof MolangRuntimeBuilder.MolangRuntimeImpl owner)) {
                throw new IllegalArgumentException("Expected a built-in Molang runtime");
            }
            variables = owner.variables;
            arrays = owner.arrays;
        }

        // 数值 getter 仅读变量根；绑定后才声明的路径也按相同规则重查。
        protected final float readNumber(int slot, MolangPath path) {
            return slot >= 0 ? variables.getNumber(slot) : variables.getFloat(path);
        }

        protected final long readOperand(int slot, MolangPath path) {
            long value = variables.getOperandOrMissing(slot);
            return value != MolangValues.MISSING_OPERAND ? value : missing(slot, path);
        }

        protected final long equalityBits(int slot, MolangPath path) {
            if (slot >= 0) {
                long bits = variables.equalityBitsOrMissing(slot);
                return bits != MolangValues.MISSING_OPERAND ? bits : MolangValues.equalityBits(arrays.get(path));
            }
            return MolangValues.equalityBits(equalityValue(slot, path));
        }

        @Nullable
        protected final Object equalityValue(int slot, MolangPath path) {
            Object value = slot >= 0 ? variables.getMember(slot) : variables.get(path);
            return value != null ? value : arrays.get(path);
        }

        protected final boolean equalsNumber(int slot, MolangPath path, float number) {
            long bits = equalityBits(slot, path);
            return bits >= 0 ? Float.intBitsToFloat((int) bits) == number : MolangValues.equalNumber(equalityValue(slot, path), number);
        }

        protected final float readNestedNumber(int rootSlot, MolangPath path) {
            return rootSlot >= 0 ? variables.nestedNumber(rootSlot, path) : variables.getFloat(path);
        }

        protected final long readNestedOperand(int rootSlot, MolangPath path) {
            long value = rootSlot >= 0 ? variables.nestedOperand(rootSlot, path) : variables.getOperandOrMissing(path);
            return value != MolangValues.MISSING_OPERAND ? value : arrays.getOperand(path);
        }

        private long missing(int slot, MolangPath path) {
            if (slot < 0) {
                long value = variables.getOperandOrMissing(path);
                if (value != MolangValues.MISSING_OPERAND) {
                    return value;
                }
            }
            return arrays.getOperand(path);
        }
    }

    @Nullable
    public MolangRuntime linkProperties(MolangRuntime runtime, int[] slots, MolangNumberContextQuery[] targets) {
        return runtime instanceof MolangBoundRuntime view ? view.linkProperties(this, slots, targets) : null;
    }

    /**
     * 已绑定的查询程序使用同一宿主调用处理器和管理求值作用域。属性纯读和由批调度管理的调用直接执行程序体。
     */
    @SuppressWarnings("unused")
    public abstract static class LinkedQueryExpression extends LinkedVariables implements MolangExpression {
        private final MolangRuntimeBuilder.MolangRuntimeImpl owner;
        private final boolean managesScope;

        protected LinkedQueryExpression(MolangRuntime runtime, boolean managesScope) {
            super(runtime);
            this.managesScope = managesScope;
            owner = (MolangRuntimeBuilder.MolangRuntimeImpl) runtime;
        }

        protected final float number1(MolangNumberContextQuery function, float argument) {
            return function.query(owner, argument);
        }

        protected final float number0(MolangNumberContextQuery function) {
            return function.query(owner, MolangValues.EMPTY_ARGUMENTS);
        }

        protected abstract float evaluateBody(MolangRuntime view);

        @Nullable
        protected abstract Object evaluateValueBody(MolangRuntime view);

        @Override
        public final float evaluate(MolangRuntime view) {
            if (!managesScope) {
                return evaluateBody(view);
            }
            int depth = owner.evaluationDepth();
            long resources = owner.scopeResources();
            owner.beginEvaluation();
            try {
                return evaluateBody(view);
            } finally {
                owner.finishLinkedEvaluation(depth, resources);
            }
        }

        @Override
        @Nullable
        public final Object evaluateValue(MolangRuntime view) {
            if (!managesScope) {
                return evaluateValueBody(view);
            }
            int depth = owner.evaluationDepth();
            long resources = owner.scopeResources();
            owner.beginEvaluation();
            try {
                return evaluateValueBody(view);
            } finally {
                owner.finishLinkedEvaluation(depth, resources);
            }
        }
    }

    @SuppressWarnings("unused")
    public static float invokeNumber1(MolangNumberContextQuery function, MolangRuntime owner, float argument) {
        return function.query(owner, argument);
    }

    /**
     * 生成代码按已验证的参数个数调用目标；单参数查询还需匹配实际宿主的完整合同。
     */
    @Nullable
    public MolangRuntime linkCompiledNumberQueries(MolangRuntime runtime, int[] slots, MolangNumberContextQuery[] targets) {
        return runtime instanceof MolangBoundRuntime view ? view.linkNumberQueries(this, slots, targets, true) : null;
    }

    @SuppressWarnings("unused")
    public static float invokeNumber0(MolangNumberContextQuery target, MolangRuntime owner) {
        return target.query(owner, MolangValues.EMPTY_ARGUMENTS);
    }
}
