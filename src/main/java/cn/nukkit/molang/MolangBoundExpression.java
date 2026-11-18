package cn.nukkit.molang;

import cn.nukkit.molang.runtime.MolangRuntime;

import javax.annotation.Nullable;

/**
 * 已验证语义并绑定到一个宿主的执行入口，不允许在求值时更换 runtime。
 */
public final class MolangBoundExpression {
    private final MolangExpression expression;
    private final MolangRuntime runtime;

    public MolangBoundExpression(MolangExpression expression, MolangRuntime runtime) {
        this.expression = expression;
        this.runtime = runtime;
    }

    public float evaluate() {
        return expression.evaluate(runtime);
    }

    @Nullable
    public Object evaluateValue() {
        return expression.evaluateValue(runtime);
    }
}
