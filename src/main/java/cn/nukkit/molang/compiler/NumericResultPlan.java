package cn.nukkit.molang.compiler;

import cn.nukkit.molang.MolangCompileOptions;
import cn.nukkit.molang.parser.ast.Expr;
import cn.nukkit.molang.parser.ast.Stmt;
import cn.nukkit.molang.runtime.MolangQueryDefinition;

import java.util.HashSet;
import java.util.Set;

/**
 * 证明所有正常返回都为数值时，共用数值方法，类型入口仅在最终边界装箱。
 */
final class NumericResultPlan {
    private final MolangCompileOptions options;
    private final Set<String> numericTemps;
    private boolean numeric = true;

    NumericResultPlan(Stmt.Program program, MolangCompileOptions options, Set<String> numericTemps) {
        this.options = options;
        this.numericTemps = numericTemps;
        AstNodes.visit(program, _ -> {}, value -> {
            if (value instanceof Expr.ContextSwitch) {
                numeric = false;
            }
        });
        Set<String> initialized = new HashSet<>();
        if (!program.isComplex() && program.statements().size() == 1 && program.statements().getFirst() instanceof Stmt.ExpressionStmt(Expr value)) {
            expression(value, initialized);
            numeric &= number(value, initialized);
        } else {
            statement(program, initialized);
        }
    }

    boolean numeric() {
        return numeric;
    }

    private void statement(Stmt statement, Set<String> initialized) {
        switch (statement) {
            case Stmt.Program value -> value.statements().forEach(child -> statement(child, initialized));
            case Stmt.Block value -> value.statements().forEach(child -> statement(child, initialized));
            case Stmt.ExpressionStmt value -> {
                expression(value.expression(), initialized);
                if (value.expression() instanceof Expr.VariableAssign assigned && "temp".equals(assigned.scope()) && numericTemps.contains(assigned.name())) {
                    initialized.add(assigned.name());
                }
            }
            case Stmt.ReturnStmt value -> {
                expression(value.value(), initialized);
                numeric &= number(value.value(), initialized);
            }
            case Stmt.Loop value -> {
                expression(value.count(), initialized);
                statement(value.body(), new HashSet<>(initialized));
            }
            case Stmt.ForEach value -> {
                expression(value.array(), initialized);
                statement(value.body(), new HashSet<>(initialized));
            }
            case Stmt.Break _, Stmt.Continue _ -> {
            }
        }
    }

    private void expression(Expr expression, Set<String> initialized) {
        if (expression instanceof Expr.Scope(Stmt.Block block)) {
            statement(block, new HashSet<>(initialized));
        } else {
            AstNodes.children(expression).forEach(child -> expression(child, initialized));
        }
    }

    private boolean number(Expr expression, Set<String> initialized) {
        return switch (expression) {
            case Expr.NumberLiteral _, Expr.This _, Expr.Unary _, Expr.Binary _, Expr.MathCall _, Expr.CompoundAssign _, Expr.Scope _ -> true;
            case Expr.VariableAccess value -> "temp".equals(value.scope()) && numericTemps.contains(value.name()) && initialized.contains(value.name());
            case Expr.VariableAssign value -> number(value.value(), initialized);
            case Expr.ArrayAssign value -> number(value.value(), initialized);
            case Expr.MemberAssign value -> number(value.value(), initialized);
            case Expr.Group value -> number(value.expression(), initialized);
            case Expr.Ternary value -> number(value.thenBranch(), initialized) && number(value.elseBranch(), initialized);
            case Expr.BinaryConditional value -> number(value.thenBranch(), initialized);
            case Expr.NullCoalescing value -> number(value.left(), initialized);
            case Expr.QueryCall value -> {
                MolangQueryDefinition definition = options.querySchema().get(value.name());
                yield MolangFunctions.builtinQuery(value.name()) || definition != null && definition.returnType() == MolangQueryDefinition.ValueType.NUMBER && !definition.nullable();
            }
            default -> false;
        };
    }
}
