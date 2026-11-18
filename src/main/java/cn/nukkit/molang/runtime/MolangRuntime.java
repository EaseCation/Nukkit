package cn.nukkit.molang.runtime;

import javax.annotation.Nullable;

public interface MolangRuntime {
    /**
     * 类型值是宿主实现的基础契约，缺失值使用 null，数值转换由专用入口完成。
     */
    @Nullable
    Object getVariableValue(String name);

    void setVariableValue(String name, @Nullable Object value);

    @Nullable
    Object getTempValue(String name);

    void setTempValue(String name, @Nullable Object value);

    @Nullable
    Object getContextValue(String name);

    QueryHandler resolveQuery(String name);

    default float getVariable(String name) {
        return getVariableNumber(name);
    }

    default void setVariable(String name, float value) {
        setVariableNumber(name, value);
    }

    default float getTemp(String name) {
        return getTempNumber(name);
    }

    default void setTemp(String name, float value) {
        setTempNumber(name, value);
    }

    default float getContext(String name) {
        return getContextNumber(name);
    }

    float getThis();

    default float getVariableElement(String name, int index) {
        return MolangValues.toFloat(getVariableElementValue(name, index));
    }

    default void setVariableElement(String name, int index, float value) {
        setVariableElementValue(name, index, value);
    }

    float log(float value);

    float debugOutput(float value);

    default long queryOperand0(String name) {
        return MolangValues.numericOperand(queryValue0(name));
    }

    default long queryOperand0(MolangQueryRef query) {
        return MolangValues.numericOperand(queryValue0(query));
    }

    default long queryOperand1(String name, @Nullable Object a) {
        return MolangValues.numericOperand(queryValue1(name, a));
    }

    default long queryOperand1(MolangQueryRef query, @Nullable Object a) {
        return MolangValues.numericOperand(queryValue1(query, a));
    }

    default long queryOperand2(String name, @Nullable Object a, @Nullable Object b) {
        return MolangValues.numericOperand(queryValue2(name, a, b));
    }

    default long queryOperand2(MolangQueryRef query, @Nullable Object a, @Nullable Object b) {
        return MolangValues.numericOperand(queryValue2(query, a, b));
    }

    default long queryOperand3(String name, @Nullable Object a, @Nullable Object b, @Nullable Object c) {
        return MolangValues.numericOperand(queryValue3(name, a, b, c));
    }

    default long queryOperand3(MolangQueryRef query, @Nullable Object a, @Nullable Object b, @Nullable Object c) {
        return MolangValues.numericOperand(queryValue3(query, a, b, c));
    }

    default float getVariableElementNumber(MolangPath path, int index) {
        return MolangValues.toFloat(getVariableElementValue(path, index));
    }

    default long getVariableElementOperand(MolangPath path, int index) {
        return MolangValues.numericOperand(getVariableElementValue(path, index));
    }

    default void setVariableElementNumber(MolangPath path, int index, float value) {
        setVariableElementValue(path, index, value);
    }

    default void setTempElementNumber(MolangPath path, int index, float value) {
        setTempElementValue(path, index, value);
    }


    default MolangQuerySchema querySchema() {
        return MolangQuerySchema.EMPTY;
    }

    @Nullable
    default Object getHost() {
        return null;
    }

    default MolangRuntime bind(MolangBindingPlan plan) {
        return this;
    }

    default void beginFrame(long frameId) {
    }

    default void endFrame() {
    }

    default void invalidateQueries() {
    }

    default void setContextValue(String name, @Nullable Object value) {
        throw new UnsupportedOperationException("This runtime has no mutable host context");
    }

    default void setContextNumber(String name, float value) {
        setContextValue(name, value);
    }

    default void setContextValue(MolangPath path, @Nullable Object value) {
        setContextValue(path.name(), value);
    }

    default void setContextNumber(MolangPath path, float value) {
        setContextNumber(path.name(), value);
    }

    @Nullable
    default Object queryValue(MolangQueryRef query, Object... args) {
        return queryValue(query.name(), args);
    }

    @Nullable
    default Object queryValue0(MolangQueryRef query) {
        return queryValue0(query.name());
    }

    default float queryNumber0(MolangQueryRef query) {
        return queryNumber0(query.name());
    }

    @Nullable
    default Object queryValue1(MolangQueryRef query, @Nullable Object a) {
        return queryValue1(query.name(), a);
    }

    default float queryNumber1(MolangQueryRef query, float a) {
        return queryNumber1(query.name(), a);
    }

    @Nullable
    default Object queryValue2(MolangQueryRef query, @Nullable Object a, @Nullable Object b) {
        return queryValue2(query.name(), a, b);
    }

    default float queryNumber2(MolangQueryRef query, float a, float b) {
        return queryNumber2(query.name(), a, b);
    }

    @Nullable
    default Object queryValue3(MolangQueryRef query, @Nullable Object a, @Nullable Object b, @Nullable Object c) {
        return queryValue3(query.name(), a, b, c);
    }

    default float queryNumber3(MolangQueryRef query, float a, float b, float c) {
        return queryNumber3(query.name(), a, b, c);
    }


