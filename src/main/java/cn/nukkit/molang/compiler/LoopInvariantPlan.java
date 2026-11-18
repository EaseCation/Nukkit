package cn.nukkit.molang.compiler;

import cn.nukkit.molang.MolangCompileOptions;
import cn.nukkit.molang.parser.ast.Expr;
import cn.nukkit.molang.parser.ast.Stmt;
import cn.nukkit.molang.runtime.MolangQueryDefinition;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 为声明读取稳定的运行时提取循环中的数值不变子式。
 * 涉及可观察回调或引用别名的循环保留原求值顺序。
 */
final class LoopInvariantPlan {
    private static final Set<String> RANDOM_MATH = Set.of("random", "random_integer", "die_roll", "die_roll_integer");

    private final Set<ScopedName> written = new HashSet<>();
    private final Set<Expr> expressions = new LinkedHashSet<>();
    private final MolangCompileOptions options;
    private boolean disabled;

    LoopInvariantPlan(Stmt body, MolangCompileOptions options) {
        this.options = options;
        inspect(body, false);
        if (!disabled) {
            inspect(body, true);
        }
    }

    List<Expr> expressions() {
        return List.copyOf(expressions);
    }

    private void inspect(Stmt statement, boolean collect) {
        switch (statement) {
            case Stmt.Program value -> value.statements().forEach(child -> inspect(child, collect));
            case Stmt.Block value -> value.statements().forEach(child -> inspect(child, collect));
            case Stmt.ExpressionStmt value -> inspect(value.expression(), collect);
            case Stmt.ReturnStmt value -> inspect(value.value(), collect);
            case Stmt.Loop value -> {
                inspect(value.count(), collect);
                inspect(value.body(), collect);
            }
            case Stmt.ForEach _ -> disabled = true;
            case Stmt.Break _, Stmt.Continue _ -> {
            }
        }
    }

    private void inspect(Expr expression, boolean collect) {
        if (collect && numericResult(expression) && invariant(expression) && readsVariable(expression)) {
            expressions.add(expression);
            return;
        }
        switch (expression) {
            case Expr.VariableAssign value -> {
                written.add(new ScopedName(value.scope(), value.name()));
                inspect(value.value(), collect);
            }
            case Expr.CompoundAssign value -> {
                written.add(new ScopedName(value.scope(), value.name()));
                inspect(value.value(), collect);
            }
            case Expr.QueryCall value -> {
                MolangQueryDefinition definition = options.querySchema().get(value.name());
                if (MolangFunctions.effectfulQuery(value.name()) || !MolangFunctions.PURE_QUERIES.contains(value.name()) && (definition == null || !definition.isReadOnly())) {
                    disabled = true;
                }
                value.args().forEach(argument -> inspect(argument, collect));
            }
            case Expr.MathCall value -> {
                if (RANDOM_MATH.contains(value.function())) {
                    disabled = true;
                }
                value.args().forEach(argument -> inspect(argument, collect));
            }
            case Expr.Binary value -> {
                inspect(value.left(), collect);
                inspect(value.right(), collect);
            }
            case Expr.Unary value -> inspect(value.operand(), collect);
            case Expr.Ternary value -> {
                inspect(value.condition(), collect);
                inspect(value.thenBranch(), collect);
                inspect(value.elseBranch(), collect);
            }
            case Expr.BinaryConditional value -> {
                inspect(value.condition(), collect);
                inspect(value.thenBranch(), collect);
            }
            case Expr.NullCoalescing value -> {
                inspect(value.left(), collect);
                inspect(value.right(), collect);
            }
            case Expr.Group value -> inspect(value.expression(), collect);
            case Expr.Scope value -> inspect(value.block(), collect);
            case Expr.ArrayAccess _, Expr.ArrayAssign _, Expr.MemberAccess _, Expr.MemberAssign _, Expr.IndexAccess _, Expr.ContextSwitch _ -> disabled = true;
            case Expr.NumberLiteral _, Expr.StringLiteral _, Expr.VariableAccess _, Expr.This _, Expr.Break _, Expr.Continue _ -> {
            }
        }
    }

    private boolean invariant(Expr expression) {
        return switch (expression) {
            case Expr.NumberLiteral _, Expr.StringLiteral _ -> true;
            case Expr.VariableAccess value -> ("variable".equals(value.scope()) || "temp".equals(value.scope()) || "context".equals(value.scope()))
                    && written.stream().noneMatch(name -> name.overlaps(value.scope(), value.name()));
            case Expr.Group value -> invariant(value.expression());
            case Expr.Unary value -> invariant(value.operand());
            case Expr.Binary value -> invariant(value.left()) && invariant(value.right());
            case Expr.Ternary value -> invariant(value.condition()) && invariant(value.thenBranch()) && invariant(value.elseBranch());
            case Expr.BinaryConditional value -> invariant(value.condition()) && invariant(value.thenBranch());
            case Expr.NullCoalescing value -> invariant(value.left()) && invariant(value.right());
            case Expr.MathCall value -> !RANDOM_MATH.contains(value.function()) && value.args().stream().allMatch(this::invariant);
            case Expr.QueryCall value -> queryInvariant(value) && value.args().stream().allMatch(this::invariant);
            default -> false;
        };
    }

    private boolean numericResult(Expr expression) {
        return switch (expression) {
            case Expr.Unary _, Expr.Binary _, Expr.MathCall _ -> true;
            case Expr.QueryCall value -> {
                MolangQueryDefinition definition = options.querySchema().get(value.name());
                yield MolangFunctions.PURE_QUERIES.contains(value.name()) || definition != null && definition.returnType() == MolangQueryDefinition.ValueType.NUMBER && !definition.nullable();
            }
            case Expr.Group value -> numericResult(value.expression());
            default -> false;
        };
    }

    private boolean readsVariable(Expr expression) {
        return switch (expression) {
            case Expr.VariableAccess value -> "variable".equals(value.scope()) || "context".equals(value.scope());
            case Expr.Group value -> readsVariable(value.expression());
            case Expr.Unary value -> readsVariable(value.operand());
            case Expr.Binary value -> readsVariable(value.left()) || readsVariable(value.right());
            case Expr.Ternary value -> readsVariable(value.condition()) || readsVariable(value.thenBranch()) || readsVariable(value.elseBranch());
            case Expr.BinaryConditional value -> readsVariable(value.condition()) || readsVariable(value.thenBranch());
            case Expr.NullCoalescing value -> readsVariable(value.left()) || readsVariable(value.right());
            case Expr.MathCall value -> value.args().stream().anyMatch(this::readsVariable);
            case Expr.QueryCall value -> !MolangFunctions.PURE_QUERIES.contains(value.name()) || value.args().stream().anyMatch(this::readsVariable);
            default -> false;
        };
    }

    private record ScopedName(String scope, String path) {
        boolean overlaps(String otherScope, String otherPath) {
            return scope.equals(otherScope) && (path.equals(otherPath) || path.startsWith(otherPath + ".") || otherPath.startsWith(path + "."));
        }
    }

    private boolean queryInvariant(Expr.QueryCall call) {
        // 只读及稳定声明不保证不会抛异常，宿主 query 不能越过条件被推测执行。
        return MolangFunctions.PURE_QUERIES.contains(call.name());
    }
}
