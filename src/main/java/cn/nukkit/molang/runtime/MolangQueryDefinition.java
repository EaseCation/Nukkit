package cn.nukkit.molang.runtime;

import java.util.*;

/**
 * 宿主明确声明的查询合同。未声明合同的查询按动态、有副作用的调用处理。
 */
public record MolangQueryDefinition(
        String name,
        ValueType returnType,
        int minArguments,
        int maxArguments,
        Effect effect,
        Stability stability,
        Set<String> reads,
        Set<String> writes,
        List<ValueType> argumentTypes,
        boolean nullable
) {
    public MolangQueryDefinition(String name, ValueType returnType, int minArguments, int maxArguments, Effect effect, Stability stability, Set<String> reads, Set<String> writes) {
        this(name, returnType, minArguments, maxArguments, effect, stability, reads, writes, List.of(), true);
    }

    public MolangQueryDefinition(String name, ValueType returnType, int minArguments, int maxArguments, Effect effect, Stability stability, Set<String> reads, Set<String> writes, List<ValueType> argumentTypes) {
        this(name, returnType, minArguments, maxArguments, effect, stability, reads, writes, argumentTypes, true);
    }

    public MolangQueryDefinition {
        name = Objects.requireNonNull(name, "name").toLowerCase(Locale.ROOT);
        Objects.requireNonNull(returnType, "returnType");
        Objects.requireNonNull(effect, "effect");
        Objects.requireNonNull(stability, "stability");
        reads = normalizeDependencies(reads);
        writes = normalizeDependencies(writes);
        argumentTypes = List.copyOf(argumentTypes);
        if (name.isEmpty() || minArguments < 0 || maxArguments < minArguments) {
            throw new IllegalArgumentException("Invalid Molang query signature: " + name);
        }
        if (effect != Effect.IMPURE && !writes.isEmpty()) {
            throw new IllegalArgumentException("Read-only queries cannot declare writes: " + name);
        }
        if (effect == Effect.IMPURE && stability != Stability.NONE) {
            throw new IllegalArgumentException("Impure queries cannot cache results: " + name);
        }
    }

    public static MolangQueryDefinition dynamic(String name) {
        return new MolangQueryDefinition(name, ValueType.DYNAMIC, 0, Integer.MAX_VALUE, Effect.IMPURE, Stability.NONE, Set.of("*"), Set.of("*"));
    }

    public static MolangQueryDefinition number(String name, int arguments, Effect effect, Stability stability) {
        return new MolangQueryDefinition(name, ValueType.NUMBER, arguments, arguments, effect, stability,
                effect == Effect.PURE ? Set.of() : Set.of("host.*"),
                effect == Effect.IMPURE ? Set.of("*") : Set.of(), List.of(), false);
    }

    public boolean isPure() {
        return effect == Effect.PURE;
    }

    public MolangQueryDefinition withArguments(ValueType... types) {
        return new MolangQueryDefinition(name, returnType, minArguments, maxArguments, effect, stability, reads, writes, List.of(types), nullable);
    }

    public MolangQueryDefinition withNullable(boolean nullable) {
        return new MolangQueryDefinition(name, returnType, minArguments, maxArguments, effect, stability, reads, writes, argumentTypes, nullable);
    }

    /**
     * 可变参数查询重复最后一个已声明的参数类型；未声明时保留动态参数语义。
     */
    public ValueType argumentType(int index) {
        return argumentTypes.isEmpty() ? ValueType.DYNAMIC : argumentTypes.get(Math.min(index, argumentTypes.size() - 1));
    }

    public boolean isReadOnly() {
        return effect != Effect.IMPURE;
    }

    public boolean isReadOnlyUnaryNumber() {
        if (!isReadOnly() || minArguments != 1 || maxArguments != 1 || returnType != ValueType.NUMBER) {
            return false;
        }
        ValueType argument = argumentType(0);
        return argument == ValueType.NUMBER || argument == ValueType.DYNAMIC;
    }

    public boolean isEvaluationStable() {
        return isReadOnly() && stability != Stability.NONE;
    }

    public boolean mayObserveTemps() {
        return reads.stream().anyMatch(MolangQueryDefinition::isTempDependency);
    }

    public boolean mayWriteTemps() {
        return writes.stream().anyMatch(MolangQueryDefinition::isTempDependency);
    }

    private static boolean isTempDependency(String name) {
        return name.equals("*") || name.equals("temp") || name.startsWith("temp.") || name.startsWith("t.");
    }

    private static Set<String> normalizeDependencies(Set<String> dependencies) {
        Set<String> result = new HashSet<>();
        for (String dependency : dependencies) {
            String normalized = dependency.toLowerCase(Locale.ROOT);
            if (normalized.equals("v") || normalized.startsWith("v.")) {
                normalized = "variable" + normalized.substring(1);
            } else if (normalized.equals("t") || normalized.startsWith("t.")) {
                normalized = "temp" + normalized.substring(1);
            } else if (normalized.equals("c") || normalized.startsWith("c.")) {
                normalized = "context" + normalized.substring(1);
            }
            if (normalized.isEmpty()) {
                throw new IllegalArgumentException("Molang query dependency cannot be empty");
            }
            result.add(normalized);
        }
        return Set.copyOf(result);
    }

    public enum ValueType {
        NUMBER,
        STRING,
        STRUCT,
        ENTITY,
        ARRAY,
        DYNAMIC,
    }

    public enum Effect {
        PURE,
        READ_ONLY,
        IMPURE,
    }

    public enum Stability {
        NONE,
        EVALUATION,
        FRAME,
    }
}
