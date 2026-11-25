package cn.nukkit.molang.compiler;

import cn.nukkit.molang.MolangExpression;
import cn.nukkit.molang.runtime.MolangNumberContextQuery;
import cn.nukkit.molang.runtime.MolangRuntime;
import cn.nukkit.molang.runtime.MolangRuntimeBuilder;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 引擎内部的短批次代码生成桥接。形态和构造工厂不持有宿主，绑定结果属于当前实体。
 */
public final class MolangBatchCompiler {
    interface UnboundProgram extends MolangExpression {
    }

    public interface Bound {
        void evaluate(float[] output);
    }

    public static final class Factory {
        private final Plan plan;
        private final Constructor constructor;

        Factory(Plan plan, Constructor constructor) {
            this.plan = plan;
            this.constructor = constructor;
        }
    }

    @FunctionalInterface
    interface Constructor {
        Bound bind(@Nullable Bindings bindings, MolangRuntime[] views, MolangRuntime runtime) throws Throwable;
    }

    public static final class Shape {
        private final Plan plan;

        private Shape(Plan plan) {
            this.plan = plan;
        }

        public Factory compile() {
            return new BatchCodeGenerator(plan).compile();
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof Shape shape && plan.equals(shape.plan);
        }

        @Override
        public int hashCode() {
            return plan.hashCode();
        }
    }

    public static Bound bind(List<MolangExpression> programs, MolangRuntime runtime, MolangRuntime[] views, Function<Shape, Factory> factoryForShape) {
        if (programs.size() < 2 || programs.size() > Integer.SIZE || programs.size() != views.length) {
            throw new IllegalArgumentException("Expected 2 to 32 Molang programs and their matching views");
        }
        List<MolangExpression> originals = List.copyOf(programs);
        // 后续会调用工厂解析器，整个绑定过程和最终执行均使用同一份私有视图快照。
        MolangRuntime[] snapshot = views.clone();
        boolean sameOwner = MolangRuntimeBuilder.hasBuiltInEvaluationScopes(runtime);
        for (int i = 0; i < originals.size(); i++) {
            MolangExpression program = originals.get(i);
            if (!TypedMolangCompiler.isUnboundProgram(program)) {
                throw new IllegalArgumentException("Expected an unbound compiled Molang program");
            }
            if (sameOwner && !program.bindingPlan().matchesView(runtime, snapshot[i])) {
                sameOwner = false;
            }
        }
        Bindings bindings = sameOwner ? new Bindings(snapshot.length) : null;
        Plan plan = Plan.select(originals, snapshot, bindings);
        Factory factory = factoryForShape.apply(new Shape(plan));
        if (!plan.equals(factory.plan)) {
            throw new IllegalArgumentException("Molang batch factory has a different program shape");
        }
        try {
            return factory.constructor.bind(bindings, snapshot, runtime);
        } catch (Throwable error) {
            throw new MolangCompileException("Failed to bind Molang batch dispatcher", error);
        }
    }

    record Bindings(MolangExpression[] targets, MolangNumberContextQuery[][] queries, int[][] variableSlots) {
        private static final int[] NO_VARIABLES = {};

        Bindings(int size) {
            this(new MolangExpression[size], new MolangNumberContextQuery[size][], new int[size][]);
        }

        BatchNumberPlan.Binding bindNumber(int index, BatchNumberPlan number, MolangRuntime view) {
            MolangNumberContextQuery[] callbacks = new MolangNumberContextQuery[number.queryCount()];
            int[] slots = number.pathCount() == 0 ? NO_VARIABLES : new int[number.pathCount()];
            BatchNumberPlan.Binding kind = number.bind(view, callbacks, slots);
            if (kind != BatchNumberPlan.Binding.UNAVAILABLE) {
                queries[index] = callbacks;
                variableSlots[index] = slots;
            }
            return kind;
        }
    }

    record Plan(List<MolangExpression> programs, int scopeMask, int linkedMask, int numberMask, int ownerMask, List<BatchNumberPlan> numbers) {
        private static final int MAX_NODES = 96;
        private static final int MAX_BYTES = 12_288;

        static Plan select(List<MolangExpression> originals, MolangRuntime[] views, @Nullable Bindings bindings) {
            if (bindings == null) {
                return new Plan(originals, 0, 0, 0, 0, List.of());
            }
            List<MolangExpression> bodies = new ArrayList<>(originals.size());
            int eligible = 0;
            for (int i = 0; i < originals.size(); i++) {
                MolangExpression body = TypedMolangCompiler.batchBody(originals.get(i));
                bodies.add(body);
                if (body != null) {
                    eligible |= 1 << i;
                }
            }
            int adjacent = eligible & ((eligible << 1) | (eligible >>> 1));
            int scopeMask = adjacent;
            int linkedMask = 0;
            int numberMask = 0;
            int ownerMask = 0;
            int remainingNodes = MAX_NODES;
            int remainingBytes = MAX_BYTES;
            List<BatchNumberPlan> numbers = new ArrayList<>();
            for (int i = 0; i < originals.size(); i++) {
                int bit = 1 << i;
                boolean managed = (adjacent & bit) != 0;
                MolangExpression program = originals.get(i);
                if (!managed) {
                    bodies.set(i, program);
                }
                MolangExpression ownerBody = TypedMolangCompiler.batchOwnerBody(program);
                if (ownerBody != null) {
                    bodies.set(i, ownerBody);
                    ownerMask |= bit;
                    continue;
                }
                BatchNumberPlan number = TypedMolangCompiler.batchNumber(program);
                if (number != null && number.nodeCount() <= remainingNodes && number.estimatedBytes() <= remainingBytes) {
                    BatchNumberPlan.Binding kind = bindings.bindNumber(i, number, views[i]);
                    if (kind != BatchNumberPlan.Binding.UNAVAILABLE) {
                        remainingNodes -= number.nodeCount();
                        remainingBytes -= number.estimatedBytes();
                        numbers.add(number);
                        numberMask |= bit;
                        // 只有全部输入链接到本宿主的采样属性时，才能省去这一项的求值域。
                        scopeMask = kind == BatchNumberPlan.Binding.SAMPLED ? scopeMask & ~bit : scopeMask | bit;
                        continue;
                    }
                }
                MolangExpression target = TypedMolangCompiler.linkBatchBody(program, views[i], managed);
                if (target != null) {
                    bindings.targets[i] = target;
                    linkedMask |= bit;
                }
            }
            List<MolangExpression> programs = adjacent == 0 && ownerMask == 0 ? originals : List.copyOf(bodies);
            return new Plan(programs, scopeMask, linkedMask, numberMask, ownerMask, List.copyOf(numbers));
        }

        @Nullable
        BatchNumberPlan number(int index) {
            int bit = 1 << index;
            return (numberMask & bit) == 0 ? null : numbers.get(Integer.bitCount(numberMask & (bit - 1)));
        }

        boolean hasHostFields() {
            return (linkedMask | numberMask | ownerMask) != 0;
        }

        boolean usesOwner(int index) {
            return (ownerMask & (1 << index)) != 0;
        }

        boolean linked(int index) {
            return (linkedMask & (1 << index)) != 0;
        }

        boolean scopes(int index) {
            return index >= 0 && index < programs.size() && (scopeMask & (1 << index)) != 0;
        }
    }

    private MolangBatchCompiler() {
    }
}
