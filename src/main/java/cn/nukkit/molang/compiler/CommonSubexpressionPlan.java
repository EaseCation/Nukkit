package cn.nukkit.molang.compiler;

import cn.nukkit.molang.MolangCompileOptions;
import cn.nukkit.molang.parser.ast.Expr;
import cn.nukkit.molang.parser.ast.Stmt;
import cn.nukkit.molang.runtime.MolangQueryDefinition;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 在有明确读写合同的直线区域复用数值子式。分支、循环边界和未知回调会清空可用结果。
 */
final class CommonSubexpressionPlan {
    private final MolangCompileOptions options;
    private final Object2IntMap<Expr> uses = new Object2IntOpenHashMap<>();
    private final Map<Expr, String> available = new HashMap<>();
    private int sequence;

    CommonSubexpressionPlan(MolangCompileOptions options) {
        this.options = options;
    }

    Stmt.Program optimize(Stmt.Program program) {
        boolean[] observesTemps = {false};
        AstNodes.visit(program, _ -> {}, expression -> {
            if (expression instanceof Expr.ContextSwitch) {
                observesTemps[0] = true;
            }
            if (expression instanceof Expr.QueryCall call && MolangFunctions.effectfulQuery(call.name())) {
                observesTemps[0] = true;
            }
            if (expression instanceof Expr.QueryCall call && !MolangFunctions.PURE_QUERIES.contains(call.name())) {
                MolangQueryDefinition definition = options.querySchema().get(call.name());
                if (definition == null || definition.mayObserveTemps()) {
                    observesTemps[0] = true;
                }
            }
            if (candidate(expression)) {
                uses.mergeInt(expression, 1, Integer::sum);
            }
        });
        if (observesTemps[0]) {
            return program;
        }
        return (Stmt.Program) statement(program);
    }

    private Stmt statement(Stmt statement) {
        return switch (statement) {
            case Stmt.Program value -> new Stmt.Program(statements(value.statements()), value.isComplex());
            case Stmt.Block value -> new Stmt.Block(statements(value.statements()));
            case Stmt.ExpressionStmt value -> new Stmt.ExpressionStmt(expression(value.expression()));
            case Stmt.ReturnStmt value -> new Stmt.ReturnStmt(expression(value.value()));
            case Stmt.Loop value -> {
                Expr count = expression(value.count());
                available.clear();
                Stmt body = statement(value.body());
                available.clear();
                yield new Stmt.Loop(count, body);
            }
            case Stmt.ForEach value -> {
                Expr array = expression(value.array());
                available.clear();
                Stmt body = statement(value.body());
                available.clear();
                yield new Stmt.ForEach(value.target(), array, body);
            }
            case Stmt.Break _, Stmt.Continue _ -> {
                available.clear();
                yield statement;
            }
        };
    }

    private List<Stmt> statements(List<Stmt> statements) {
        List<Stmt> result = new ArrayList<>(statements.size());
        for (Stmt statement : statements) {
            result.add(statement(statement));
        }
        return List.copyOf(result);
    }

    private Expr expression(Expr expression) {
        boolean cache = candidate(expression) && uses.getInt(expression) > 1;
        if (cache) {
            String name = available.get(expression);
            if (name != null) {
                return new Expr.VariableAccess("temp", name);
            }
        }
        Expr rewritten = switch (expression) {
            case Expr.VariableAssign value -> {
                Expr assigned = expression(value.value());
                invalidate(value.scope(), value.name());
                yield new Expr.VariableAssign(value.scope(), value.name(), assigned);
            }
            case Expr.CompoundAssign value -> {
                Expr assigned = expression(value.value());
                invalidate(value.scope(), value.name());
                yield new Expr.CompoundAssign(value.scope(), value.name(), value.operator(), assigned);
            }
            case Expr.Binary value -> {
                Expr left = expression(value.left());
                boolean conditional = "&&".equals(value.operator()) || "||".equals(value.operator());
                if (conditional) {
                    available.clear();
                }
                Expr right = expression(value.right());
                if (conditional) {
                    available.clear();
                }
                yield new Expr.Binary(left, value.operator(), right);
            }
            case Expr.Unary value -> new Expr.Unary(value.operator(), expression(value.operand()));
            case Expr.Group value -> new Expr.Group(expression(value.expression()));
            case Expr.Ternary value -> {
                Expr condition = expression(value.condition());
                available.clear();
                Expr first = expression(value.thenBranch());
                available.clear();
                Expr second = expression(value.elseBranch());
                available.clear();
                yield new Expr.Ternary(condition, first, second);
            }
            case Expr.BinaryConditional value -> {
                Expr condition = expression(value.condition());
                available.clear();
                Expr first = expression(value.thenBranch());
                available.clear();
                yield new Expr.BinaryConditional(condition, first);
            }
            case Expr.NullCoalescing value -> {
                Expr left = expression(value.left());
                available.clear();
                Expr right = expression(value.right());
                available.clear();
                yield new Expr.NullCoalescing(left, right);
            }
            case Expr.MathCall value -> new Expr.MathCall(value.function(), expressions(value.args()));
            case Expr.QueryCall value -> {
                List<Expr> arguments = expressions(value.args());
                if (!readOnlyQuery(value.name())) {
                    available.clear();
                }
                yield new Expr.QueryCall(value.name(), arguments);
            }
            case Expr.Scope value -> {
                available.clear();
                Stmt.Block body = (Stmt.Block) statement(value.block());
                available.clear();
                yield new Expr.Scope(body);
            }
            case Expr.ArrayAccess value -> new Expr.ArrayAccess(value.scope(), value.name(), expression(value.index()));
            case Expr.ArrayAssign value -> {
                Expr index = expression(value.index());
                Expr assigned = expression(value.value());
                available.clear();
                yield new Expr.ArrayAssign(value.scope(), value.name(), index, assigned);
            }
            case Expr.MemberAssign value -> {
                Expr target = expression(value.target());
                Expr assigned = expression(value.value());
                available.clear();
                yield new Expr.MemberAssign(target, value.name(), assigned);
            }
            case Expr.MemberAccess value -> new Expr.MemberAccess(expression(value.target()), value.name());
            case Expr.IndexAccess value -> new Expr.IndexAccess(expression(value.target()), expression(value.index()));
            case Expr.ContextSwitch _ -> {
                // 被引用实体可能提供不同的 getter 合同，不跨上下文复用调用者的稳定性假设。
                available.clear();
                yield expression;
            }
            default -> expression;
        };
        if (cache) {
            String name = "\u0000cse_" + sequence++;
            available.put(expression, name);
            return new Expr.VariableAssign("temp", name, rewritten);
        }
        return rewritten;
    }

