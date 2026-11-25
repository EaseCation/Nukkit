package cn.nukkit.molang.compiler;

import cn.nukkit.molang.MolangCompileOptions;
import cn.nukkit.molang.MolangVersion;
import cn.nukkit.molang.parser.ast.Expr;
import cn.nukkit.molang.parser.ast.Stmt;
import cn.nukkit.molang.runtime.MolangQueryDefinition;
import cn.nukkit.molang.runtime.MolangQuerySchema;
import cn.nukkit.molang.runtime.MolangValues;

import javax.annotation.Nullable;
import java.util.*;

/**
 * 数值和类型表达式共享的语义优化阶段。只折叠确定值，不重排浮点运算或删除副作用。
 */
public final class AstOptimizer {
    private final MolangCompileOptions options;

    private AstOptimizer(MolangCompileOptions options) {
        this.options = options;
    }

    public static Stmt.Program optimize(Stmt.Program program, MolangCompileOptions options) {
        AstOptimizer optimizer = new AstOptimizer(options);
        optimizer.validate(program, 0);
        Stmt.Program folded = new Stmt.Program(optimizer.statements(program.statements(), new HashMap<>()), program.isComplex());
        Stmt.Program shared = new CommonSubexpressionPlan(options).optimize(folded);
        return prune(shared, options);
    }

    private List<Stmt> statements(List<Stmt> input, Map<String, Expr> constants) {
        List<Stmt> result = new ArrayList<>();
        for (Stmt statement : input) {
            Stmt optimized = statement(statement, constants);
            result.add(optimized);
            if (optimized instanceof Stmt.ReturnStmt || optimized instanceof Stmt.Break || optimized instanceof Stmt.Continue) {
                break;
            }
        }
        return List.copyOf(result);
    }

    private Stmt statement(Stmt statement, Map<String, Expr> constants) {
        return switch (statement) {
            case Stmt.Program value -> new Stmt.Program(statements(value.statements(), constants), value.isComplex());
            case Stmt.Block value -> new Stmt.Block(statements(value.statements(), constants));
            case Stmt.ExpressionStmt value -> new Stmt.ExpressionStmt(expression(value.expression(), constants));
            case Stmt.ReturnStmt value -> new Stmt.ReturnStmt(expression(value.value(), constants));
            case Stmt.Loop value -> {
                Expr count = expression(value.count(), constants);
                Object amount = literal(count);
                if (amount != null && (int) MolangValues.toFloat(amount) <= 0) {
                    yield new Stmt.Block(List.of());
                }
                Set<String> changed = writtenTemps(value.body());
                Map<String, Expr> bodyConstants = new HashMap<>(constants);
                invalidate(bodyConstants, changed);
                Stmt body = statement(value.body(), bodyConstants);
                invalidate(constants, changed);
                yield new Stmt.Loop(count, body);
            }
            case Stmt.ForEach value -> {
                Expr array = expression(value.array(), constants);
                Set<String> changed = writtenTemps(value.body());
                if ("temp".equals(value.target().scope())) {
                    changed.add(value.target().name());
                }
                Map<String, Expr> bodyConstants = new HashMap<>(constants);
                invalidate(bodyConstants, changed);
                Stmt body = statement(value.body(), bodyConstants);
                invalidate(constants, changed);
                yield new Stmt.ForEach(value.target(), array, body);
            }
            case Stmt.Break _, Stmt.Continue _ -> statement;
        };
    }

