package cn.nukkit.molang.compiler;

import cn.nukkit.molang.MolangBoundExpression;
import cn.nukkit.molang.MolangExpression;
import cn.nukkit.molang.parser.ast.Expr;
import cn.nukkit.molang.parser.ast.Stmt;
import cn.nukkit.molang.runtime.MolangBindingPlan;
import cn.nukkit.molang.runtime.MolangRuntime;
import cn.nukkit.molang.runtime.MolangValues;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

/**
 * 普通入口在调用方的作用域中保留迭代目标。只有自行创建作用域的入口才能省略失用的写回。
 * 两个执行实例共享生成类和符号计划。
 */
record OwnedIteration(MolangExpression ordinary, MolangExpression owned) implements MolangExpression {
    static MolangExpression original(MolangExpression expression) {
        return expression instanceof OwnedIteration choice ? choice.ordinary : expression;
    }

    private MolangExpression forRuntime(MolangRuntime runtime) {
        return MolangValues.isBuiltinStorage(runtime) ? owned : ordinary;
    }

    @Override
    public float evaluate(MolangRuntime runtime) {
        return forRuntime(runtime).evaluate(runtime);
    }

    @Override
    @Nullable
    public Object evaluateValue(MolangRuntime runtime) {
        return forRuntime(runtime).evaluateValue(runtime);
    }

    @Override
    public MolangBoundExpression bind(MolangRuntime runtime) {
        return forRuntime(runtime).bind(runtime);
    }

    @Override
    public MolangBindingPlan bindingPlan() {
        return ordinary.bindingPlan();
    }

    @Nullable
    static Stmt.ForEach find(Stmt.Program program, Set<String> locals) {
        List<Stmt> statements = program.statements();
        for (int i = statements.size() - 1; i >= 0; i--) {
            Stmt statement = statements.get(i);
            if (statement instanceof Stmt.ForEach loop) {
                return NumericForEachPlan.eligible(loop) && !locals.contains(loop.target().name()) ? loop : null;
            }
            if (!localStatement(statement, locals)) {
                return null;
            }
        }
        return null;
    }

    private static boolean localStatement(Stmt statement, Set<String> locals) {
        return switch (statement) {
            case Stmt.Block block -> block.statements().stream().allMatch(child -> localStatement(child, locals));
            case Stmt.ReturnStmt result -> local(result.value(), locals);
            case Stmt.ExpressionStmt value -> local(value.expression(), locals);
            default -> false;
        };
    }

    private static boolean local(Expr expression, Set<String> locals) {
        return switch (expression) {
            case Expr.NumberLiteral _ -> true;
            case Expr.VariableAccess read -> "temp".equals(read.scope()) && locals.contains(read.name());
            case Expr.VariableAssign write -> "temp".equals(write.scope()) && locals.contains(write.name()) && local(write.value(), locals);
            case Expr.CompoundAssign write -> "temp".equals(write.scope()) && locals.contains(write.name()) && local(write.value(), locals);
            case Expr.Unary _, Expr.Group _, Expr.Binary _, Expr.Ternary _, Expr.BinaryConditional _ ->
                    AstNodes.children(expression).stream().allMatch(child -> local(child, locals));
            case Expr.MathCall call -> {
                MolangFunctions.MathFunction function = MolangFunctions.math(call.function());
                yield function != null && function.pure() && call.args().stream().allMatch(arg -> local(arg, locals));
            }
            default -> false;
        };
    }
}
