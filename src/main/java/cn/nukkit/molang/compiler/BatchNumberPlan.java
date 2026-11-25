package cn.nukkit.molang.compiler;

import cn.nukkit.molang.MolangCompileOptions;
import cn.nukkit.molang.MolangVersion;
import cn.nukkit.molang.parser.ast.Expr;
import cn.nukkit.molang.parser.ast.Stmt;
import cn.nukkit.molang.runtime.*;

import javax.annotation.Nullable;
import java.lang.classfile.ClassFile;
import java.lang.classfile.CodeBuilder;
import java.lang.classfile.MethodModel;
import java.lang.classfile.attribute.CodeAttribute;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

/**
 * 保存可嵌入短批次的数值表达式及其符号；宿主回调和槽号在各实体的绑定阶段单独取得。
 */
final class BatchNumberPlan {
    static final int FIRST_EXPRESSION_LOCAL = 20;

    private static final int MAX_NODES = 24;
    private static final int MAX_BYTES = 2048;
    private static final int MAX_LOCALS = 128;

    private final Stmt.Program program;
    private final Expr expression;
    private final MolangCompileOptions options;
    private final MolangBindingPlan symbols;
    private final int[] queries;
    private final int[] paths;
    private final boolean singleEvaluationRead;
    private final int nodeCount;
    private final int estimatedBytes;
    private final boolean straightLine;
    private final Set<Expr> reusable;

    private BatchNumberPlan(Stmt.Program program, Expr expression, MolangCompileOptions options, MolangBindingPlan symbols,
                            int[] queries, int[] paths, boolean singleEvaluationRead, int nodeCount, int estimatedBytes, boolean straightLine, Set<Expr> reusable) {
        this.program = program;
        this.expression = expression;
        this.options = options;
        this.symbols = symbols;
        this.queries = queries;
        this.paths = paths;
        this.singleEvaluationRead = singleEvaluationRead;
        this.nodeCount = nodeCount;
        this.estimatedBytes = estimatedBytes;
        this.straightLine = straightLine;
        this.reusable = reusable;
    }

    @Nullable
    private static Expr result(Stmt.Program program) {
        if (program.statements().size() != 1) {
            return null;
        }
        return switch (program.statements().getFirst()) {
            case Stmt.ReturnStmt returned -> returned.value();
            case Stmt.ExpressionStmt statement when !program.isComplex() -> statement.expression();
            default -> null;
        };
    }

    static boolean supports(Stmt.Program program, MolangCompileOptions options, boolean variables) {
        Expr value = result(program);
        return value != null && accepts(value, options, variables, false);
    }

    private static boolean accepts(Expr expression, MolangCompileOptions options, boolean variables, boolean operand) {
        return switch (expression) {
            case Expr.NumberLiteral _ -> true;
            case Expr.VariableAccess read -> variables && operand && "variable".equals(read.scope()) && read.name().indexOf('.') < 0;
            case Expr.Group group -> accepts(group.expression(), options, variables, operand);
            case Expr.Unary unary -> accepts(unary.operand(), options, variables, false);
            case Expr.Binary binary -> {
                boolean arithmetic = variables && switch (binary.operator()) {
                    case "&&", "||", "==", "!=" -> false;
                    default -> true;
                };
                yield accepts(binary.left(), options, variables, arithmetic) && accepts(binary.right(), options, variables, arithmetic);
            }
            case Expr.Ternary choice -> accepts(choice.condition(), options, variables, false)
                    && accepts(choice.thenBranch(), options, variables, false) && accepts(choice.elseBranch(), options, variables, false);
            case Expr.MathCall call -> {
                MolangFunctions.MathFunction function = MolangFunctions.math(call.function());
                yield function != null && function.pure() && call.args().stream().allMatch(a -> accepts(a, options, variables, false));
            }
            case Expr.QueryCall call -> {
                MolangQueryDefinition definition = options.querySchema().get(call.name());
                yield !MolangFunctions.builtinQuery(call.name()) && call.args().isEmpty() && definition != null
                        && definition.isReadOnly() && definition.returnType() == MolangQueryDefinition.ValueType.NUMBER
                        && !definition.nullable() && definition.minArguments() == 0 && definition.maxArguments() == 0;
            }
            default -> false;
        };
    }