    private Expr expression(Expr expression, Map<String, Expr> constants) {
        return switch (expression) {
            case Expr.NumberLiteral _, Expr.StringLiteral _, Expr.This _, Expr.Break _, Expr.Continue _ -> expression;
            case Expr.VariableAccess value -> "temp".equals(value.scope()) ? constants.getOrDefault(value.name(), value) : value;
            case Expr.VariableAssign value -> {
                Expr assigned = expression(value.value(), constants);
                assignConstant(constants, value.scope(), value.name(), assigned);
                yield new Expr.VariableAssign(value.scope(), value.name(), assigned);
            }
            case Expr.CompoundAssign value -> {
                Expr assigned = expression(new Expr.Binary(new Expr.VariableAccess(value.scope(), value.name()),
                        value.operator().substring(0, 1), value.value()), constants);
                assignConstant(constants, value.scope(), value.name(), assigned);
                yield new Expr.VariableAssign(value.scope(), value.name(), assigned);
            }
            case Expr.Group value -> expression(value.expression(), constants);
            case Expr.Unary value -> {
                Expr operand = expression(value.operand(), constants);
                Object constant = literal(operand);
                if (constant == null) {
                    yield new Expr.Unary(value.operator(), operand);
                }
                float number = MolangValues.toFloat(constant);
                yield new Expr.NumberLiteral("!".equals(value.operator()) ? number == 0 ? 1 : 0 : -number);
            }
            case Expr.Binary value -> binary(value, constants);
            case Expr.Ternary value -> conditional(value.condition(), value.thenBranch(), value.elseBranch(), constants);
            case Expr.BinaryConditional value -> conditional(value.condition(), value.thenBranch(), new Expr.NumberLiteral(0), constants);
            case Expr.NullCoalescing value -> {
                Expr left = expression(value.left(), constants);
                if (literal(left) != null) {
                    yield left;
                }
                Map<String, Expr> alternative = new HashMap<>(constants);
                Expr right = expression(value.right(), alternative);
                merge(constants, constants, alternative);
                yield new Expr.NullCoalescing(left, right);
            }
            case Expr.MathCall value -> math(value, constants);
            case Expr.QueryCall value -> {
                List<Expr> arguments = expressions(value.args(), constants);
                if (mayWriteTemps(value.name())) {
                    constants.clear();
                }
                Expr folded = foldQuery(value.name(), arguments);
                yield folded != null ? folded : new Expr.QueryCall(value.name(), arguments);
            }
            case Expr.Scope value -> new Expr.Scope((Stmt.Block) statement(value.block(), constants));
            case Expr.ArrayAccess value -> new Expr.ArrayAccess(value.scope(), value.name(), expression(value.index(), constants));
            case Expr.ArrayAssign value -> {
                Expr index = expression(value.index(), constants);
                Expr assigned = expression(value.value(), constants);
                if ("temp".equals(value.scope())) {
                    invalidate(constants, value.name());
                }
                yield new Expr.ArrayAssign(value.scope(), value.name(), index, assigned);
            }
            case Expr.IndexAccess value -> new Expr.IndexAccess(expression(value.target(), constants), expression(value.index(), constants));
            case Expr.MemberAccess value -> new Expr.MemberAccess(expression(value.target(), constants), value.name());
            case Expr.MemberAssign value -> {
                Expr target = expression(value.target(), constants);
                Expr assigned = expression(value.value(), constants);
                constants.clear();
                yield new Expr.MemberAssign(target, value.name(), assigned);
            }
            case Expr.ContextSwitch value -> {
                Expr target = expression(value.target(), constants);
                constants.clear();
                AstOptimizer foreign = new AstOptimizer(new MolangCompileOptions(options.version(), MolangQuerySchema.EMPTY, false));
                Expr switched = foreign.expression(value.expression(), new HashMap<>());
                yield new Expr.ContextSwitch(target, switched);
            }
        };
    }

