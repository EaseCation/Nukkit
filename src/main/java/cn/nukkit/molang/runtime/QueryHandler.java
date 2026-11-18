package cn.nukkit.molang.runtime;

import javax.annotation.Nullable;

/**
 * 查询参数和结果保留完整类型；固定参数数值入口可覆写以避免装箱。
 */
@FunctionalInterface
public interface QueryHandler {
    @Nullable
    Object queryValue(Object... args);

    default long queryOperand0() {
        return MolangValues.numericOperand(queryValue0());
    }

    default long queryOperand1(@Nullable Object a) {
        return MolangValues.numericOperand(queryValue1(a));
    }

    default long queryOperand2(@Nullable Object a, @Nullable Object b) {
        return MolangValues.numericOperand(queryValue2(a, b));
    }

    default long queryOperand3(@Nullable Object a, @Nullable Object b, @Nullable Object c) {
        return MolangValues.numericOperand(queryValue3(a, b, c));
    }

    /**
     * 固定参数入口保留 queryValue 的覆写语义，并让 JIT 消除非逃逸的参数容器。
     */
    @Nullable
    default Object queryValue0() {
        return queryValue(MolangValues.EMPTY_ARGUMENTS);
    }

    @Nullable
    default Object queryValue1(@Nullable Object a) {
        return queryValue(a);
    }

    @Nullable
    default Object queryValue2(@Nullable Object a, @Nullable Object b) {
        return queryValue(a, b);
    }

    @Nullable
    default Object queryValue3(@Nullable Object a, @Nullable Object b, @Nullable Object c) {
        return queryValue(a, b, c);
    }

    default float queryNumber0() {
        return MolangValues.toFloat(queryValue0());
    }

    default float queryNumber1(float a) {
        return MolangValues.toFloat(queryValue1(a));
    }

    default float queryNumber2(float a, float b) {
        return MolangValues.toFloat(queryValue2(a, b));
    }

    default float queryNumber3(float a, float b, float c) {
        return MolangValues.toFloat(queryValue3(a, b, c));
    }
}