    @Nullable
    static BatchNumberPlan create(Stmt.Program program, MolangCompileOptions options, MolangBindingPlan symbols, int[] queries, int[] paths, boolean single, byte[] original, byte[] linked) {
        Expr value = result(program);
        if (value == null) {
            return null;
        }
        int count = 0;
        boolean straight = true;
        Set<Expr> reusable = new HashSet<>();
        Deque<Expr> pending = new ArrayDeque<>();
        pending.push(value);
        while (!pending.isEmpty()) {
            Expr node = pending.pop();
            if (++count > MAX_NODES) {
                return null;
            }
            if (node instanceof Expr.Ternary || node instanceof Expr.Binary binary && ("&&".equals(binary.operator()) || "||".equals(binary.operator()))) {
                straight = false;
            }
            if (node instanceof Expr.QueryCall || node instanceof Expr.MathCall || node instanceof Expr.Unary || node instanceof Expr.Binary) {
                reusable.add(node);
            }
            pending.addAll(AstNodes.children(node));
        }
        CodeAttribute plainBody = numericMethod(original);
        CodeAttribute linkedBody = numericMethod(linked);
        if (Math.max(plainBody.maxLocals(), linkedBody.maxLocals()) + FIRST_EXPRESSION_LOCAL > MAX_LOCALS) {
            return null;
        }
        // 为更宽的局部索引、字段和常量引用、边界及可复用数值的存取预留空间。
        int budget = 64 + 2 * Math.max(plainBody.codeLength(), linkedBody.codeLength()) + 16 * count;
        if (budget > MAX_BYTES) {
            return null;
        }
        return new BatchNumberPlan(program, value, options, symbols, queries, paths, single, count, budget, straight, Set.copyOf(reusable));
    }

    private static CodeAttribute numericMethod(byte[] bytes) {
        for (MethodModel method : ClassFile.of().parse(bytes).methods()) {
            if (method.methodName().equalsString("evaluate") || method.methodName().equalsString("evaluateBody")) {
                return (CodeAttribute) method.code().orElseThrow();
            }
        }
        throw new MolangCompileException("Missing compiled numeric body");
    }

    int nodeCount() {
        return nodeCount;
    }

    int estimatedBytes() {
        return estimatedBytes;
    }

    int queryCount() {
        return queries.length;
    }

    int pathCount() {
        return paths.length;
    }

    MolangPath path(int index) {
        return symbols.path(paths[index]);
    }

    boolean straightLine() {
        return straightLine;
    }

    boolean contains(Expr value) {
        return reusable.contains(value);
    }

    MolangVersion version() {
        return options.version();
    }


    Binding bind(MolangRuntime view, MolangNumberContextQuery[] inputs, int[] variableSlots) {
        if (paths.length != 0) {
            symbols.linkVariables(view, paths, variableSlots);
        } else if (symbols.linkProperties(view, queries, inputs) != null) {
            return Binding.SAMPLED;
        }
        MolangRuntime owner = singleEvaluationRead ? symbols.linkSingleEvaluationQuery(view, queries[0], inputs) : symbols.linkNumberQueries(view, queries, inputs);
        return owner != null ? Binding.SCOPED : Binding.UNAVAILABLE;
    }

    void emit(CodeBuilder code, Output output) {
        new TypedMolangCompiler(program, options).emitBatch(code, expression, this, output);
    }

    int variableIndex(String name) {
        for (int i = 0; i < paths.length; i++) {
            if (path(i).name().equals(name)) {
                return i;
            }
        }
        throw new MolangCompileException("Unknown batch variable: " + name);
    }

    int queryIndex(String name) {
        for (int i = 0; i < queries.length; i++) {
            if (symbols.query(queries[i]).name().equals(name)) {
                return i;
            }
        }
        throw new MolangCompileException("Unknown batch query: " + name);
    }

    enum Binding {
        UNAVAILABLE,
        SCOPED,
        SAMPLED
    }

    interface Output {
        void readQuery(CodeBuilder code, int index);

        void readVariableOperand(CodeBuilder code, int index);

        int previousLocal(Expr value);

        int captureLocal(Expr value);
    }
}