    /*
     * 预解析路径入口默认转发到字符串 API，保留现有宿主实现的覆写行为。
     */

    @Nullable
    default Object getVariableValue(MolangPath path) {
        return getVariableValue(path.name());
    }

    default float getVariableNumber(MolangPath path) {
        return getVariableNumber(path.name());
    }

    default long getVariableOperand(MolangPath path) {
        return MolangValues.numericOperand(getVariableValue(path));
    }

    default void setVariableValue(MolangPath path, @Nullable Object value) {
        setVariableValue(path.name(), value);
    }

    default void setVariableNumber(MolangPath path, float value) {
        setVariableNumber(path.name(), value);
    }

    default void copyVariableValue(MolangPath target, MolangPath source) {
        setVariableValue(target, getVariableValue(source));
    }

    @Nullable
    default Object getTempValue(MolangPath path) {
        return getTempValue(path.name());
    }

    default float getTempNumber(MolangPath path) {
        return getTempNumber(path.name());
    }

    default long getTempOperand(MolangPath path) {
        return MolangValues.numericOperand(getTempValue(path));
    }

    default void setTempValue(MolangPath path, @Nullable Object value) {
        setTempValue(path.name(), value);
    }

    default void setTempNumber(MolangPath path, float value) {
        setTempNumber(path.name(), value);
    }

    @Nullable
    default Object getContextValue(MolangPath path) {
        return getContextValue(path.name());
    }

    default float getContextNumber(MolangPath path) {
        return getContextNumber(path.name());
    }

    default long getContextOperand(MolangPath path) {
        return MolangValues.numericOperand(getContextValue(path));
    }

    @Nullable
    default Object getArrayValue(MolangPath path) {
        return getArrayValue(path.name());
    }

    @Nullable
    default Object getVariableElementValue(MolangPath path, int index) {
        return getVariableElementValue(path.name(), index);
    }

    default void setVariableElementValue(MolangPath path, int index, @Nullable Object value) {
        setVariableElementValue(path.name(), index, value);
    }

    default void setTempElementValue(MolangPath path, int index, @Nullable Object value) {
        setTempElementValue(path.name(), index, value);
    }


    /**
     * 声明变量读取没有副作用，且在表达式没有写入或调用外部 query 时保持不变。
     */
    default boolean supportsStableVariableReads() {
        return false;
    }

    /**
     * 宿主允许不可观察的临时变量读写放入 JVM locals，并在循环出口发布最终值。
     */
    default boolean supportsLocalTempAccess() {
        return false;
    }

    /**
     * 编译器使用的数值入口。默认尊重类型值覆写，数值存储可覆写以避免装箱。
     */
    default float getVariableNumber(String name) {
        return MolangValues.toFloat(getVariableValue(name));
    }

    default float getTempNumber(String name) {
        return MolangValues.toFloat(getTempValue(name));
    }

    default float getContextNumber(String name) {
        return MolangValues.toFloat(getContextValue(name));
    }

    default void setVariableNumber(String name, float value) {
        setVariableValue(name, value);
    }

    default void setTempNumber(String name, float value) {
        setTempValue(name, value);
    }

    /**
     * 固定参数入口仍通过 queryValue 分派，保留自定义运行时的类型查询语义。
     */
    @Nullable
    default Object queryValue0(String name) {
        return queryValue(name, MolangValues.EMPTY_ARGUMENTS);
    }

    default float queryNumber0(String name) {
        return MolangValues.toFloat(queryValue0(name));
    }

    @Nullable
    default Object queryValue1(String name, @Nullable Object a) {
        return queryValue(name, a);
    }

    default float queryNumber1(String name, float a) {
        return MolangValues.toFloat(queryValue1(name, a));
    }

    @Nullable
    default Object queryValue2(String name, @Nullable Object a, @Nullable Object b) {
        return queryValue(name, a, b);
    }

    default float queryNumber2(String name, float a, float b) {
        return MolangValues.toFloat(queryValue2(name, a, b));
    }

    @Nullable
    default Object queryValue3(String name, @Nullable Object a, @Nullable Object b, @Nullable Object c) {
        return queryValue(name, a, b, c);
    }

    default float queryNumber3(String name, float a, float b, float c) {
        return MolangValues.toFloat(queryValue3(name, a, b, c));
    }


    @Nullable
    default Object getArrayValue(String name) {
        return getVariableValue(name);
    }

    @Nullable
    default Object getVariableElementValue(String name, int index) {
        return MolangValues.getElement(getArrayValue(name), index);
    }

    default void setVariableElementValue(String name, int index, @Nullable Object value) {
        MolangValues.setElement(getArrayValue(name), index, value);
    }

    default void setTempElementValue(String name, int index, @Nullable Object value) {
        MolangValues.setElement(getTempValue(name), index, value);
    }

    @Nullable
    default Object queryValue(String name, Object... args) {
        return resolveQuery(name).queryValue(args);
    }

    /**
     * 每次表达式求值建立独立的临时变量作用域；嵌套求值结束后恢复外层。
     */
    default void beginEvaluation() {
    }

    default void endEvaluation() {
    }

    static MolangRuntimeBuilder builder() {
        return new MolangRuntimeBuilder();
    }
}
