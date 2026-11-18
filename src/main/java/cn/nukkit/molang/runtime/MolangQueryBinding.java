package cn.nukkit.molang.runtime;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * 一个 runtime 内的查询绑定和有界深度缓存，多个编译程序共享同一份帧快照。
 */
final class MolangQueryBinding implements QueryHandler {
    private static final int VARIABLES = 1;
    private static final int TEMPS = 2;
    private static final int CONTEXT = 4;
    private static final int HOST = 8;

    private final MolangRuntimeBuilder.MolangRuntimeImpl owner;
    private final MolangQueryDefinition definition;
    @Nullable
    private final QueryHandler handler;
    @Nullable
    private final MolangNumberContextQuery numberFunction;
    private final int dependencies;
    private final boolean explicitContract;
    @Nullable
    private Cache frameCache;
    @Nullable
    private List<Cache> evaluationCaches;

    MolangQueryBinding(MolangRuntimeBuilder.MolangRuntimeImpl owner, MolangQueryDefinition definition, QueryHandler handler) {
        this(owner, definition, handler, null);
    }

    MolangQueryBinding(MolangRuntimeBuilder.MolangRuntimeImpl owner, MolangQueryDefinition definition, MolangNumberContextQuery numberFunction) {
        this(owner, definition, null, numberFunction);
    }

    private MolangQueryBinding(MolangRuntimeBuilder.MolangRuntimeImpl owner, MolangQueryDefinition definition, @Nullable QueryHandler handler, @Nullable MolangNumberContextQuery numberFunction) {
        this.owner = owner;
        this.definition = definition;
        this.handler = handler;
        this.numberFunction = numberFunction;
        dependencies = dependencies(definition.reads());
        explicitContract = !definition.equals(MolangQueryDefinition.dynamic(definition.name()));
    }

    @Override
    public long queryOperand0() {
        return definition.returnType() == MolangQueryDefinition.ValueType.NUMBER
                ? Integer.toUnsignedLong(Float.floatToRawIntBits(queryNumber0()))
                : MolangValues.numericOperand(queryValue0());
    }

    @Override
    public long queryOperand1(@Nullable Object a) {
        if (definition.returnType() != MolangQueryDefinition.ValueType.NUMBER) {
            return MolangValues.numericOperand(queryValue1(a));
        }
        checkArguments(1);
        checkArgument(0, a);
        before();
        try {
            float number;
            if (numberFunction != null) {
                number = numberFunction.query(owner, a);
            } else {
                Object value = handler.queryValue1(a);
                validateResult(value);
                number = MolangValues.toFloat(value);
            }
            return Integer.toUnsignedLong(Float.floatToRawIntBits(number));
        } finally {
            after();
        }
    }

    @Override
    public long queryOperand2(@Nullable Object a, @Nullable Object b) {
        if (definition.returnType() != MolangQueryDefinition.ValueType.NUMBER) {
            return MolangValues.numericOperand(queryValue2(a, b));
        }
        checkArguments(2);
        checkArgument(0, a);
        checkArgument(1, b);
        before();
        try {
            float number;
            if (numberFunction != null) {
                number = numberFunction.query(owner, a, b);
            } else {
                Object value = handler.queryValue2(a, b);
                validateResult(value);
                number = MolangValues.toFloat(value);
            }
            return Integer.toUnsignedLong(Float.floatToRawIntBits(number));
        } finally {
            after();
        }
    }

    @Override
    public long queryOperand3(@Nullable Object a, @Nullable Object b, @Nullable Object c) {
        if (definition.returnType() != MolangQueryDefinition.ValueType.NUMBER) {
            return MolangValues.numericOperand(queryValue3(a, b, c));
        }
        checkArguments(3);
        checkArgument(0, a);
        checkArgument(1, b);
        checkArgument(2, c);
        before();
        try {
            float number;
            if (numberFunction != null) {
                number = numberFunction.query(owner, a, b, c);
            } else {
                Object value = handler.queryValue3(a, b, c);
                validateResult(value);
                number = MolangValues.toFloat(value);
            }
            return Integer.toUnsignedLong(Float.floatToRawIntBits(number));
        } finally {
            after();
        }
    }

    private void checkArguments(int count) {
        if (count < definition.minArguments() || count > definition.maxArguments()) {
            throw new IllegalArgumentException("Invalid argument count for query " + definition.name() + ": " + count);
        }
    }

