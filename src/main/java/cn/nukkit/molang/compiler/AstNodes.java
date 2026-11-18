package cn.nukkit.molang.compiler;

import cn.nukkit.molang.parser.ast.Expr;
import cn.nukkit.molang.parser.ast.Stmt;

import java.util.List;
import java.util.function.Consumer;

final class AstNodes {
    static List<Expr> children(Expr expression) {
        return switch (expression) {
            case Expr.Binary value -> List.of(value.left(), value.right());
            case Expr.Unary value -> List.of(value.operand());
            case Expr.Ternary value -> List.of(value.condition(), value.thenBranch(), value.elseBranch());
            case Expr.BinaryConditional value -> List.of(value.condition(), value.thenBranch());
            case Expr.NullCoalescing value -> List.of(value.left(), value.right());
            case Expr.VariableAssign value -> List.of(value.value());
            case Expr.CompoundAssign value -> List.of(value.value());
            case Expr.ArrayAccess value -> List.of(value.index());
            case Expr.ArrayAssign value -> List.of(value.index(), value.value());
            case Expr.MathCall value -> {
                MolangFunctions.MathFunction function = MolangFunctions.math(value.function());
                yield function != null ? value.args().subList(0, Math.min(value.args().size(), function.arity())) : value.args();
            }
            case Expr.QueryCall value -> value.args();
            case Expr.MemberAccess value -> List.of(value.target());
            case Expr.MemberAssign value -> List.of(value.target(), value.value());
            case Expr.IndexAccess value -> List.of(value.target(), value.index());
            case Expr.ContextSwitch value -> List.of(value.target(), value.expression());
            case Expr.Group value -> List.of(value.expression());
            default -> List.of();
        };
    }

    static void visit(Stmt statement, Consumer<Stmt> statements, Consumer<Expr> expressions) {
        statements.accept(statement);
        switch (statement) {
            case Stmt.Program value -> value.statements().forEach(child -> visit(child, statements, expressions));
            case Stmt.Block value -> value.statements().forEach(child -> visit(child, statements, expressions));
            case Stmt.ExpressionStmt value -> visit(value.expression(), statements, expressions);
            case Stmt.ReturnStmt value -> visit(value.value(), statements, expressions);
            case Stmt.Loop value -> {
                visit(value.count(), statements, expressions);
                visit(value.body(), statements, expressions);
            }
            case Stmt.ForEach value -> {
                visit(value.array(), statements, expressions);
                visit(value.body(), statements, expressions);
            }
            case Stmt.Break _, Stmt.Continue _ -> {
            }
        }
    }

    static void visit(Expr expression, Consumer<Stmt> statements, Consumer<Expr> expressions) {
        expressions.accept(expression);
        if (expression instanceof Expr.Scope(Stmt.Block block)) {
            visit(block, statements, expressions);
        } else {
            children(expression).forEach(child -> visit(child, statements, expressions));
        }
    }

    private AstNodes() {
    }
}