    private Expr binary(Expr.Binary binary, Map<String, Expr> constants) {
        Expr rawRight = legacyDivisor(binary.operator(), binary.right());
        Expr left = expression(binary.left(), constants);
        Object first = literal(left);
        if ("&&".equals(binary.operator()) || "||".equals(binary.operator())) {
            boolean and = "&&".equals(binary.operator());
            if (first != null) {
                if ((MolangValues.toFloat(first) != 0) != and) {
                    return new Expr.NumberLiteral(and ? 0 : 1);
                }
                Expr right = expression(rawRight, constants);
                Object last = literal(right);
                return last != null ? new Expr.NumberLiteral(MolangValues.toFloat(last) != 0 ? 1 : 0)
                        : new Expr.Unary("!", new Expr.Unary("!", right));
            }
            Map<String, Expr> optional = new HashMap<>(constants);
            Expr right = expression(rawRight, optional);
            merge(constants, constants, optional);
            return new Expr.Binary(left, binary.operator(), right);
        }
        Expr right = expression(rawRight, constants);
        Object second = literal(right);
        if (first == null || second == null) {
            return new Expr.Binary(left, binary.operator(), right);
        }
        return new Expr.NumberLiteral(foldBinary(binary.operator(), first, second));
    }

    private Expr conditional(Expr condition, Expr thenBranch, Expr elseBranch, Map<String, Expr> constants) {
        Expr selected = expression(condition, constants);
        Object value = literal(selected);
        if (value != null) {
            return expression(MolangValues.toFloat(value) != 0 ? thenBranch : elseBranch, constants);
        }
        Map<String, Expr> first = new HashMap<>(constants);
        Map<String, Expr> second = new HashMap<>(constants);
        Expr thenValue = expression(thenBranch, first);
        Expr elseValue = expression(elseBranch, second);
        merge(constants, first, second);
        return new Expr.Ternary(selected, thenValue, elseValue);
    }

    private Expr math(Expr.MathCall call, Map<String, Expr> constants) {
        MolangFunctions.MathFunction function = MolangFunctions.math(call.function());
        if (function == null) {
            throw new MolangCompileException("Unknown math function: math." + call.function());
        }
        List<Expr> arguments = new ArrayList<>(function.arity());
        float[] values = new float[function.arity()];
        boolean fold = function.pure();
        for (int index = 0; index < function.arity(); index++) {
            Expr argument = index < call.args().size()
                    ? expression(call.args().get(index), constants) : new Expr.NumberLiteral(0);
            arguments.add(argument);
            Object literal = literal(argument);
            if (literal == null) {
                fold = false;
            } else {
                values[index] = MolangValues.toFloat(literal);
            }
        }
        return fold ? new Expr.NumberLiteral(function.evaluate(values))
                : new Expr.MathCall(call.function(), List.copyOf(arguments));
    }

    private Expr legacyDivisor(String operator, Expr divisor) {
        if ("/".equals(operator) && !options.version().supports(MolangVersion.DIVIDE_BY_NEGATIVE_VALUE) && !staticValue(divisor)) {
            // 1.19.60 之前的语义只对动态分母取绝对值；必须先于临时变量常量传播判定。
            return new Expr.MathCall("abs", List.of(divisor));
        }
        return divisor;
    }

    private static boolean staticValue(Expr expression) {
        return switch (expression) {
            case Expr.NumberLiteral _, Expr.StringLiteral _ -> true;
            case Expr.Group value -> staticValue(value.expression());
            case Expr.Unary value -> staticValue(value.operand());
            case Expr.Binary value -> staticValue(value.left()) && staticValue(value.right());
            case Expr.Ternary value -> staticValue(value.condition()) && staticValue(value.thenBranch()) && staticValue(value.elseBranch());
            case Expr.BinaryConditional value -> staticValue(value.condition()) && staticValue(value.thenBranch());
            case Expr.NullCoalescing value -> staticValue(value.left()) && staticValue(value.right());
            case Expr.MathCall value -> {
                MolangFunctions.MathFunction function = MolangFunctions.math(value.function());
                yield function != null && function.pure() && value.args().stream().limit(function.arity()).allMatch(AstOptimizer::staticValue);
            }
            case Expr.QueryCall value -> MolangFunctions.PURE_QUERIES.contains(value.name()) && value.args().stream().allMatch(AstOptimizer::staticValue);
            default -> false;
        };
    }

