package cn.nukkit.molang.runtime;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;

/**
 * 一个 runtime 内的查询绑定和有界深度缓存，多个编译程序共享同一份帧快照。
 */
class MolangQueryBinding implements QueryHandler {
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
    @Nullable
    private final MolangNumberContextQuery uncachedNumber0;
    private final int dependencies;
    private final boolean explicitContract;
    @Nullable
    private Cache frameCache;
    @Nullable
    private EvaluationCache evaluationCache;

    MolangQueryBinding(MolangRuntimeBuilder.MolangRuntimeImpl owner, MolangQueryDefinition definition, QueryHandler handler) {
        this(owner, definition, handler, null);
    }

    @Nullable
    final MolangNumberProperty.Cell property() {
        return this instanceof SampledNumber ? (MolangNumberProperty.Cell) numberFunction : null;
    }

    static MolangQueryBinding forHandler(MolangRuntimeBuilder.MolangRuntimeImpl owner, MolangQueryDefinition definition, QueryHandler handler) {
        if (definition.isReadOnly() && definition.minArguments() <= 1 && definition.maxArguments() >= 1 && definition.argumentType(0) == MolangQueryDefinition.ValueType.STRING) {
            return new StringArgument(owner, definition, handler);
        }
        return new MolangQueryBinding(owner, definition, handler);
    }

    private static final class StringArgument extends MolangQueryBinding {
        private StringArgument(MolangRuntimeBuilder.MolangRuntimeImpl owner, MolangQueryDefinition definition, QueryHandler handler) {
            super(owner, definition, handler);
        }

        @Override
        @Nullable
        public Object queryValue1(@Nullable Object argument) {
            if (!(argument instanceof String)) {
                super.checkArgument(0, argument);
            }
            return super.result(super.handler.queryValue1(argument));
        }
    }

    static MolangQueryBinding number(MolangRuntimeBuilder.MolangRuntimeImpl owner, MolangQueryDefinition definition, MolangNumberContextQuery function) {
        if (function instanceof MolangNumberProperty.Registration property) {
            MolangNumberProperty.Cell cell = property.bind(owner);
            if (cell.belongsTo(owner, definition)) {
                return new SampledNumber(owner, definition, cell);
            }
            function = cell;
        }
        if (definition.minArguments() == 0 && definition.isEvaluationStable() && dependencies(definition.reads()) == HOST) {
            return definition.stability() == MolangQueryDefinition.Stability.FRAME ? new FrameNumber(owner, definition, function) : new EvaluationNumber(owner, definition, function);
        }
        return definition.isReadOnlyUnaryNumber() ? new UnaryNumber(owner, definition, function) : new MolangQueryBinding(owner, definition, null, function);
    }

    private static final class UnaryNumber extends MolangQueryBinding {
        private UnaryNumber(MolangRuntimeBuilder.MolangRuntimeImpl owner, MolangQueryDefinition definition, MolangNumberContextQuery function) {
            super(owner, definition, null, function);
        }

        @Override
        public float queryNumber1(float argument) {
            return super.numberFunction.query(super.owner, argument);
        }

        private float read(@Nullable Object argument) {
            if (!(argument instanceof Number || argument instanceof Boolean)) {
                super.checkArgument(0, argument);
            }
            return super.numberFunction.query(super.owner, argument);
        }

        @Override
        public long queryOperand1(@Nullable Object argument) {
            return Integer.toUnsignedLong(Float.floatToRawIntBits(read(argument)));
        }

        @Override
        public Object queryValue1(@Nullable Object argument) {
            return read(argument);
        }
    }

    // 只有注册时核对过宿主与完整签名的属性才进入此路径；宿主已经存有最新样本。
    private static final class SampledNumber extends MolangQueryBinding {
        private SampledNumber(MolangRuntimeBuilder.MolangRuntimeImpl owner, MolangQueryDefinition definition, MolangNumberProperty.Cell cell) {
            super(owner, definition, null, cell);
        }

        @Override
        public float queryNumber0() {
            return super.numberFunction.query(super.owner, MolangValues.EMPTY_ARGUMENTS);
        }
    }

    private MolangQueryBinding(MolangRuntimeBuilder.MolangRuntimeImpl owner, MolangQueryDefinition definition, @Nullable QueryHandler handler, @Nullable MolangNumberContextQuery numberFunction) {
        this.owner = owner;
        this.definition = definition;
        this.handler = handler;
        this.numberFunction = numberFunction;
        uncachedNumber0 = definition.minArguments() == 0 && definition.isReadOnly() && definition.stability() == MolangQueryDefinition.Stability.NONE ? numberFunction : null;
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
        if (uncachedNumber0 != null) {
            return uncachedNumber0.query(owner, MolangValues.EMPTY_ARGUMENTS);
        }
        return checkedNumber0();
    }

    private float checkedNumber0() {
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
        EvaluationCache cache = evaluationCache;
        if (cache == null) {
            cache = new EvaluationCache();
            evaluationCache = cache;
        }
        return depth == 1 ? cache : cache.nested(depth - 2);
    }

