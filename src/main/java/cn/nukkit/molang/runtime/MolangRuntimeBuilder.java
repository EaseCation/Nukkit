package cn.nukkit.molang.runtime;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntStack;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongStack;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MolangRuntimeBuilder {
    private final MolangStruct variables = new MolangStruct();
    private final Map<String, QueryHandler> queries = new HashMap<>();
    private final Map<String, MolangContextQuery> contextQueries = new HashMap<>();
    private final Map<String, MolangNumberContextQuery> numberQueries = new HashMap<>();
    private final Map<String, MolangQueryDefinition> definitions = new HashMap<>();
    private final MolangStruct contextValues = new MolangStruct();
    private final MolangStruct arrays = new MolangStruct();
    private float thisValue = 0;
    private MolangLogger logger = MolangLogger.DEFAULT;
    private boolean strictQueries;
    @Nullable
    private Object host;
    @Nullable
    private QueryRegistry cachedRegistry;

    public MolangRuntimeBuilder declareVariables(String... names) {
        variables.declareMembers(List.of(names));
        return this;
    }

    public MolangRuntimeBuilder declareContext(String... names) {
        contextValues.declareMembers(List.of(names));
        return this;
    }

    public MolangRuntimeBuilder host(@Nullable Object host) {
        this.host = host;
        return this;
    }

    public MolangRuntimeBuilder variable(String name, float value) {
        variables.set(name, value);
        return this;
    }

    public MolangRuntimeBuilder variable(String name, @Nullable Object value) {
        variables.set(name, value);
        return this;
    }

    public MolangRuntimeBuilder query(String name, QueryHandler handler) {
        String normalized = name.toLowerCase(Locale.ROOT);
        queries.put(normalized, handler);
        contextQueries.remove(normalized);
        numberQueries.remove(normalized);
        definitions.put(normalized, MolangQueryDefinition.dynamic(normalized));
        cachedRegistry = null;
        return this;
    }

    public MolangRuntimeBuilder query(MolangQueryDefinition definition, QueryHandler handler) {
        queries.put(definition.name(), handler);
        contextQueries.remove(definition.name());
        numberQueries.remove(definition.name());
        definitions.put(definition.name(), definition);
        cachedRegistry = null;
        return this;
    }

    public MolangRuntimeBuilder query(MolangQueryDefinition definition, MolangContextQuery function) {
        queries.remove(definition.name());
        contextQueries.put(definition.name(), function);
        numberQueries.remove(definition.name());
        definitions.put(definition.name(), definition);
        cachedRegistry = null;
        return this;
    }

    public MolangRuntimeBuilder queryNumber(MolangQueryDefinition definition, MolangNumberContextQuery function) {
        if (definition.returnType() != MolangQueryDefinition.ValueType.NUMBER || definition.nullable()) {
            throw new IllegalArgumentException("Primitive queries require a non-nullable NUMBER signature");
        }
        if (function == null) {
            throw new IllegalArgumentException("Primitive query function is required");
        }
        queries.remove(definition.name());
        contextQueries.remove(definition.name());
        numberQueries.put(definition.name(), function);
        definitions.put(definition.name(), definition);
        cachedRegistry = null;
        return this;
    }

    public MolangRuntimeBuilder query(String name, float constantValue) {
        return query(name, _ -> constantValue);
    }

    public MolangRuntimeBuilder context(String name, float value) {
        contextValues.set(name, value);
        return this;
    }

    public MolangRuntimeBuilder context(String name, @Nullable Object value) {
        contextValues.set(name, value);
        return this;
    }

    public MolangRuntimeBuilder thisValue(float value) {
        this.thisValue = value;
        return this;
    }

    public MolangRuntimeBuilder array(String name, float[] values) {
        arrays.set(name, values);
        return this;
    }

    public MolangRuntimeBuilder array(String name, Object[] values) {
        arrays.set(name, values);
        return this;
    }

    public MolangRuntimeBuilder dynamicArray(String name, float... values) {
        arrays.setOwnedReference(name, MolangArray.numbers(values));
        return this;
    }

    public MolangRuntimeBuilder dynamicReferenceArray(String name, Object... values) {
        arrays.setOwnedReference(name, MolangArray.references(values));
        return this;
    }

    public MolangRuntimeBuilder logger(MolangLogger logger) {
        this.logger = logger;
        return this;
    }

    public MolangRuntimeBuilder strictQueries(boolean strictQueries) {
        this.strictQueries = strictQueries;
        return this;
    }

    public MolangRuntime build() {
        return new MolangRuntimeImpl(variables.copy(), registry(), contextValues.copy(), arrays.copy(), thisValue, logger, strictQueries, host);
    }

    public MolangRuntimeTemplate template() {
        MolangStruct variableDefaults = variables.copy();
        MolangStruct contextDefaults = contextValues.copy();
        MolangStruct arrayDefaults = arrays.copy();
        QueryRegistry registry = registry();
        float initialThis = thisValue;
        MolangLogger sharedLogger = logger;
        boolean strict = strictQueries;
        return new MolangRuntimeTemplate(instanceHost -> new MolangRuntimeImpl(variableDefaults.copy(), registry, contextDefaults.copy(), arrayDefaults.copy(), initialThis, sharedLogger, strict, instanceHost), host);
    }

    private Map<String, QueryHandler> immutableQueries() {
        // 该快照只保存在私有注册表中，创建后不再修改或向外暴露，避免热查询多一层包装。
        return new HashMap<>(queries);
    }

    private QueryRegistry registry() {
        QueryRegistry cached = cachedRegistry;
        if (cached != null) {
            return cached;
        }
        MolangQuerySchema schema = new MolangQuerySchema(definitions);
        boolean tracks = schema.definitions().values().stream().anyMatch(definition -> definition.minArguments() == 0 && definition.isEvaluationStable());
        boolean boundCalls = tracks || !contextQueries.isEmpty() || !numberQueries.isEmpty() || schema.definitions().values().stream()
                .anyMatch(definition -> !definition.equals(MolangQueryDefinition.dynamic(definition.name())));
        cached = new QueryRegistry(immutableQueries(), Map.copyOf(contextQueries), Map.copyOf(numberQueries), schema, tracks, boundCalls);
        cachedRegistry = cached;
        return cached;
    }

    private record QueryRegistry(Map<String, QueryHandler> handlers,
                                 Map<String, MolangContextQuery> functions,
                                 Map<String, MolangNumberContextQuery> numberFunctions,
                                 MolangQuerySchema schema,
                                 boolean tracksChanges,
                                 boolean boundCalls) {
    }

    static final class MolangRuntimeImpl implements MolangRuntime {
        private static final QueryHandler UNKNOWN_QUERY = _ -> 0f;

        @Override
        @Nullable
        public Object queryValue0(MolangQueryRef query) {
            return resolveCompiledQuery(query).queryValue0();
        }

        @Override
        public float queryNumber0(MolangQueryRef query) {
            return resolveCompiledQuery(query).queryNumber0();
        }

        @Override
        @Nullable
        public Object queryValue1(MolangQueryRef query, @Nullable Object a) {
            return resolveCompiledQuery(query).queryValue1(a);
        }

        @Override
        public float queryNumber1(MolangQueryRef query, float a) {
            return resolveCompiledQuery(query).queryNumber1(a);
        }

        @Override
        @Nullable
        public Object queryValue2(MolangQueryRef query, @Nullable Object a, @Nullable Object b) {
            return resolveCompiledQuery(query).queryValue2(a, b);
        }

        @Override
        public float queryNumber2(MolangQueryRef query, float a, float b) {
            return resolveCompiledQuery(query).queryNumber2(a, b);
        }

        @Override
        @Nullable
        public Object queryValue3(MolangQueryRef query, @Nullable Object a, @Nullable Object b, @Nullable Object c) {
            return resolveCompiledQuery(query).queryValue3(a, b, c);
        }

        @Override
        public float queryNumber3(MolangQueryRef query, float a, float b, float c) {
            return resolveCompiledQuery(query).queryNumber3(a, b, c);
        }

        @Override
        @Nullable
        public Object queryValue(MolangQueryRef query, Object... args) {
            return resolveCompiledQuery(query).queryValue(args);
        }

        private QueryHandler resolveCompiledQuery(MolangQueryRef query) {
            return useQueryBindings ? resolveDeclaredQuery(query.name()) : resolveDynamicQuery(query.name());
        }


        @Override
        public long queryOperand0(String name) {
            return resolveQuery(name).queryOperand0();
        }

        @Override
        public long queryOperand0(MolangQueryRef query) {
            return resolveCompiledQuery(query).queryOperand0();
        }

        @Override
        public long queryOperand1(String name, @Nullable Object a) {
            return resolveQuery(name).queryOperand1(a);
        }

        @Override
        public long queryOperand1(MolangQueryRef query, @Nullable Object a) {
            return resolveCompiledQuery(query).queryOperand1(a);
        }

        @Override
        public long queryOperand2(String name, @Nullable Object a, @Nullable Object b) {
            return resolveQuery(name).queryOperand2(a, b);
        }

        @Override
        public long queryOperand2(MolangQueryRef query, @Nullable Object a, @Nullable Object b) {
            return resolveCompiledQuery(query).queryOperand2(a, b);
        }

        @Override
        public long queryOperand3(String name, @Nullable Object a, @Nullable Object b, @Nullable Object c) {
            return resolveQuery(name).queryOperand3(a, b, c);
        }

        @Override
        public long queryOperand3(MolangQueryRef query, @Nullable Object a, @Nullable Object b, @Nullable Object c) {
            return resolveCompiledQuery(query).queryOperand3(a, b, c);
        }

        @Override
        public float getVariableNumber(MolangPath path) {
            return variables.getFloat(path);
        }

        @Override
        public long getVariableOperand(MolangPath path) {
            long value = variables.getOperandOrMissing(path);
            return value == MolangValues.MISSING_OPERAND ? arrays.getOperand(path) : value;
        }

        @Override
        @Nullable
        public Object getVariableValue(MolangPath path) {
            Object value = variables.get(path);
            return value != null ? value : arrays.get(path);
        }

        @Override
        public void setVariableNumber(MolangPath path, float value) {
            variables.set(path, value);
            variableChanged();
        }

        @Override
        public void setVariableValue(MolangPath path, @Nullable Object value) {
            variables.set(path, value);
            variableChanged();
        }

        @Override
        public void copyVariableValue(MolangPath target, MolangPath source) {
            if (!variables.copyNumber(target, source)) {
                variables.set(target, getVariableValue(source));
            }
            variableChanged();
        }

        @Override
        public float getTempNumber(MolangPath path) {
            ensureTempScope();
            return temps.getFloat(path);
        }

        @Override
        public long getTempOperand(MolangPath path) {
            ensureTempScope();
            return temps.getOperand(path);
        }

        @Override
        @Nullable
        public Object getTempValue(MolangPath path) {
            ensureTempScope();
            return temps.get(path);
        }

        @Override
        public void setTempNumber(MolangPath path, float value) {
            ensureTempScope();
            temps.set(path, value);
            tempChanged();
        }

        @Override
        public void setTempValue(MolangPath path, @Nullable Object value) {
            ensureTempScope();
            temps.set(path, value);
            tempChanged();
        }

        @Override
        public float getContextNumber(MolangPath path) {
            return contextValues.getFloat(path);
        }

        @Override
        public long getContextOperand(MolangPath path) {
            return contextValues.getOperand(path);
        }

        @Override
        @Nullable
        public Object getContextValue(MolangPath path) {
            return contextValues.get(path);
        }

        @Override
        public void setContextValue(MolangPath path, @Nullable Object value) {
            contextValues.set(path, value);
            contextChanged();
        }

        @Override
        public void setContextNumber(MolangPath path, float value) {
            contextValues.set(path, value);
            contextChanged();
        }

        @Override
        @Nullable
        public Object getArrayValue(MolangPath path) {
            Object value = arrays.get(path);
            return value != null ? value : variables.get(path);
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

        @Override
        public void setVariableElementValue(MolangPath path, int index, @Nullable Object value) {
            MolangStruct storage = variables.get(path) != null || arrays.get(path) == null ? variables : arrays;
            setElementValue(storage, path, index, value);
            variableChanged();
        }

        @Override
        public void setVariableElementNumber(MolangPath path, int index, float value) {
            MolangStruct storage = variables.get(path) != null || arrays.get(path) == null ? variables : arrays;
            setElementNumber(storage, path, index, value);
            variableChanged();
        }

        @Override
        public void setTempElementValue(MolangPath path, int index, @Nullable Object value) {
            ensureTempScope();
            setElementValue(temps, path, index, value);
            tempChanged();
        }

        @Override
        public void setTempElementNumber(MolangPath path, int index, float value) {
            ensureTempScope();
            setElementNumber(temps, path, index, value);
            tempChanged();
        }


        @Override
        public boolean supportsStableVariableReads() {
            return true;
        }

        @Override
        public boolean supportsLocalTempAccess() {
            return true;
        }

        @Override
        public float getVariableNumber(String name) {
            return variables.getFloat(name);
        }

        @Override
        public void setVariableNumber(String name, float value) {
            variables.set(name, value);
            variableChanged();
        }

        @Override
        public float getTempNumber(String name) {
            ensureTempScope();
            return temps.getFloat(name);
        }

        @Override
        public void setTempNumber(String name, float value) {
            ensureTempScope();
            temps.set(name, value);
            tempChanged();
        }

        @Override
        public float getContextNumber(String name) {
            return contextValues.getFloat(name);
        }

        @Override
        @Nullable
        public Object queryValue0(String name) {
            return resolveQuery(name).queryValue0();
        }

        @Override
        public float queryNumber0(String name) {
            return resolveQuery(name).queryNumber0();
        }

        @Override
        @Nullable
        public Object queryValue1(String name, @Nullable Object a) {
            return resolveQuery(name).queryValue1(a);
        }

        @Override
        public float queryNumber1(String name, float a) {
            return resolveQuery(name).queryNumber1(a);
        }

        @Override
        @Nullable
        public Object queryValue2(String name, @Nullable Object a, @Nullable Object b) {
            return resolveQuery(name).queryValue2(a, b);
        }

        @Override
        public float queryNumber2(String name, float a, float b) {
            return resolveQuery(name).queryNumber2(a, b);
        }

        @Override
        @Nullable
        public Object queryValue3(String name, @Nullable Object a, @Nullable Object b, @Nullable Object c) {
            return resolveQuery(name).queryValue3(a, b, c);
        }

        @Override
        public float queryNumber3(String name, float a, float b, float c) {
            return resolveQuery(name).queryNumber3(a, b, c);
        }


        final MolangStruct variables;
        private final Map<String, QueryHandler> queries;
        private final Map<String, MolangContextQuery> contextQueries;
        private final Map<String, MolangNumberContextQuery> numberQueries;
        private final Map<String, MolangQueryBinding> queryBindings = new HashMap<>();
        private final MolangQuerySchema schema;
        private final boolean useQueryBindings;
        final MolangStruct contextValues;
        final MolangStruct arrays;
        private final Deque<MolangStruct> tempScopes = new ArrayDeque<>();
        private final IntStack tempDepths = new IntArrayList();
        private final Deque<MolangStruct> availableTemps = new ArrayDeque<>();
        private MolangStruct temps = new MolangStruct();
        private int evaluationDepth;
        private int tempDepth;
        private final LongStack evaluationIds = new LongArrayList();
        private long nextEvaluationId;
        long evaluationId;
        long frameEpoch;
        int frameDepth;
        private long frameId;
        private boolean hasFrame;
        long variableVersion;
        long tempVersion;
        long contextVersion;
        long hostVersion;
        private final boolean tracksQueryChanges;
        @Nullable
        private final Object host;
        private final float thisValue;
        private final MolangLogger logger;
        private final boolean strictQueries;

        MolangRuntimeImpl(MolangStruct variables, QueryRegistry registry, MolangStruct contextValues, MolangStruct arrays, float thisValue, MolangLogger logger, boolean strictQueries, @Nullable Object host) {
            this.variables = variables;
            this.schema = registry.schema();
            this.queries = registry.handlers();
            this.contextQueries = registry.functions();
            this.numberQueries = registry.numberFunctions();
            this.useQueryBindings = registry.boundCalls();
            this.contextValues = contextValues;
            this.arrays = arrays;
            this.thisValue = thisValue;
            this.logger = logger;
            this.strictQueries = strictQueries;
            this.host = host;
            tracksQueryChanges = registry.tracksChanges();
        }

        @Override
        public MolangQuerySchema querySchema() {
            return schema;
        }

        @Override
        @Nullable
        public Object getHost() {
            return host;
        }

        @Override
        public MolangRuntime bind(MolangBindingPlan plan) {
            return new MolangBoundRuntime(this, plan);
        }

        @Override
        public void beginFrame(long frameId) {
            if (frameDepth != 0) {
                if (this.frameId != frameId) {
                    throw new IllegalStateException("Cannot nest different Molang frame identifiers");
                }
                frameDepth++;
                return;
            }
            if (!hasFrame || this.frameId != frameId) {
                this.frameId = frameId;
                hasFrame = true;
                frameEpoch++;
            }
            frameDepth = 1;
        }

        @Override
        public void endFrame() {
            if (frameDepth == 0) {
                throw new IllegalStateException("No active Molang frame");
            }
            frameDepth--;
        }

        @Override
        public void invalidateQueries() {
            if (tracksQueryChanges) {
                variableVersion++;
                tempVersion++;
                contextVersion++;
                hostVersion++;
            }
        }

        void variableChanged() {
            if (tracksQueryChanges) {
                variableVersion++;
            }
        }

        void tempChanged() {
            if (tracksQueryChanges) {
                tempVersion++;
            }
        }

        void contextChanged() {
            if (tracksQueryChanges) {
                contextVersion++;
            }
        }

        int evaluationDepth() {
            return evaluationDepth;
        }

        MolangStruct currentTemps() {
            ensureTempScope();
            return temps;
        }

        MolangQueryBinding binding(String name) {
            MolangQueryBinding binding = queryBindings.get(name);
            if (binding != null) {
                return binding;
            }
            MolangNumberContextQuery numberFunction = numberQueries.get(name);
            if (numberFunction != null) {
                binding = new MolangQueryBinding(this, schema.get(name), numberFunction);
                queryBindings.put(name, binding);
                return binding;
            }
            QueryHandler handler = queries.get(name);
            if (handler == null) {
                MolangContextQuery function = contextQueries.get(name);
                if (function != null) {
                    handler = arguments -> function.query(this, arguments);
                }
            }
            if (handler != null) {
                MolangQueryDefinition definition = schema.get(name);
                binding = new MolangQueryBinding(this, definition != null ? definition : MolangQueryDefinition.dynamic(name), handler);
                queryBindings.put(name, binding);
                return binding;
            }
            // 未执行的分支可以引用未知查询；绑定阶段不能提前触发 strict 查询错误。
            return new MolangQueryBinding(this, MolangQueryDefinition.dynamic(name), arguments -> resolveQuery(name).queryValue(arguments));
        }

        @Override
        public float getVariable(String name) {
            return variables.getFloat(name);
        }

        @Override
        public void setVariable(String name, float value) {
            variables.set(name, value);
            variableChanged();
        }

        @Override
        @Nullable
        public Object getVariableValue(String name) {
            Object value = variables.get(name);
            return value != null ? value : arrays.get(name);
        }

        @Override
        public void setVariableValue(String name, @Nullable Object value) {
            variables.set(name, value);
            variableChanged();
        }

        @Override
        public float getTemp(String name) {
            ensureTempScope();
            return temps.getFloat(name);
        }

        @Override
        public void setTemp(String name, float value) {
            ensureTempScope();
            temps.set(name, value);
            tempChanged();
        }

        @Override
        @Nullable
        public Object getTempValue(String name) {
            ensureTempScope();
            return temps.get(name);
        }

        @Override
        public void setTempValue(String name, @Nullable Object value) {
            ensureTempScope();
            temps.set(name, value);
            tempChanged();
        }

        @Override
        public void beginEvaluation() {
            if (tracksQueryChanges) {
                evaluationIds.push(evaluationId);
                evaluationId = ++nextEvaluationId;
            }
            evaluationDepth++;
        }

        @Override
        public void endEvaluation() {
            if (evaluationDepth == 0) {
                throw new IllegalStateException("No active Molang evaluation");
            }
            if (tempDepth == evaluationDepth) {
                // 仅清空根容器；表达式返回的子结构仍保留其独立内容。
                temps.clear();
                availableTemps.push(temps);
                temps = tempScopes.pop();
                tempDepth = tempDepths.popInt();
            }
            evaluationDepth--;
            if (tracksQueryChanges) {
                evaluationId = evaluationIds.popLong();
            }
        }

        private void ensureTempScope() {
            if (tempDepth == evaluationDepth) {
                return;
            }
            // 没有访问临时变量的求值层只记录深度，不分配或清空容器。
            tempScopes.push(temps);
            tempDepths.push(tempDepth);
            MolangStruct reusable = availableTemps.pollFirst();
            temps = reusable != null ? reusable : new MolangStruct();
            tempDepth = evaluationDepth;
        }

        @Override
        public float getContext(String name) {
            return contextValues.getFloat(name);
        }

        @Override
        @Nullable
        public Object getContextValue(String name) {
            return contextValues.get(name);
        }

        @Override
        public void setContextValue(String name, @Nullable Object value) {
            contextValues.set(name, value);
            contextChanged();
        }

        @Override
        public void setContextNumber(String name, float value) {
            contextValues.set(name, value);
            contextChanged();
        }

        @Override
        @Nullable
        public Object getArrayValue(String name) {
            Object array = arrays.get(name);
            return array != null ? array : variables.get(name);
        }

        @Override
        @Nullable
        public Object queryValue(String name, Object... args) {
            return resolveQuery(name).queryValue(args);
        }

        @Override
        public QueryHandler resolveQuery(String name) {
            String normalized = name.toLowerCase(Locale.ROOT);
            return useQueryBindings ? resolveDeclaredQuery(normalized) : resolveDynamicQuery(normalized);
        }

        private QueryHandler resolveDynamicQuery(String normalized) {
            QueryHandler handler = queries.get(normalized);
            if (handler != null) {
                return handler;
            }
            return unknownQuery(normalized);
        }

        private QueryHandler resolveDeclaredQuery(String normalized) {
            MolangQueryBinding existing = queryBindings.get(normalized);
            if (existing != null) {
                return existing;
            }
            return resolveDeclaredQuerySlow(normalized);
        }

        private QueryHandler resolveDeclaredQuerySlow(String normalized) {
            if (queries.containsKey(normalized)) {
                return binding(normalized);
            }
            if (contextQueries.containsKey(normalized) || numberQueries.containsKey(normalized)) {
                return binding(normalized);
            }
            return unknownQuery(normalized);
        }

        private QueryHandler unknownQuery(String name) {
            if (strictQueries) {
                throw new IllegalArgumentException("Unknown Molang query: " + name);
            }
            return UNKNOWN_QUERY;
        }

        @Override
        public float getThis() {
            return thisValue;
        }

        @Override
        public float getVariableElement(String name, int index) {
            return MolangValues.getElementFloat(getVariableValue(name), index);
        }

        @Override
        @Nullable
        public Object getVariableElementValue(String name, int index) {
            return MolangValues.getElement(getVariableValue(name), index);
        }

        @Override
        public void setVariableElement(String name, int index, float value) {
            MolangStruct storage = variables.get(name) != null || arrays.get(name) == null ? variables : arrays;
            setElementNumber(storage, name, index, value);
            variableChanged();
        }

        @Override
        public void setVariableElementValue(String name, int index, @Nullable Object value) {
            MolangStruct storage = variables.get(name) != null || arrays.get(name) == null ? variables : arrays;
            setElementValue(storage, name, index, value);
            variableChanged();
        }

        @Override
        public void setTempElementValue(String name, int index, @Nullable Object value) {
            ensureTempScope();
            setElementValue(temps, name, index, value);
            tempChanged();
        }

        private static void setElementValue(MolangStruct storage, String name, int index, @Nullable Object value) {
            Object previous = storage.get(name);
            Object updated = MolangArrays.write(previous, index, value);
            if (updated != previous) {
                storage.setOwnedReference(name, updated);
            }
        }

        private static void setElementValue(MolangStruct storage, MolangPath path, int index, @Nullable Object value) {
            Object previous = storage.get(path);
            Object updated = MolangArrays.write(previous, index, value);
            if (updated != previous) {
                storage.setOwnedReference(path, updated);
            }
        }

        private static void setElementNumber(MolangStruct storage, String name, int index, float value) {
            Object previous = storage.get(name);
            Object updated = MolangArrays.writeNumber(previous, index, value);
            if (updated != previous) {
                storage.setOwnedReference(name, updated);
            }
        }

        private static void setElementNumber(MolangStruct storage, MolangPath path, int index, float value) {
            Object previous = storage.get(path);
            Object updated = MolangArrays.writeNumber(previous, index, value);
            if (updated != previous) {
                storage.setOwnedReference(path, updated);
            }
        }

        @Override
        public float log(float value) {
            logger.log(value);
            return value;
        }

        @Override
        public float debugOutput(float value) {
            logger.debugOutput(value);
            return value;
        }
    }
}