    private List<Expr> expressions(List<Expr> values, Map<String, Expr> constants) {
        List<Expr> result = new ArrayList<>(values.size());
        for (Expr value : values) {
            result.add(expression(value, constants));
        }
        return List.copyOf(result);
    }

    @Nullable
    static Object literal(Expr expression) {
        return switch (expression) {
            case Expr.NumberLiteral value -> value.value();
            case Expr.StringLiteral value -> value.value();
            default -> null;
        };
    }

    private static float foldBinary(String operator, Object left, Object right) {
        if ("==".equals(operator)) {
            return MolangValues.equal(left, right) ? 1 : 0;
        }
        if ("!=".equals(operator)) {
            return MolangValues.equal(left, right) ? 0 : 1;
        }
        if (left instanceof String || right instanceof String) {
            return 0;
        }
        float first = MolangValues.toFloat(left);
        float second = MolangValues.toFloat(right);
        return switch (operator) {
            case "+" -> first + second;
            case "-" -> first - second;
            case "*" -> first * second;
            case "/" -> second == 0 ? 0 : first / second;
            case "%" -> second == 0 ? 0 : first % second;
            case "<" -> first < second ? 1 : 0;
            case "<=" -> first <= second ? 1 : 0;
            case ">" -> first > second ? 1 : 0;
            case ">=" -> first >= second ? 1 : 0;
            default -> throw new MolangCompileException("Unknown binary operator: " + operator);
        };
    }

    @Nullable
    private static Expr foldQuery(String name, List<Expr> arguments) {
        if (!MolangFunctions.PURE_QUERIES.contains(name) || arguments.stream().anyMatch(value -> literal(value) == null)) {
            return null;
        }
        if ("count".equals(name)) {
            return new Expr.NumberLiteral(arguments.size());
        }
        if ("in_range".equals(name)) {
            if (arguments.size() != 3) {
                return new Expr.NumberLiteral(0);
            }
            float value = MolangValues.toFloat(literal(arguments.get(0)));
            return new Expr.NumberLiteral(value >= MolangValues.toFloat(literal(arguments.get(1))) && value <= MolangValues.toFloat(literal(arguments.get(2))) ? 1 : 0);
        }
        if (arguments.size() < 2) {
            return new Expr.NumberLiteral(0);
        }
        Object first = literal(arguments.getFirst());
        for (int index = 1; index < arguments.size(); index++) {
            Object value = literal(arguments.get(index));
            boolean equal = MolangValues.equal(first, value);
            if ("all".equals(name) && !equal || "any".equals(name) && equal) {
                return new Expr.NumberLiteral("any".equals(name) ? 1 : 0);
            }
            if ("approx_eq".equals(name) && Math.abs(MolangValues.toFloat(value) - MolangValues.toFloat(first)) >= 0.000001f) {
                return new Expr.NumberLiteral(0);
            }
        }
        return new Expr.NumberLiteral("any".equals(name) ? 0 : 1);
    }

    private boolean mayWriteTemps(String query) {
        if (MolangFunctions.effectfulQuery(query)) {
            return true;
        }
        if (MolangFunctions.PURE_QUERIES.contains(query)) {
            return false;
        }
        MolangQueryDefinition definition = options.querySchema().get(query);
        return definition == null || definition.mayWriteTemps();
    }

    private Set<String> writtenTemps(Stmt statement) {
        Set<String> written = new HashSet<>();
        AstNodes.visit(statement, node -> {
            if (node instanceof Stmt.ForEach loop && "temp".equals(loop.target().scope())) {
                written.add(loop.target().name());
            }
        }, node -> {
            switch (node) {
                case Expr.VariableAssign value when "temp".equals(value.scope()) -> written.add(value.name());
                case Expr.CompoundAssign value when "temp".equals(value.scope()) -> written.add(value.name());
                case Expr.ArrayAssign value when "temp".equals(value.scope()) -> written.add(value.name());
                case Expr.QueryCall value when mayWriteTemps(value.name()) -> written.add("*");
                case Expr.MemberAssign _, Expr.ContextSwitch _ -> written.add("*");
                default -> {
                }
            }
        });
        return written;
    }

