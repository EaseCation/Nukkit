package cn.nukkit.molang.compiler;

import cn.nukkit.molang.parser.ast.Expr;
import cn.nukkit.molang.parser.ast.Stmt;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 直线循环体只改写数值局部量时，变量操作数在整次循环内保持相同的值及类型标记。
 */
final class LoopOperandReads {
    static List<Expr.VariableAccess> find(Stmt body, Set<String> locals) {
        Set<Expr.VariableAccess> reads = new LinkedHashSet<>();
        return collect(body, locals, reads) ? List.copyOf(reads) : List.of();
    }

    private static boolean collect(Stmt statement, Set<String> locals, Set<Expr.VariableAccess> reads) {
        return switch (statement) {
            case Stmt.Block block -> block.statements().stream().allMatch(child -> collect(child, locals, reads));
            case Stmt.ExpressionStmt(Expr.VariableAssign value) -> isLocalWrite(value.scope(), value.name(), value.value(), locals, reads);
            case Stmt.ExpressionStmt(Expr.CompoundAssign value) -> isLocalWrite(value.scope(), value.name(), value.value(), locals, reads);
            default -> false;
        };
    }

    private static boolean isLocalWrite(String scope, String name, Expr value, Set<String> locals, Set<Expr.VariableAccess> reads) {
        return scope.equals("temp") && locals.contains(name) && arithmetic(value, locals, reads);
    }

    private static boolean arithmetic(Expr expr, Set<String> locals, Set<Expr.VariableAccess> reads) {
        return switch (expr) {
            case Expr.NumberLiteral ignored -> true;
            case Expr.VariableAccess value -> {
                if (value.scope().equals("variable")) {
                    reads.add(value);
                    yield true;
                }
                yield value.scope().equals("temp") && locals.contains(value.name());
            }
            case Expr.Group g -> arithmetic(g.expression(), locals, reads);
            case Expr.Binary b -> switch (b.operator()) {
                case "+", "-", "*", "/", "%", "<", "<=", ">", ">=" ->
                        arithmetic(b.left(), locals, reads) && arithmetic(b.right(), locals, reads);
                default -> false;
            };
            default -> false;
        };
    }

    private LoopOperandReads() {
    }
}
