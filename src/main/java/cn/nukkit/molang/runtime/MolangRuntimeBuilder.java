package cn.nukkit.molang.runtime;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongStack;

import javax.annotation.Nullable;
import java.util.*;

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

    /**
     * 内建 runtime 的所有程序视图均向同一个宿主转发求值作用域。
     */
    public static boolean hasBuiltInEvaluationScopes(MolangRuntime runtime) {
        return runtime instanceof MolangRuntimeImpl;
    }

    /**
     * 在同一深度结束当前求值并开始下一次求值，供已确认使用内建宿主的编译批次调用。
     */
    @SuppressWarnings("unused")
    public static void nextEvaluation(MolangRuntime runtime) {
        ((MolangRuntimeImpl) runtime).releaseEvaluation();
    }

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
        cached = new QueryRegistry(immutableQueries(), Map.copyOf(contextQueries), Map.copyOf(numberQueries), schema, tracks, boundCalls, MolangQuerySite.Layout.of(schema.definitions().keySet()));
        cachedRegistry = cached;
        return cached;
    }

    private record QueryRegistry(Map<String, QueryHandler> handlers,
                                 Map<String, MolangContextQuery> functions,
                                 Map<String, MolangNumberContextQuery> numberFunctions,
                                 MolangQuerySchema schema,
                                 boolean tracksChanges,
                                 boolean boundCalls,
                                 MolangQuerySite.Layout layout) {
    }

    static final class MolangRuntimeImpl implements MolangRuntime {
        private static final QueryHandler UNKNOWN_QUERY = _ -> 0f;
        private static final MolangQueryBinding[] NO_BINDINGS = {};

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

        QueryHandler resolve(MolangQuerySite site) {
            if (!useQueryBindings) {
                return resolveDynamicQuery(site.name());
            }
            int slot = site.slot(queryLayout);
            return slot >= 0 ? binding(slot, site.name()) : unknownQuery(site.name());
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
            return currentTemps().getFloat(path);
        }

        @Override
        public long getTempOperand(MolangPath path) {
            return currentTemps().getOperand(path);
        }

        @Override
        @Nullable
        public Object getTempValue(MolangPath path) {
            return currentTemps().get(path);
        }

        @Override
        public void setTempNumber(MolangPath path, float value) {
            currentTemps().set(path, value);
            tempChanged();
        }

        @Override
        public void setTempValue(MolangPath path, @Nullable Object value) {
            currentTemps().set(path, value);
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
            Object variable = variables.get(path);
            Object previous = variable != null ? variable : arrays.get(path);
            finishVariableElementWrite(path, variable, previous, MolangArrays.write(previous, index, value));
        }

        @Override
        public void setVariableElementNumber(MolangPath path, int index, float value) {
            Object variable = variables.get(path);
            Object previous = variable != null ? variable : arrays.get(path);
            finishVariableElementWrite(path, variable, previous, MolangArrays.writeNumber(previous, index, value));
        }

        private void finishVariableElementWrite(MolangPath path, @Nullable Object variable, @Nullable Object previous, @Nullable Object updated) {
            if (updated != previous) {
                MolangStruct storage = variable != null || previous == null ? variables : arrays;
                storage.setOwnedReference(path, updated);
            }
            variableChanged();
        }

        @Override
        public void setTempElementValue(MolangPath path, int index, @Nullable Object value) {
            setElementValue(currentTemps(), path, index, value);
            tempChanged();
        }

        @Override
        public void setTempElementNumber(MolangPath path, int index, float value) {
            setElementNumber(currentTemps(), path, index, value);
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
            return currentTemps().getFloat(name);
        }

        @Override
        public void setTempNumber(String name, float value) {
            currentTemps().set(name, value);
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
        private final MolangQuerySite.Layout queryLayout;
        private MolangQueryBinding[] queryBindings = NO_BINDINGS;
        private final MolangQuerySchema schema;
        @Nullable
        MolangQuerySchema validatedSchema;
        private final boolean useQueryBindings;
        final MolangStruct contextValues;
        final MolangStruct arrays;
        @Nullable
        private TempScope tempScope;
        private int evaluationDepth;
        @Nullable
        private LongStack evaluationIds;
        private long nextEvaluationId;
        private long evaluationId;
        private int evaluationIdDepth;
        long frameEpoch;
        long hostFrameEpoch;
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
            queryLayout = registry.layout();
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
                invalidateFrameNumbers();
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
                invalidateFrameNumbers();
            }
        }

        private void invalidateFrameNumbers() {
            // 零值留给尚未采样的绑定。
            if (++hostFrameEpoch == 0) {
                hostFrameEpoch = 1;
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

        boolean topEvaluationMatches(long epoch) {
            return evaluationIdDepth == 1 && evaluationId == epoch;
        }

        long evaluationId() {
            // 帧内的 FRAME 查询不使用求值编号，嵌套时只保存真正访问过缓存的父层。
            if (evaluationDepth != evaluationIdDepth) {
                if (evaluationIdDepth != 0) {
                    if (evaluationIds == null) {
                        evaluationIds = new LongArrayList();
                    }
                    evaluationIds.push(evaluationId);
                    evaluationIds.push(evaluationIdDepth);
                }
                evaluationIdDepth = evaluationDepth;
                evaluationId = ++nextEvaluationId;
            }
            return evaluationId;
        }

        MolangStruct currentTemps() {
            TempScope current = tempScope;
            if (current == null) {
                current = new TempScope(null);
                tempScope = current;
            }
            if (current.depth != evaluationDepth) {
                TempScope next = current.child;
                if (next == null) {
                    next = new TempScope(current);
                    current.child = next;
                }
                next.depth = evaluationDepth;
                tempScope = next;
                return next.values;
            }
            return current.values;
        }

        // 只有实际读写 temp 的求值层才占用节点；同深度后续求值复用已清空的子节点。
        private static final class TempScope {
            private final MolangStruct values = new MolangStruct();
            @Nullable
            private final TempScope parent;
            @Nullable
            private TempScope child;
            private int depth;

            private TempScope(@Nullable TempScope parent) {
                this.parent = parent;
            }
        }

        MolangQueryBinding binding(String name) {
            int slot = queryLayout.index(name);
            return slot >= 0 ? binding(slot, name) : missingBinding(name);
        }

        private MolangQueryBinding binding(int slot, String name) {
            MolangQueryBinding[] bindings = queryBindings;
            if (slot < bindings.length && bindings[slot] != null) {
                return bindings[slot];
            }
            return createBinding(slot, name);
        }

        private MolangQueryBinding createBinding(int slot, String name) {
            MolangQueryBinding binding;
            MolangNumberContextQuery numberFunction = numberQueries.get(name);
            if (numberFunction != null) {
                binding = MolangQueryBinding.number(this, schema.get(name), numberFunction);
                return rememberBinding(slot, binding);
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
                binding = MolangQueryBinding.forHandler(this, definition != null ? definition : MolangQueryDefinition.dynamic(name), handler);
                return rememberBinding(slot, binding);
            }
            return missingBinding(name);
        }

        private MolangQueryBinding rememberBinding(int slot, MolangQueryBinding binding) {
            if (slot >= queryBindings.length) {
                long required = Math.max(slot + 1L, Math.max(4L, 2L * queryBindings.length));
                int capacity = (int) Math.min(queryLayout.size(), required);
                queryBindings = Arrays.copyOf(queryBindings, capacity);
            }
            queryBindings[slot] = binding;
            return binding;
        }

        private MolangQueryBinding missingBinding(String name) {
            // 未执行的分支可以引用未知查询；绑定阶段不能提前触发 strict 查询错误。
            return new MolangQueryBinding(this, MolangQueryDefinition.dynamic(name), arguments -> unknownQuery(name).queryValue(arguments));
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
            return currentTemps().getFloat(name);
        }

        @Override
        public void setTemp(String name, float value) {
            currentTemps().set(name, value);
            tempChanged();
        }

        @Override
        @Nullable
        public Object getTempValue(String name) {
            return currentTemps().get(name);
        }

        @Override
        public void setTempValue(String name, @Nullable Object value) {
            currentTemps().set(name, value);
            tempChanged();
        }

        @Override
        public void beginEvaluation() {
            evaluationDepth++;
        }

        @Override
        public void endEvaluation() {
            releaseEvaluation();
            evaluationDepth--;
        }

        long scopeResources() {
            return ((long) (tempScope == null ? 0 : tempScope.depth) << 32) | Integer.toUnsignedLong(evaluationIdDepth);
        }

        void finishLinkedEvaluation(int previousDepth, long resources) {
            // 重入可能使用 temp、建立缓存或改变作用域栈，仍按普通 endEvaluation 释放实际的当前层。
            if (evaluationDepth == previousDepth + 1 && scopeResources() == resources) {
                evaluationDepth = previousDepth;
            } else {
                endEvaluation();
            }
        }

        private void releaseEvaluation() {
            if (evaluationDepth == 0) {
                throw new IllegalStateException("No active Molang evaluation");
            }
            if (tempScope != null && tempScope.depth == evaluationDepth) {
                tempScope.values.clear();
                tempScope = tempScope.parent;
            }
            if (evaluationDepth == evaluationIdDepth) {
                if (evaluationDepth == 1 || evaluationIds == null || evaluationIds.isEmpty()) {
                    evaluationIdDepth = 0;
                    evaluationId = 0;
                } else {
                    evaluationIdDepth = (int) evaluationIds.popLong();
                    evaluationId = evaluationIds.popLong();
                }
            }
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
            // 编译器生成的查询名已规范化，只有未绑定或调用方传入不同大小写时才需要回退。
            if (useQueryBindings) {
                int slot = queryLayout.index(name);
                return slot >= 0 ? binding(slot, name) : resolveDeclaredQuery(name.toLowerCase(Locale.ROOT));
            }
            QueryHandler found = queries.get(name);
            return found != null ? found : resolveDynamicQuery(name.toLowerCase(Locale.ROOT));
        }

        private QueryHandler resolveDynamicQuery(String normalized) {
            QueryHandler handler = queries.get(normalized);
            if (handler != null) {
                return handler;
            }
            return unknownQuery(normalized);
        }

        private QueryHandler resolveDeclaredQuery(String normalized) {
            int slot = queryLayout.index(normalized);
            return slot >= 0 ? binding(slot, normalized) : unknownQuery(normalized);
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
            Object variable = variables.get(name);
            Object previous = variable != null ? variable : arrays.get(name);
            finishVariableElementWrite(name, variable, previous, MolangArrays.writeNumber(previous, index, value));
        }

        @Override
        public void setVariableElementValue(String name, int index, @Nullable Object value) {
            Object variable = variables.get(name);
            Object previous = variable != null ? variable : arrays.get(name);
            finishVariableElementWrite(name, variable, previous, MolangArrays.write(previous, index, value));
        }

        private void finishVariableElementWrite(String name, @Nullable Object variable, @Nullable Object previous, @Nullable Object updated) {
            if (updated != previous) {
                MolangStruct storage = variable != null || previous == null ? variables : arrays;
                storage.setOwnedReference(name, updated);
            }
            variableChanged();
        }

        @Override
        public void setTempElementValue(String name, int index, @Nullable Object value) {
            setElementValue(currentTemps(), name, index, value);
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