    private List<Expr> expressions(List<Expr> input) {
        List<Expr> result = new ArrayList<>(input.size());
        input.forEach(value -> result.add(expression(value)));
        return List.copyOf(result);
    }

    private boolean readOnlyQuery(String name) {
        if (MolangFunctions.effectfulQuery(name)) {
            return false;
        }
        MolangQueryDefinition definition = options.querySchema().get(name);
        return MolangFunctions.PURE_QUERIES.contains(name) || definition != null && definition.isReadOnly();
    }

    private boolean candidate(Expr expression) {
        boolean numeric = expression instanceof Expr.Unary || expression instanceof Expr.Binary || expression instanceof Expr.MathCall;
        if (expression instanceof Expr.QueryCall call) {
            MolangQueryDefinition definition = options.querySchema().get(call.name());
            numeric = MolangFunctions.PURE_QUERIES.contains(call.name()) || definition != null
                    && definition.returnType() == MolangQueryDefinition.ValueType.NUMBER;
        }
        return numeric && pure(expression);
    }

    private boolean pure(Expr expression) {
        return switch (expression) {
            case Expr.NumberLiteral _, Expr.StringLiteral _ -> true;
            case Expr.VariableAccess value -> "temp".equals(value.scope()) || options.stableVariableReads() && ("variable".equals(value.scope()) || "context".equals(value.scope()));
            case Expr.Group value -> pure(value.expression());
            case Expr.Unary value -> pure(value.operand());
            case Expr.Binary value -> pure(value.left()) && pure(value.right());
            case Expr.Ternary value -> pure(value.condition()) && pure(value.thenBranch()) && pure(value.elseBranch());
            case Expr.BinaryConditional value -> pure(value.condition()) && pure(value.thenBranch());
            case Expr.NullCoalescing value -> pure(value.left()) && pure(value.right());
            case Expr.MathCall value -> {
                MolangFunctions.MathFunction function = MolangFunctions.math(value.function());
                yield function != null && function.pure() && value.args().stream().allMatch(this::pure);
            }
            case Expr.QueryCall value -> {
                MolangQueryDefinition definition = options.querySchema().get(value.name());
                boolean stable = MolangFunctions.PURE_QUERIES.contains(value.name())
                        || !MolangFunctions.effectfulQuery(value.name()) && definition != null
                        && (definition.isPure() || definition.isEvaluationStable());
                yield stable && value.args().stream().allMatch(this::pure);
            }
            default -> false;
        };
    }

    private void invalidate(String scope, String path) {
        String changed = scope + "." + path;
        available.keySet().removeIf(value -> dependencies(value).stream().anyMatch(read -> dependencyOverlaps(read, changed)));
    }

    private Set<String> dependencies(Expr expression) {
        Set<String> result = new HashSet<>();
        AstNodes.visit(expression, _ -> {}, node -> {
            if (node instanceof Expr.VariableAccess(String scope, String name)) {
                result.add(scope + "." + name);
            } else if (node instanceof Expr.QueryCall query) {
                MolangQueryDefinition definition = options.querySchema().get(query.name());
                if (definition != null) {
                    result.addAll(definition.reads());
                }
            }
        });
        return result;
    }

    static boolean dependencyOverlaps(String dependency, String changed) {
        String normalized = dependency.startsWith("v.") ? "variable." + dependency.substring(2)
                : dependency.startsWith("t.") ? "temp." + dependency.substring(2)
                : dependency.startsWith("c.") ? "context." + dependency.substring(2) : dependency;
        if (normalized.equals("*")) {
            return true;
        }
        if (normalized.endsWith(".*")) {
            normalized = normalized.substring(0, normalized.length() - 2);
        }
        return AstOptimizer.overlaps(normalized, changed);
    }
}