    private static void assignConstant(Map<String, Expr> constants, String scope, String name, Expr assigned) {
        if ("temp".equals(scope)) {
            invalidate(constants, name);
            if (literal(assigned) != null) {
                constants.put(name, assigned);
            }
        }
    }

    static boolean overlaps(String first, String second) {
        return first.equals(second) || first.startsWith(second + ".") || second.startsWith(first + ".");
    }

    private static void invalidate(Map<String, Expr> constants, Set<String> changed) {
        if (changed.contains("*")) {
            constants.clear();
        } else {
            changed.forEach(name -> invalidate(constants, name));
        }
    }

    private static void invalidate(Map<String, Expr> constants, String changed) {
        constants.keySet().removeIf(name -> overlaps(name, changed));
    }

    private static void merge(Map<String, Expr> target, Map<String, Expr> first, Map<String, Expr> second) {
        Map<String, Expr> intersection = new HashMap<>();
        first.forEach((name, value) -> {
            Expr other = second.get(name);
            if (sameConstant(value, other)) {
                intersection.put(name, value);
            }
        });
        target.clear();
        target.putAll(intersection);
    }

    private static boolean sameConstant(Expr first, @Nullable Expr second) {
        if (first instanceof Expr.NumberLiteral(float a) && second instanceof Expr.NumberLiteral(float b)) {
            return Float.floatToRawIntBits(a) == Float.floatToRawIntBits(b);
        }
        return first instanceof Expr.StringLiteral && first.equals(second);
    }

    private void validate(Stmt statement, int loops) {
        switch (statement) {
            case Stmt.Program value -> value.statements().forEach(child -> validate(child, loops));
            case Stmt.Block value -> value.statements().forEach(child -> validate(child, loops));
            case Stmt.ExpressionStmt value -> validate(value.expression(), loops);
            case Stmt.ReturnStmt value -> validate(value.value(), loops);
            case Stmt.Loop value -> {
                validate(value.count(), loops);
                validate(value.body(), loops + 1);
            }
            case Stmt.ForEach value -> {
                validate(value.array(), loops);
                validate(value.body(), loops + 1);
            }
            case Stmt.Break _, Stmt.Continue _ -> {
                if (loops == 0) {
                    throw new MolangCompileException("Loop control is only valid inside a loop");
                }
            }
        }
    }

    private void validate(Expr expression, int loops) {
        if ((expression instanceof Expr.Break || expression instanceof Expr.Continue) && loops == 0) {
            throw new MolangCompileException("Loop control is only valid inside a loop");
        }
        if (expression instanceof Expr.MathCall call && MolangFunctions.math(call.function()) == null) {
            throw new MolangCompileException("Unknown math function: math." + call.function());
        }
        if (expression instanceof Expr.Scope(Stmt.Block block)) {
            validate(block, loops);
        } else {
            AstNodes.children(expression).forEach(child -> validate(child, loops));
        }
    }

    private static Stmt.Program prune(Stmt.Program program, MolangCompileOptions options) {
        Set<String> read = new HashSet<>();
        AstNodes.visit(program, _ -> {}, value -> {
            if (value instanceof Expr.VariableAccess(String scope, String name) && "temp".equals(scope)) {
                read.add(name);
            }
        });
        Set<String> dead = new HashSet<>(new NumericLocalPlan(program, options).numericTemps());
        dead.removeAll(read);
        return new Stmt.Program(pruneStatements(program.statements(), dead, program.isComplex()), program.isComplex());
    }

