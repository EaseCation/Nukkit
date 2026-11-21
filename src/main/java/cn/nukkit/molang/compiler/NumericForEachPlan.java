package cn.nukkit.molang.compiler;

import cn.nukkit.molang.parser.ast.Expr;
import cn.nukkit.molang.parser.ast.Stmt;

/**
 * 只有迭代值在循环体内不可由宿主观察、数组不会被修改时，才延迟发布数值目标。
 */
final class NumericForEachPlan {
    static boolean eligible(Stmt.ForEach loop) {
        if (!"temp".equals(loop.target().scope()) || loop.target().name().contains(".")) {
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
                case Expr.VariableAssign value when !scalarAssignment(value.value(), target) -> rejected[0] = true;
                // 容器访问、复制及实体引用解析都可能回调宿主并读取当前迭代目标。
                case Expr.NullCoalescing _, Expr.ArrayAccess _, Expr.IndexAccess _, Expr.MemberAccess _,
                     Expr.ArrayAssign _, Expr.MemberAssign _, Expr.ContextSwitch _, Expr.QueryCall _ -> rejected[0] = true;
                default -> {
                }
            }
        });
        return !rejected[0];
    }

    private static boolean scalarAssignment(Expr value, String target) {
        return switch (value) {
            case Expr.NumberLiteral _, Expr.StringLiteral _, Expr.This _, Expr.Unary _, Expr.Binary _, Expr.CompoundAssign _, Expr.MathCall _ -> true;
            case Expr.Group group -> scalarAssignment(group.expression(), target);
            case Expr.VariableAccess variable -> "temp".equals(variable.scope()) && target.equals(variable.name());
            default -> false;
        };
    }

    private NumericForEachPlan() {
    }
}
