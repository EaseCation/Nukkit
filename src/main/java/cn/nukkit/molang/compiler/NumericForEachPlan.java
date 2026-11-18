package cn.nukkit.molang.compiler;

import cn.nukkit.molang.MolangCompileOptions;
import cn.nukkit.molang.parser.ast.Expr;
import cn.nukkit.molang.parser.ast.Stmt;

/**
 * 只有迭代值在循环体内不可由宿主观察、数组不会被修改时，才延迟发布数值目标。
 */
final class NumericForEachPlan {
    static boolean eligible(Stmt.ForEach loop, MolangCompileOptions options) {
        if (!options.stableVariableReads() || !"temp".equals(loop.target().scope()) || loop.target().name().contains(".")) {
            return false;
        }
        String target = loop.target().name();
        boolean[] rejected = {false};
        AstNodes.visit(loop.body(), statement -> {
            if (statement instanceof Stmt.ForEach || statement instanceof Stmt.ReturnStmt) {
                rejected[0] = true;
            }
        }, expression -> {
            switch (expression) {
                case Expr.VariableAssign value when "temp".equals(value.scope()) && AstOptimizer.overlaps(value.name(), target) -> rejected[0] = true;
                case Expr.CompoundAssign value when "temp".equals(value.scope()) && AstOptimizer.overlaps(value.name(), target) -> rejected[0] = true;
                case Expr.VariableAccess value when "temp".equals(value.scope()) && !value.name().equals(target)
                        && AstOptimizer.overlaps(value.name(), target) -> rejected[0] = true;
                case Expr.ArrayAccess value when "temp".equals(value.scope()) && AstOptimizer.overlaps(value.name(), target) -> rejected[0] = true;
                case Expr.ArrayAssign _, Expr.MemberAssign _, Expr.ContextSwitch _ -> rejected[0] = true;
                case Expr.QueryCall value -> {
                    // 宿主调用即使只读也可能抛异常并观察尚未发布的迭代目标。
                    if (!MolangFunctions.PURE_QUERIES.contains(value.name())) {
                        rejected[0] = true;
                    }
                }
                default -> {
                }
            }
        });
        return !rejected[0];
    }

    private NumericForEachPlan() {
    }
}