    private void checkArgument(int index, @Nullable Object value) {
        MolangQueryDefinition.ValueType type = definition.argumentType(index);
        if (!matches(type, value)) {
            throw new IllegalArgumentException("Invalid argument type for query " + definition.name() + " at " + index + ": " + type);
        }
    }

    private void checkValues(Object[] args) {
        checkArguments(args.length);
        for (int index = 0; index < args.length; index++) {
            checkArgument(index, args[index]);
        }
    }

    @Nullable
    private Object result(@Nullable Object value) {
        validateResult(value);
        if (value == null || value instanceof Float || definition.returnType() != MolangQueryDefinition.ValueType.NUMBER) {
            return value;
        }
        return MolangValues.toFloat(value);
    }

    private void validateResult(@Nullable Object value) {
        if (value == null) {
            if (!definition.nullable()) {
                throw new IllegalArgumentException("Query returned a missing non-nullable value: " + definition.name());
            }
            return;
        }
        if (!matches(definition.returnType(), value)) {
            throw new IllegalArgumentException("Invalid return type for query " + definition.name() + ": " + definition.returnType());
        }
    }

    private static boolean matches(MolangQueryDefinition.ValueType type, @Nullable Object value) {
        return switch (type) {
            case DYNAMIC -> true;
            case NUMBER -> value instanceof Number || value instanceof Boolean;
            case STRING -> value instanceof String;
            case STRUCT -> value instanceof MolangStruct;
            case ENTITY -> value instanceof MolangEntityReference;
            case ARRAY -> MolangValues.isArray(value);
        };
    }

    private void before() {
        if (!definition.isReadOnly()) {
            owner.invalidateQueries();
        }
    }

    private void after() {
        if (!definition.isReadOnly()) {
            owner.invalidateQueries();
        }
    }

    @Override
    @Nullable
    public Object queryValue(Object... args) {
        if (args.length == 0) {
            return queryValue0();
        }
        checkValues(args);
        before();
        try {
            return numberFunction != null ? numberFunction.query(owner, args) : result(handler.queryValue(args));
        } finally {
            after();
        }
    }

    @Override
    @Nullable
    public Object queryValue0() {
        if (numberFunction != null) {
            return queryNumber0();
        }
        checkArguments(0);
        Cache cache = cache();
        if (cache != null && valid(cache)) {
            return definition.returnType() == MolangQueryDefinition.ValueType.NUMBER
                    ? cache.missing ? null : cache.number : cache.value;
        }
        before();
        try {
            Object value = result(handler.queryValue0());
            if (cache != null) {
                rememberValue(cache, value);
            }
            return value;
        } finally {
            after();
        }
    }

    @Override
    public float queryNumber0() {
        checkArguments(0);
        Cache cache = cache();
        if (cache != null && valid(cache)) {
            return definition.returnType() == MolangQueryDefinition.ValueType.NUMBER ? cache.number : MolangValues.toFloat(cache.value);
        }
        before();
        try {
            if (numberFunction != null) {
                float value = numberFunction.query(owner, MolangValues.EMPTY_ARGUMENTS);
                if (cache != null) {
                    rememberNumber(cache, value, false);
                }
                return value;
            }
            if (!explicitContract) {
                return handler.queryNumber0();
            }
            Object value = handler.queryValue0();
            validateResult(value);
            float number = MolangValues.toFloat(value);
            if (cache != null) {
                if (definition.returnType() == MolangQueryDefinition.ValueType.NUMBER) {
                    rememberNumber(cache, number, value == null);
                } else {
                    cache.value = value;
                    remember(cache);
                }
            }
            return number;
        } finally {
            after();
        }
    }

    @Override
    @Nullable
    public Object queryValue1(@Nullable Object a) {
        checkArguments(1);
        checkArgument(0, a);
        before();
        try {
            return numberFunction != null ? numberFunction.query(owner, a) : result(handler.queryValue1(a));
        } finally {
            after();
        }
    }

    @Override
    public float queryNumber1(float a) {
        checkArguments(1);
        checkArgument(0, a);
        before();
        try {
            if (numberFunction != null) {
                return numberFunction.query(owner, a);
            }
            if (explicitContract) {
                Object value = handler.queryValue1(a);
                validateResult(value);
                return MolangValues.toFloat(value);
            }
            return handler.queryNumber1(a);
        } finally {
            after();
        }
    }

    @Override
    @Nullable
    public Object queryValue2(@Nullable Object a, @Nullable Object b) {
        checkArguments(2);
        checkArgument(0, a);
        checkArgument(1, b);
        before();
        try {
            return numberFunction != null ? numberFunction.query(owner, a, b) : result(handler.queryValue2(a, b));
        } finally {
            after();
        }
    }