    private static List<Stmt> pruneStatements(List<Stmt> statements, Set<String> dead, boolean discardConstants) {
        List<Stmt> result = new ArrayList<>(statements.size());
        for (Stmt statement : statements) {
            Stmt selected = switch (statement) {
                case Stmt.ExpressionStmt value -> new Stmt.ExpressionStmt(pruneExpression(value.expression(), dead));
                case Stmt.ReturnStmt value -> new Stmt.ReturnStmt(pruneExpression(value.value(), dead));
                case Stmt.Block value -> new Stmt.Block(pruneStatements(value.statements(), dead, true));
                case Stmt.Loop value -> new Stmt.Loop(pruneExpression(value.count(), dead), pruneNested(value.body(), dead));
                case Stmt.ForEach value -> new Stmt.ForEach(value.target(), pruneExpression(value.array(), dead), pruneNested(value.body(), dead));
                default -> statement;
            };
            if (!discardConstants || !(selected instanceof Stmt.ExpressionStmt(Expr value)) || literal(value) == null) {
                result.add(selected);
            }
        }
        return List.copyOf(result);
    }

    private static Stmt pruneNested(Stmt statement, Set<String> dead) {
        List<Stmt> result = pruneStatements(List.of(statement), dead, true);
        return result.size() == 1 ? result.getFirst() : new Stmt.Block(result);
    }

    private static Expr pruneExpression(Expr expression, Set<String> dead) {
        return switch (expression) {
            case Expr.VariableAssign value -> {
                Expr assigned = pruneExpression(value.value(), dead);
                yield "temp".equals(value.scope()) && dead.contains(value.name()) ? assigned
                        : new Expr.VariableAssign(value.scope(), value.name(), assigned);
            }
            case Expr.CompoundAssign value -> new Expr.CompoundAssign(value.scope(), value.name(), value.operator(), pruneExpression(value.value(), dead));
            case Expr.Binary value -> new Expr.Binary(pruneExpression(value.left(), dead), value.operator(), pruneExpression(value.right(), dead));
            case Expr.Unary value -> new Expr.Unary(value.operator(), pruneExpression(value.operand(), dead));
            case Expr.Group value -> new Expr.Group(pruneExpression(value.expression(), dead));
            case Expr.Ternary value -> new Expr.Ternary(pruneExpression(value.condition(), dead), pruneExpression(value.thenBranch(), dead), pruneExpression(value.elseBranch(), dead));
            case Expr.BinaryConditional value -> new Expr.BinaryConditional(pruneExpression(value.condition(), dead), pruneExpression(value.thenBranch(), dead));
            case Expr.NullCoalescing value -> new Expr.NullCoalescing(pruneExpression(value.left(), dead), pruneExpression(value.right(), dead));
            case Expr.MathCall value -> new Expr.MathCall(value.function(), value.args().stream().map(argument -> pruneExpression(argument, dead)).toList());
            case Expr.QueryCall value -> new Expr.QueryCall(value.name(), value.args().stream().map(argument -> pruneExpression(argument, dead)).toList());
            case Expr.Scope value -> new Expr.Scope((Stmt.Block) pruneNested(value.block(), dead));
            case Expr.ArrayAccess value -> new Expr.ArrayAccess(value.scope(), value.name(), pruneExpression(value.index(), dead));
            case Expr.ArrayAssign value -> new Expr.ArrayAssign(value.scope(), value.name(), pruneExpression(value.index(), dead), pruneExpression(value.value(), dead));
            case Expr.IndexAccess value -> new Expr.IndexAccess(pruneExpression(value.target(), dead), pruneExpression(value.index(), dead));
            case Expr.MemberAccess value -> new Expr.MemberAccess(pruneExpression(value.target(), dead), value.name());
            case Expr.MemberAssign value -> new Expr.MemberAssign(pruneExpression(value.target(), dead), value.name(), pruneExpression(value.value(), dead));
            case Expr.ContextSwitch value -> new Expr.ContextSwitch(pruneExpression(value.target(), dead), pruneExpression(value.expression(), dead));
            default -> expression;
        };
    }
}
