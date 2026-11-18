package cn.nukkit.molang.runtime;

import javax.annotation.Nullable;

/**
 * 绑定视图的转发层，未专门绑定的入口使用宿主 runtime 的实现。
 */
abstract class MolangRuntimeForwarder implements MolangRuntime {
    final MolangRuntime delegate;

    @Override
    public long queryOperand0(String name) {
        return delegate.queryOperand0(name);
    }

    @Override
    public long queryOperand0(MolangQueryRef query) {
        return delegate.queryOperand0(query);
    }

    @Override
    public long queryOperand1(String name, @Nullable Object a) {
        return delegate.queryOperand1(name, a);
    }

    @Override
    public long queryOperand1(MolangQueryRef query, @Nullable Object a) {
        return delegate.queryOperand1(query, a);
    }

    @Override
    public long queryOperand2(String name, @Nullable Object a, @Nullable Object b) {
        return delegate.queryOperand2(name, a, b);
    }

    @Override
    public long queryOperand2(MolangQueryRef query, @Nullable Object a, @Nullable Object b) {
        return delegate.queryOperand2(query, a, b);
    }

    @Override
    public long queryOperand3(String name, @Nullable Object a, @Nullable Object b, @Nullable Object c) {
        return delegate.queryOperand3(name, a, b, c);
    }

    @Override
    public long queryOperand3(MolangQueryRef query, @Nullable Object a, @Nullable Object b, @Nullable Object c) {
        return delegate.queryOperand3(query, a, b, c);
    }

    MolangRuntimeForwarder(MolangRuntime delegate) {
        this.delegate = delegate;
    }

    @Override
    public float getVariable(String name) {
        return delegate.getVariable(name);
    }

    @Override
    public void setVariable(String name, float value) {
        delegate.setVariable(name, value);
    }

    @Override
    public float getTemp(String name) {
        return delegate.getTemp(name);
    }

    @Override
    public void setTemp(String name, float value) {
        delegate.setTemp(name, value);
    }

    @Override
    public float getContext(String name) {
        return delegate.getContext(name);
    }

    @Override
    public float getThis() {
        return delegate.getThis();
    }

    @Override
    public float getVariableElement(String name, int index) {
        return delegate.getVariableElement(name, index);
    }

    @Override
    public void setVariableElement(String name, int index, float value) {
        delegate.setVariableElement(name, index, value);
    }

    @Override
    public float log(float value) {
        return delegate.log(value);
    }

    @Override
    public float debugOutput(float value) {
        return delegate.debugOutput(value);
    }

    @Override
    public float getVariableElementNumber(MolangPath path, int index) {
        return delegate.getVariableElementNumber(path, index);
    }

    @Override
    public long getVariableElementOperand(MolangPath path, int index) {
        return delegate.getVariableElementOperand(path, index);
    }

    @Override
    public void setVariableElementNumber(MolangPath path, int index, float value) {
        delegate.setVariableElementNumber(path, index, value);
    }

    @Override
    public void setTempElementNumber(MolangPath path, int index, float value) {
        delegate.setTempElementNumber(path, index, value);
    }

    @Override
    public MolangQuerySchema querySchema() {
        return delegate.querySchema();
    }

    @Override
    @Nullable
    public Object getHost() {
        return delegate.getHost();
    }

    @Override
    public MolangRuntime bind(MolangBindingPlan plan) {
        return delegate.bind(plan);
    }

    @Override
    public void beginFrame(long frameId) {
        delegate.beginFrame(frameId);
    }

    @Override
    public void endFrame() {
        delegate.endFrame();
    }

    @Override
    public void invalidateQueries() {
        delegate.invalidateQueries();
    }

    @Override
    public void setContextValue(String name, @Nullable Object value) {
        delegate.setContextValue(name, value);
    }

    @Override
    public void setContextNumber(String name, float value) {
        delegate.setContextNumber(name, value);
    }

    @Override
    public void setContextValue(MolangPath path, @Nullable Object value) {
        delegate.setContextValue(path, value);
    }

    @Override
    public void setContextNumber(MolangPath path, float value) {
        delegate.setContextNumber(path, value);
    }

    @Override
    @Nullable
    public Object queryValue(MolangQueryRef query, Object... args) {
        return delegate.queryValue(query, args);
    }

    @Override
    @Nullable
    public Object queryValue0(MolangQueryRef query) {
        return delegate.queryValue0(query);
    }

    @Override
    public float queryNumber0(MolangQueryRef query) {
        return delegate.queryNumber0(query);
    }

    @Override
    @Nullable
    public Object queryValue1(MolangQueryRef query, @Nullable Object a) {
        return delegate.queryValue1(query, a);
    }

    @Override
    public float queryNumber1(MolangQueryRef query, float a) {
        return delegate.queryNumber1(query, a);
    }

    @Override
    @Nullable
    public Object queryValue2(MolangQueryRef query, @Nullable Object a, @Nullable Object b) {
        return delegate.queryValue2(query, a, b);
    }

    @Override
    public float queryNumber2(MolangQueryRef query, float a, float b) {
        return delegate.queryNumber2(query, a, b);
    }

    @Override
    @Nullable
    public Object queryValue3(MolangQueryRef query, @Nullable Object a, @Nullable Object b, @Nullable Object c) {
        return delegate.queryValue3(query, a, b, c);
    }

    @Override
    public float queryNumber3(MolangQueryRef query, float a, float b, float c) {
        return delegate.queryNumber3(query, a, b, c);
    }

    @Override
    @Nullable
    public Object getVariableValue(MolangPath path) {
        return delegate.getVariableValue(path);
    }