    @Override
    public float queryNumber2(float a, float b) {
        checkArguments(2);
        checkArgument(0, a);
        checkArgument(1, b);
        before();
        try {
            if (numberFunction != null) {
                return numberFunction.query(owner, a, b);
            }
            if (explicitContract) {
                Object value = handler.queryValue2(a, b);
                validateResult(value);
                return MolangValues.toFloat(value);
            }
            return handler.queryNumber2(a, b);
        } finally {
            after();
        }
    }

    @Override
    @Nullable
    public Object queryValue3(@Nullable Object a, @Nullable Object b, @Nullable Object c) {
        checkArguments(3);
        checkArgument(0, a);
        checkArgument(1, b);
        checkArgument(2, c);
        before();
        try {
            return numberFunction != null ? numberFunction.query(owner, a, b, c) : result(handler.queryValue3(a, b, c));
        } finally {
            after();
        }
    }

    @Override
    public float queryNumber3(float a, float b, float c) {
        checkArguments(3);
        checkArgument(0, a);
        checkArgument(1, b);
        checkArgument(2, c);
        before();
        try {
            if (numberFunction != null) {
                return numberFunction.query(owner, a, b, c);
            }
            if (explicitContract) {
                Object value = handler.queryValue3(a, b, c);
                validateResult(value);
                return MolangValues.toFloat(value);
            }
            return handler.queryNumber3(a, b, c);
        } finally {
            after();
        }
    }

    @Nullable
    private Cache cache() {
        if (!definition.isEvaluationStable()) {
            return null;
        }
        if (definition.stability() == MolangQueryDefinition.Stability.FRAME && owner.frameDepth > 0) {
            if (frameCache == null) {
                frameCache = new Cache(true);
            }
            return frameCache;
        }
        int depth = owner.evaluationDepth();
        if (depth == 0) {
            return null;
        }
        if (evaluationCaches == null) {
            evaluationCaches = new ArrayList<>();
        }
        while (evaluationCaches.size() < depth) {
            evaluationCaches.add(new Cache(false));
        }
        return evaluationCaches.get(depth - 1);
    }

    private boolean valid(Cache cache) {
        return cache.valid && cache.epoch == (cache.frame ? owner.frameEpoch : owner.evaluationId)
                && ((dependencies & VARIABLES) == 0 || cache.variables == owner.variableVersion)
                && ((dependencies & TEMPS) == 0 || cache.temps == owner.tempVersion)
                && ((dependencies & CONTEXT) == 0 || cache.context == owner.contextVersion)
                && ((dependencies & HOST) == 0 || cache.host == owner.hostVersion);
    }

    private void rememberNumber(Cache cache, float number, boolean missing) {
        cache.number = number;
        cache.missing = missing;
        remember(cache);
    }

    private void rememberValue(Cache cache, @Nullable Object value) {
        if (definition.returnType() == MolangQueryDefinition.ValueType.NUMBER) {
            cache.number = MolangValues.toFloat(value);
            cache.missing = value == null;
        } else {
            cache.value = value;
        }
        remember(cache);
    }

    private void remember(Cache cache) {
        cache.epoch = cache.frame ? owner.frameEpoch : owner.evaluationId;
        cache.variables = owner.variableVersion;
        cache.temps = owner.tempVersion;
        cache.context = owner.contextVersion;
        cache.host = owner.hostVersion;
        cache.valid = true;
    }

    private static int dependencies(Set<String> names) {
        int mask = 0;
        for (String dependency : names) {
            String name = dependency.toLowerCase(Locale.ROOT);
            if (name.equals("*")) {
                return VARIABLES | TEMPS | CONTEXT | HOST;
            }
            if (name.equals("variable") || name.startsWith("variable.") || name.startsWith("v.")) {
                mask |= VARIABLES;
            } else if (name.equals("temp") || name.startsWith("temp.") || name.startsWith("t.")) {
                mask |= TEMPS;
            } else if (name.equals("context") || name.startsWith("context.") || name.startsWith("c.")) {
                mask |= CONTEXT;
            } else {
                mask |= HOST;
            }
        }
        return mask;
    }

    private static final class Cache {
        private final boolean frame;
        private boolean valid;
        private boolean missing;
        private long epoch;
        private long variables;
        private long temps;
        private long context;
        private long host;
        private float number;
        @Nullable
        private Object value;

        private Cache(boolean frame) {
            this.frame = frame;
        }
    }
}