    private boolean valid(Cache cache) {
        return cache.valid && cache.epoch == (cache.frame ? owner.frameEpoch : owner.evaluationId())
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
        cache.epoch = cache.frame ? owner.frameEpoch : owner.evaluationId();
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

    @Nullable
    final MolangNumberContextQuery linkedNumber1(MolangQueryDefinition expected) {
        return this instanceof UnaryNumber && !definition.nullable() && definition.equals(expected) ? numberFunction : null;
    }

    @Nullable
    final MolangNumberContextQuery linkedNumber0() {
        if (this instanceof SampledNumber) {
            return numberFunction;
        }
        if (uncachedNumber0 != null) {
            return uncachedNumber0;
        }
        return this instanceof FrameNumber frame ? frame : null;
    }

    @Nullable
    final MolangNumberContextQuery singleEvaluationTarget() {
        if (this instanceof SampledNumber && definition.stability() == MolangQueryDefinition.Stability.EVALUATION) {
            return numberFunction;
        }
        return definition.stability() == MolangQueryDefinition.Stability.EVALUATION && this instanceof EvaluationNumber binding ? binding::onlyNumber0 : null;
    }

    /**
     * 只依赖宿主的零参数数值查询在顶层求值中直接保存数值，内嵌作用域各自保存快照。
     */
    private static class EvaluationNumber extends MolangQueryBinding {
        private long epoch;
        private long hostVersion;
        private float number;
        private boolean valid;

        private EvaluationNumber(MolangRuntimeBuilder.MolangRuntimeImpl owner, MolangQueryDefinition definition, MolangNumberContextQuery function) {
            super(owner, definition, null, function);
        }

        final MolangRuntimeBuilder.MolangRuntimeImpl owner() {
            return super.owner;
        }

        final float sample(MolangRuntimeBuilder.MolangRuntimeImpl runtime) {
            return super.numberFunction.query(runtime, MolangValues.EMPTY_ARGUMENTS);
        }

        @Override
        public float queryNumber0() {
            MolangRuntimeBuilder.MolangRuntimeImpl runtime = owner();
            if (runtime.evaluationDepth() != 1) {
                return super.queryNumber0();
            }
            if (valid && runtime.topEvaluationMatches(epoch) && hostVersion == runtime.hostVersion) {
                return number;
            }
            float value = sample(runtime);
            number = value;
            epoch = runtime.evaluationId();
            hostVersion = runtime.hostVersion;
            valid = true;
            return value;
        }

        private float onlyNumber0(MolangRuntime unused, Object... arguments) {
            MolangRuntimeBuilder.MolangRuntimeImpl runtime = super.owner;
            if (runtime.evaluationDepth() != 1) {
                return queryNumber0();
            }
            float value = super.numberFunction.query(runtime, MolangValues.EMPTY_ARGUMENTS);
            // 宿主回调留下额外作用域时，调用方仍能观察根作用域，保留原查询完成时的缓存更新。
            if (runtime.evaluationDepth() != 1) {
                number = value;
                epoch = runtime.evaluationId();
                hostVersion = runtime.hostVersion;
                valid = true;
            }
            return value;
        }
    }

    private static final class FrameNumber extends EvaluationNumber implements MolangNumberContextQuery {
        private long epoch;
        private float value;

        private FrameNumber(MolangRuntimeBuilder.MolangRuntimeImpl owner, MolangQueryDefinition definition, MolangNumberContextQuery function) {
            super(owner, definition, function);
        }

        @Override
        public float query(MolangRuntime runtime, Object... arguments) {
            if (runtime == owner() && arguments.length == 0) {
                return queryNumber0();
            }
            // 此绑定也可被调用方作为回调传给另一个宿主，仍按原签名检查实参。
            return MolangValues.toFloat(queryValue(arguments));
        }

        @Override
        public float queryNumber0() {
            MolangRuntimeBuilder.MolangRuntimeImpl runtime = owner();
            if (runtime.frameDepth == 0) {
                return super.queryNumber0();
            }
            if (epoch != runtime.hostFrameEpoch) {
                value = sample(runtime);
                // 回调可能更新宿主或切换帧，仅在成功采样后保存当时的版本。
                epoch = runtime.hostFrameEpoch;
            }
            return value;
        }
    }

    private static final class EvaluationCache extends Cache {
        @Nullable
        private Cache[] levels;

        private EvaluationCache() {
            super(false);
        }

        private Cache nested(int index) {
            if (levels == null) {
                levels = new Cache[index + 1];
            } else if (index >= levels.length) {
                levels = Arrays.copyOf(levels, Math.max(index + 1, levels.length * 2));
            }
            Cache cache = levels[index];
            if (cache == null) {
                cache = new Cache(false);
                levels[index] = cache;
            }
            return cache;
        }
    }

    private static class Cache {
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