    @Override
    public float getVariableNumber(MolangPath path) {
        return delegate.getVariableNumber(path);
    }

    @Override
    public long getVariableOperand(MolangPath path) {
        return delegate.getVariableOperand(path);
    }

    @Override
    public void setVariableValue(MolangPath path, @Nullable Object value) {
        delegate.setVariableValue(path, value);
    }

    @Override
    public void setVariableNumber(MolangPath path, float value) {
        delegate.setVariableNumber(path, value);
    }

    @Override
    public void copyVariableValue(MolangPath target, MolangPath source) {
        delegate.copyVariableValue(target, source);
    }

    @Override
    @Nullable
    public Object getTempValue(MolangPath path) {
        return delegate.getTempValue(path);
    }

    @Override
    public float getTempNumber(MolangPath path) {
        return delegate.getTempNumber(path);
    }

    @Override
    public long getTempOperand(MolangPath path) {
        return delegate.getTempOperand(path);
    }

    @Override
    public void setTempValue(MolangPath path, @Nullable Object value) {
        delegate.setTempValue(path, value);
    }

    @Override
    public void setTempNumber(MolangPath path, float value) {
        delegate.setTempNumber(path, value);
    }

    @Override
    @Nullable
    public Object getContextValue(MolangPath path) {
        return delegate.getContextValue(path);
    }

    @Override
    public float getContextNumber(MolangPath path) {
        return delegate.getContextNumber(path);
    }

    @Override
    public long getContextOperand(MolangPath path) {
        return delegate.getContextOperand(path);
    }

    @Override
    @Nullable
    public Object getArrayValue(MolangPath path) {
        return delegate.getArrayValue(path);
    }

    @Override
    @Nullable
    public Object getVariableElementValue(MolangPath path, int index) {
        return delegate.getVariableElementValue(path, index);
    }

    @Override
    public void setVariableElementValue(MolangPath path, int index, @Nullable Object value) {
        delegate.setVariableElementValue(path, index, value);
    }

    @Override
    public void setTempElementValue(MolangPath path, int index, @Nullable Object value) {
        delegate.setTempElementValue(path, index, value);
    }

    @Override
    public boolean supportsStableVariableReads() {
        return delegate.supportsStableVariableReads();
    }

    @Override
    public boolean supportsLocalTempAccess() {
        return delegate.supportsLocalTempAccess();
    }

    @Override
    public float getVariableNumber(String name) {
        return delegate.getVariableNumber(name);
    }

    @Override
    public float getTempNumber(String name) {
        return delegate.getTempNumber(name);
    }

    @Override
    public float getContextNumber(String name) {
        return delegate.getContextNumber(name);
    }

    @Override
    public void setVariableNumber(String name, float value) {
        delegate.setVariableNumber(name, value);
    }

    @Override
    public void setTempNumber(String name, float value) {
        delegate.setTempNumber(name, value);
    }

    @Override
    @Nullable
    public Object queryValue0(String name) {
        return delegate.queryValue0(name);
    }

    @Override
    public float queryNumber0(String name) {
        return delegate.queryNumber0(name);
    }

    @Override
    @Nullable
    public Object queryValue1(String name, @Nullable Object a) {
        return delegate.queryValue1(name, a);
    }

    @Override
    public float queryNumber1(String name, float a) {
        return delegate.queryNumber1(name, a);
    }

    @Override
    @Nullable
    public Object queryValue2(String name, @Nullable Object a, @Nullable Object b) {
        return delegate.queryValue2(name, a, b);
    }

    @Override
    public float queryNumber2(String name, float a, float b) {
        return delegate.queryNumber2(name, a, b);
    }

    @Override
    @Nullable
    public Object queryValue3(String name, @Nullable Object a, @Nullable Object b, @Nullable Object c) {
        return delegate.queryValue3(name, a, b, c);
    }

    @Override
    public float queryNumber3(String name, float a, float b, float c) {
        return delegate.queryNumber3(name, a, b, c);
    }

    @Override
    @Nullable
    public Object getVariableValue(String name) {
        return delegate.getVariableValue(name);
    }

    @Override
    public void setVariableValue(String name, @Nullable Object value) {
        delegate.setVariableValue(name, value);
    }

    @Override
    @Nullable
    public Object getTempValue(String name) {
        return delegate.getTempValue(name);
    }

    @Override
    public void setTempValue(String name, @Nullable Object value) {
        delegate.setTempValue(name, value);
    }

    @Override
    @Nullable
    public Object getContextValue(String name) {
        return delegate.getContextValue(name);
    }

    @Override
    @Nullable
    public Object getArrayValue(String name) {
        return delegate.getArrayValue(name);
    }

    @Override
    @Nullable
    public Object getVariableElementValue(String name, int index) {
        return delegate.getVariableElementValue(name, index);
    }

    @Override
    public void setVariableElementValue(String name, int index, @Nullable Object value) {
        delegate.setVariableElementValue(name, index, value);
    }

    @Override
    public void setTempElementValue(String name, int index, @Nullable Object value) {
        delegate.setTempElementValue(name, index, value);
    }

    @Override
    @Nullable
    public Object queryValue(String name, Object... args) {
        return delegate.queryValue(name, args);
    }

    @Override
    public void beginEvaluation() {
        delegate.beginEvaluation();
    }

    @Override
    public void endEvaluation() {
        delegate.endEvaluation();
    }

    @Override
    public QueryHandler resolveQuery(String name) {
        return delegate.resolveQuery(name);
    }
}
