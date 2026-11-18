package cn.nukkit.molang;

import cn.nukkit.molang.runtime.MolangRuntime;
import cn.nukkit.molang.runtime.MolangBindingPlan;

@FunctionalInterface
public interface MolangExpression {
    float evaluate(MolangRuntime runtime);

    default MolangBindingPlan bindingPlan() {
        return MolangBindingPlan.EMPTY;
    }

    default MolangBoundExpression bind(MolangRuntime runtime) {
        return new MolangBoundExpression(this, runtime.bind(bindingPlan()));
    }

    /**
     * 返回保留字符串、结构和实体引用类型的表达式结果。
     */
    default Object evaluateValue(MolangRuntime runtime) {
        return evaluate(runtime);
    }
}
