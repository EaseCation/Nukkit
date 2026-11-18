package cn.nukkit.molang.compiler;

import cn.nukkit.molang.MolangCompileOptions;
import cn.nukkit.molang.MolangExpression;
import cn.nukkit.molang.parser.ast.Stmt;
import cn.nukkit.molang.parser.ast.Expr;
import cn.nukkit.molang.runtime.MolangBindingPlan;
import cn.nukkit.molang.runtime.MolangRuntime;
import cn.nukkit.molang.runtime.MolangValues;

import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * 大程序按顶层语句分段，只建立一次逻辑求值作用域，显式 return 会立即终止所有后续段。
 */
final class SegmentedMolangCompiler {
    private static final int MAX_CHUNK_NODES = 400;
    private static final int MAX_STATEMENT_NODES = 900;
    private static final int MAX_OPTIMIZER_DEPTH = 512;

    static final Object CONTINUE = new Object();

    static void validateInput(Stmt.Program program) {
        Deque<DepthNode> pending = new ArrayDeque<>();
        pending.push(new DepthNode(program, 0));
        while (!pending.isEmpty()) {
            DepthNode current = pending.pop();
            if (current.depth() > MAX_OPTIMIZER_DEPTH) {
                throw new MolangCompileException("Molang expression nesting exceeds the compiler limit");
            }
            for (Object child : children(current.value())) {
                pending.push(new DepthNode(child, current.depth() + 1));
            }
        }
    }

    static MolangExpression compile(Stmt.Program program, MolangCompileOptions options) {
        List<List<Stmt>> chunks = new ArrayList<>();
        List<Stmt> current = new ArrayList<>();
        int cost = 0;
        for (Stmt statement : program.statements()) {
            int nodes = nodes(statement);
            if (nodes > MAX_STATEMENT_NODES) {
                throw new MolangCompileException("A single Molang statement exceeds the compiled method limit; split the statement into smaller scopes");
            }
            if (!current.isEmpty() && cost + nodes > MAX_CHUNK_NODES) {
                chunks.add(List.copyOf(current));
                current.clear();
                cost = 0;
            }
            current.add(statement);
            cost += nodes;
        }
        if (chunks.isEmpty()) {
            return new TypedMolangCompiler(program, options).compile();
        }
        if (!current.isEmpty()) {
            chunks.add(List.copyOf(current));
        }
        CompilationSymbols symbols = new CompilationSymbols();
        MolangExpression[] compiled = new MolangExpression[chunks.size()];
        for (int index = 0; index < compiled.length; index++) {
            compiled[index] = new TypedMolangCompiler(new Stmt.Program(chunks.get(index), true), options, symbols, true).compile();
        }
        MolangBindingPlan plan = new MolangBindingPlan(symbols.paths, symbols.queries);
        boolean invalidatesQueries = symbols.switchesContext;
        return new MolangExpression() {
            @Override
            public MolangBindingPlan bindingPlan() {
                return plan;
            }

            @Override
            public float evaluate(MolangRuntime runtime) {
                return MolangValues.toFloat(evaluateValue(runtime));
            }

            @Override
            public Object evaluateValue(MolangRuntime runtime) {
                runtime.beginEvaluation();
                try {
                    for (MolangExpression segment : compiled) {
                        Object value = segment.evaluateValue(runtime);
                        if (value != CONTINUE) {
                            return value;
                        }
                    }
                    return 0;
                } finally {
                    runtime.endEvaluation();
                    if (invalidatesQueries) {
                        runtime.invalidateQueries();
                    }
                }
            }
        };
    }

    private static int nodes(Stmt statement) {
        Deque<Object> pending = new ArrayDeque<>();
        pending.push(statement);
        int count = 0;
        while (!pending.isEmpty()) {
            Object value = pending.pop();
            if (++count > MAX_STATEMENT_NODES) {
                return count;
            }
            pending.addAll(children(value));
        }
        return count;
    }

    private static List<?> children(Object value) {
        return switch (value) {
            case Stmt.Program program -> program.statements();
            case Stmt.Block block -> block.statements();
            case Stmt.ExpressionStmt expression -> List.of(expression.expression());
            case Stmt.ReturnStmt returned -> List.of(returned.value());
            case Stmt.Loop loop -> List.of(loop.count(), loop.body());
            case Stmt.ForEach loop -> List.of(loop.array(), loop.body());
            case Expr.Scope scope -> List.of(scope.block());
            case Expr expression -> AstNodes.children(expression);
            default -> List.of();
        };
    }

    private SegmentedMolangCompiler() {
    }

    private record DepthNode(Object value, int depth) {
    }
}
