package cn.nukkit.molang.compiler;

import cn.nukkit.molang.MolangCompileOptions;
import cn.nukkit.molang.MolangExpression;
import cn.nukkit.molang.parser.ast.Expr;
import cn.nukkit.molang.parser.ast.Stmt;
import cn.nukkit.molang.runtime.*;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.lang.classfile.ClassFile;
import java.lang.classfile.CodeBuilder;
import java.lang.classfile.Label;
import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.*;

import static java.lang.classfile.ClassFile.*;
import static java.lang.constant.ConstantDescs.*;

/**
 * 为运行时类型生成 JVM 字节码；解析和节点分派仅发生在编译期间。
 */
final class TypedMolangCompiler {
    private static final int MAX_SPECIALIZED_LOOP_DEPTH = 2;
    private static final int MAX_SPECIALIZED_LOOP_NODES = 120;

    private static final ClassDesc COMPILED = ClassDesc.of(TypedMolangCompiler.class.getPackageName(), "CompiledExpr");
    private static final ClassDesc EXPRESSION = MolangExpression.class.describeConstable().orElseThrow();
    private static final ClassDesc RUNTIME = MolangRuntime.class.describeConstable().orElseThrow();
    private static final ClassDesc VALUES = MolangValues.class.describeConstable().orElseThrow();
    private static final ClassDesc MATH = MolangMath.class.describeConstable().orElseThrow();
    private static final ClassDesc PATH = MolangPath.class.describeConstable().orElseThrow();
    private static final ClassDesc PATH_ARRAY = PATH.arrayType();
    private static final ClassDesc QUERY_REF = MolangQueryRef.class.describeConstable().orElseThrow();
    private static final ClassDesc QUERY_ARRAY = QUERY_REF.arrayType();
    private static final ClassDesc BINDING_PLAN = MolangBindingPlan.class.describeConstable().orElseThrow();
    private static final ClassDesc SEGMENTED = SegmentedMolangCompiler.class.describeConstable().orElseThrow();
    private static final ClassDesc CD_Math = Math.class.describeConstable().orElseThrow();
    private static final ClassDesc FLOAT = Float.class.describeConstable().orElseThrow();
    private static final ClassDesc OBJECT_ARRAY = CD_Object.arrayType();

    private final Stmt.Program program;
    private final MolangCompileOptions options;
    private final NumericLocalPlan localPlan;
    private final boolean alwaysNumericResult;
    private final CompilationSymbols symbols;
    private final boolean segmented;
    private final Object2IntMap<String> pathIndexes;
    private final List<MolangPath> paths;
    private final Object2IntMap<String> queryIndexes;
    private final List<MolangQueryRef> queries;
    private final Object2IntMap<String> numericTemps = new Object2IntOpenHashMap<>();
    private final Object2IntMap<String> objectTemps = new Object2IntOpenHashMap<>();
    private Object2IntMap<Expr> hoistedNumbers = new Object2IntOpenHashMap<>();
    private final Deque<Label> breaks = new ArrayDeque<>();
    private final Deque<Label> continues = new ArrayDeque<>();
    private int nextSlot = 2;
    private boolean numericReturn;
    private boolean foreignContext;
    private boolean switchesContext;

    TypedMolangCompiler(Stmt.Program program, MolangCompileOptions options) {
        this(program, options, new CompilationSymbols(), false);
    }

    TypedMolangCompiler(Stmt.Program program, MolangCompileOptions options, CompilationSymbols symbols, boolean segmented) {
        this.program = program;
        this.options = options;
        this.symbols = symbols;
        this.segmented = segmented;
        this.pathIndexes = symbols.pathIndexes;
        this.paths = symbols.paths;
        this.queryIndexes = symbols.queryIndexes;
        this.queries = symbols.queries;
        this.localPlan = new NumericLocalPlan(program, options);
        this.alwaysNumericResult = !segmented && new NumericResultPlan(program, options, localPlan.numericTemps()).numeric();
        pathIndexes.defaultReturnValue(-1);
        queryIndexes.defaultReturnValue(-1);
        numericTemps.defaultReturnValue(-1);
        objectTemps.defaultReturnValue(-1);
        hoistedNumbers.defaultReturnValue(-1);
    }

    MolangExpression compile() {
        byte[] bytes = ClassFile.of().build(COMPILED, builder -> {
            builder.withFlags(ACC_PUBLIC | ACC_FINAL | ACC_SYNTHETIC);
            builder.withInterfaceSymbols(EXPRESSION);
            builder.withField("paths", PATH_ARRAY, ACC_PRIVATE | ACC_FINAL);
            builder.withField("queries", QUERY_ARRAY, ACC_PRIVATE | ACC_FINAL);
            builder.withField("bindingPlan", BINDING_PLAN, ACC_PRIVATE | ACC_FINAL);
            builder.withMethodBody(INIT_NAME, MethodTypeDesc.of(CD_void, PATH_ARRAY, QUERY_ARRAY, BINDING_PLAN), ACC_PUBLIC, code -> {
                code.aload(0).invokespecial(CD_Object, INIT_NAME, MTD_void);
                code.aload(0).aload(1).putfield(COMPILED, "paths", PATH_ARRAY);
                code.aload(0).aload(2).putfield(COMPILED, "queries", QUERY_ARRAY);
                code.aload(0).aload(3).putfield(COMPILED, "bindingPlan", BINDING_PLAN).return_();
            });
            builder.withMethodBody("bindingPlan", MethodTypeDesc.of(BINDING_PLAN), ACC_PUBLIC,
                    code -> code.aload(0).getfield(COMPILED, "bindingPlan", BINDING_PLAN).areturn());
            builder.withMethodBody("evaluate", MethodTypeDesc.of(CD_float, RUNTIME), ACC_PUBLIC, code -> {
                generateBody(code, true);
            });
            builder.withMethodBody("evaluateValue", MethodTypeDesc.of(CD_Object, RUNTIME), ACC_PUBLIC, code -> {
                if (alwaysNumericResult) {
                    code.aload(0).aload(1).invokevirtual(COMPILED, "evaluate", MethodTypeDesc.of(CD_float, RUNTIME));
                    box(code);
                    code.areturn();
                } else {
                    generateBody(code, false);
                }
            });
        });
        symbols.switchesContext |= switchesContext;
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup().defineHiddenClass(bytes, true, MethodHandles.Lookup.ClassOption.NESTMATE);
            MolangExpression compiled = (MolangExpression) lookup.findConstructor(lookup.lookupClass(),
                    MethodType.methodType(void.class, MolangPath[].class, MolangQueryRef[].class, MolangBindingPlan.class))
                    .invoke(paths.toArray(MolangPath[]::new), queries.toArray(MolangQueryRef[]::new), new MolangBindingPlan(paths, queries));
            if (segmented || !localPlan.needsRuntimeTemps() && !switchesContext) {
                return compiled;
            }
            return new ScopedExpression(compiled, switchesContext);
        } catch (Throwable exception) {
            throw new MolangCompileException("Failed to load typed expression", exception);
        }
    }

    private record ScopedExpression(MolangExpression compiled, boolean invalidatesQueries) implements MolangExpression {
        @Override
        public MolangBindingPlan bindingPlan() {
            return compiled.bindingPlan();
        }

        @Override
        public float evaluate(MolangRuntime runtime) {
            runtime.beginEvaluation();
            try {
                return compiled.evaluate(runtime);
            } finally {
                runtime.endEvaluation();
                if (invalidatesQueries) {
                    runtime.invalidateQueries();
                }
            }
        }

        @Override
        public Object evaluateValue(MolangRuntime runtime) {
            runtime.beginEvaluation();
            try {
                return compiled.evaluateValue(runtime);
            } finally {
                runtime.endEvaluation();
                if (invalidatesQueries) {
                    runtime.invalidateQueries();
                }
            }
        }
    }

    private void generateBody(CodeBuilder code, boolean numeric) {
        numericReturn = numeric;
        nextSlot = 2;
        numericTemps.clear();
        objectTemps.clear();
        hoistedNumbers.clear();
        for (String name : segmented ? Set.<String>of() : localPlan.numericTemps()) {
            int slot = nextSlot;
            nextSlot += 2;
            numericTemps.put(name, slot);
            code.fconst_0().fstore(slot).iconst_0().istore(slot + 1);
        }
        for (String name : segmented ? Set.<String>of() : localPlan.objectTemps()) {
            int slot = nextSlot++;
            objectTemps.put(name, slot);
            code.aconst_null().astore(slot);
        }
        if (!program.isComplex() && program.statements().size() == 1
                && program.statements().getFirst() instanceof Stmt.ExpressionStmt(Expr value)) {
            returnExpression(code, value, 1);
        } else {
            statement(code, program, 1);
            if (segmented && !numeric) {
                code.getstatic(SEGMENTED, "CONTINUE", CD_Object).areturn();
            } else {
                returnExpression(code, new Expr.NumberLiteral(0), 1);
            }
        }
    }

    private void returnExpression(CodeBuilder code, Expr value, int runtime) {
        if (numericReturn) {
            numericExpression(code, value, runtime);
            code.freturn();
        } else {
            expression(code, value, runtime);
            code.areturn();
        }
    }

    private void statement(CodeBuilder code, Stmt statement, int runtime) {
        int scratchBase = nextSlot;
        switch (statement) {
            case Stmt.Program value -> value.statements().forEach(child -> statement(code, child, runtime));
            case Stmt.Block value -> value.statements().forEach(child -> statement(code, child, runtime));
            case Stmt.ExpressionStmt value -> {
                if (value.expression() instanceof Expr.VariableAssign(String targetScope, String targetName, Expr targetValue)
                        && "variable".equals(targetScope)
                        && targetValue instanceof Expr.VariableAccess(String sourceScope, String sourceName)
                        && "variable".equals(sourceScope)) {
                    code.aload(runtime);
                    path(code, targetName);
                    path(code, sourceName);
                    code.invokeinterface(RUNTIME, "copyVariableValue", MethodTypeDesc.of(CD_void, PATH, PATH));
                } else if (isNumeric(value.expression())) {
                    numericExpression(code, value.expression(), runtime);
                    code.pop();
                } else {
                    expression(code, value.expression(), runtime);
                    code.pop();
                }
            }
            case Stmt.ReturnStmt value -> returnExpression(code, value.value(), runtime);
            case Stmt.Break _ -> jump(code, breaks, "break");
            case Stmt.Continue _ -> jump(code, continues, "continue");
            case Stmt.Loop value -> loop(code, value, runtime);
            case Stmt.ForEach value -> forEach(code, value, runtime);
        }
        // 中间操作数不会跨越语句存活；永久 temp、外层循环计数和提升值均在基线之前。
        nextSlot = scratchBase;
    }

    private void loop(CodeBuilder code, Stmt.Loop loop, int runtime) {
        int count = nextSlot++;
        numericExpression(code, loop.count(), runtime);
        code.f2i().ldc(1024).invokestatic(CD_Math, "min", MethodTypeDesc.of(CD_int, CD_int, CD_int)).istore(count);
        MolangCompileOptions loopOptions = foreignContext ? new MolangCompileOptions(options.version(), MolangQuerySchema.EMPTY, false) : options;
        List<Expr> invariants = new LoopInvariantPlan(loop.body(), loopOptions).expressions().stream()
                .filter(expression -> !hoistedNumbers.containsKey(expression)).toList();
        if (invariants.isEmpty() || breaks.size() >= MAX_SPECIALIZED_LOOP_DEPTH || !smallLoop(loop.body())) {
            loopBody(code, count, loop.body(), runtime);
            return;
        }
        Label fallback = code.newLabel();
        Label done = code.newLabel();
        code.iload(count).ifle(done);
        code.aload(runtime).invokeinterface(RUNTIME, "supportsStableVariableReads", MethodTypeDesc.of(CD_boolean)).ifeq(fallback);

        Object2IntMap<Expr> previous = hoistedNumbers;
        hoistedNumbers = new Object2IntOpenHashMap<>(previous);
        hoistedNumbers.defaultReturnValue(-1);
        for (Expr invariant : invariants) {
            int slot = storeNumber(code, invariant, runtime);
            hoistedNumbers.put(invariant, slot);
        }
        loopBody(code, count, loop.body(), runtime);
        hoistedNumbers = previous;
        code.goto_(done).labelBinding(fallback);
        loopBody(code, count, loop.body(), runtime);
        code.labelBinding(done);
    }

    private void loopBody(CodeBuilder code, int count, Stmt body, int runtime) {
        Label start = code.newLabel();
        Label end = code.newLabel();
        Label next = code.newLabel();
        code.labelBinding(start).iload(count).ifle(end);
        breaks.push(end);
        continues.push(next);
        statement(code, body, runtime);
        continues.pop();
        breaks.pop();
        code.labelBinding(next).iinc(count, -1).goto_(start).labelBinding(end);
    }

    private static boolean smallLoop(Stmt statement) {
        int[] nodes = {0};
        AstNodes.visit(statement, _ -> nodes[0]++, _ -> nodes[0]++);
        return nodes[0] <= MAX_SPECIALIZED_LOOP_NODES;
    }

    private void forEach(CodeBuilder code, Stmt.ForEach loop, int runtime) {
        int array = storeExpression(code, loop.array(), runtime);
        int index = nextSlot++;
        int length = nextSlot++;
        code.iconst_0().istore(index).aload(array).invokestatic(VALUES, "length", MethodTypeDesc.of(CD_int, CD_Object)).istore(length);
        if (!segmented && !foreignContext && NumericForEachPlan.eligible(loop, options) && smallLoop(loop.body())) {
            Label fallback = code.newLabel();
            Label complete = code.newLabel();
            code.iload(length).ifle(complete);
            code.aload(runtime).invokeinterface(RUNTIME, "supportsLocalTempAccess", MethodTypeDesc.of(CD_boolean)).ifeq(fallback);
            code.aload(array).invokestatic(VALUES, "isNumericArray", MethodTypeDesc.of(CD_boolean, CD_Object)).ifeq(fallback);
            numericForEach(code, loop, runtime, array, index, length);
            code.goto_(complete).labelBinding(fallback);
            generalForEach(code, loop, runtime, array, index, length);
            code.labelBinding(complete);
        } else {
            generalForEach(code, loop, runtime, array, index, length);
        }
    }

    private void numericForEach(CodeBuilder code, Stmt.ForEach loop, int runtime, int array, int index, int length) {
        int targetSlot = nextSlot;
        nextSlot += 2;
        String target = loop.target().name();
        int previous = numericTemps.put(target, targetSlot);
        int previousObject = objectTemps.removeInt(target);
        Label start = code.newLabel();
        Label end = code.newLabel();
        Label next = code.newLabel();
        code.fconst_0().fstore(targetSlot).iconst_0().istore(targetSlot + 1);
        code.labelBinding(start).iload(index).iload(length).if_icmpge(end);
        code.aload(array).iload(index).invokestatic(VALUES, "getElementFloat", MethodTypeDesc.of(CD_float, CD_Object, CD_int))
                .fstore(targetSlot).iconst_1().istore(targetSlot + 1);
        breaks.push(end);
        continues.push(next);
        statement(code, loop.body(), runtime);
        continues.pop();
        breaks.pop();
        code.labelBinding(next).iinc(index, 1).goto_(start).labelBinding(end);
        if (previous >= 0) {
            numericTemps.put(target, previous);
        } else {
            numericTemps.removeInt(target);
        }
        if (previousObject >= 0) {
            objectTemps.put(target, previousObject);
        }
        // 快路径只有非空数组进入；迭代目标在正常退出和 break 后发布最后实际访问的元素。
        code.aload(1);
        path(code, target);
        code.fload(targetSlot).invokeinterface(RUNTIME, "setTempNumber", MethodTypeDesc.of(CD_void, PATH, CD_float));
    }

    private void generalForEach(CodeBuilder code, Stmt.ForEach loop, int runtime, int array, int index, int length) {
        Label start = code.newLabel();
        Label end = code.newLabel();
        Label next = code.newLabel();
        code.labelBinding(start).iload(index).iload(length).if_icmpge(end);
        Label referenceElement = code.newLabel();
        Label elementReady = code.newLabel();
        code.aload(array).invokestatic(VALUES, "isNumericArray", MethodTypeDesc.of(CD_boolean, CD_Object)).ifeq(referenceElement);
        code.aload("temp".equals(loop.target().scope()) ? 1 : runtime);
        path(code, loop.target().name());
        code.aload(array).iload(index).invokestatic(VALUES, "getElementFloat", MethodTypeDesc.of(CD_float, CD_Object, CD_int));
        code.invokeinterface(RUNTIME, "temp".equals(loop.target().scope()) ? "setTempNumber" : "setVariableNumber", MethodTypeDesc.of(CD_void, PATH, CD_float));
        code.goto_(elementReady).labelBinding(referenceElement);
        code.aload("temp".equals(loop.target().scope()) ? 1 : runtime);
        path(code, loop.target().name());
        code.aload(array).iload(index).invokestatic(VALUES, "getElement", MethodTypeDesc.of(CD_Object, CD_Object, CD_int));
        setVariable(code, loop.target().scope());
        code.labelBinding(elementReady);
        breaks.push(end);
        continues.push(next);
        statement(code, loop.body(), runtime);
        continues.pop();
        breaks.pop();
        code.labelBinding(next).iinc(index, 1).goto_(start).labelBinding(end);
    }

    private boolean isNumeric(Expr expression) {
        return switch (expression) {
            case Expr.NumberLiteral _, Expr.This _, Expr.Unary _, Expr.Binary _, Expr.MathCall _, Expr.CompoundAssign _ -> true;
            case Expr.QueryCall value -> {
                MolangQueryDefinition definition = foreignContext ? null : options.querySchema().get(value.name());
                yield MolangFunctions.builtinQuery(value.name()) || definition != null
                        && definition.returnType() == MolangQueryDefinition.ValueType.NUMBER && !definition.nullable();
            }
            case Expr.VariableAssign value -> isNumeric(value.value());
            case Expr.ArrayAssign value -> isNumeric(value.value());
            case Expr.MemberAssign value -> isNumeric(value.value());
            case Expr.Group value -> isNumeric(value.expression());
            case Expr.Ternary value -> isNumeric(value.thenBranch()) && isNumeric(value.elseBranch());
            case Expr.BinaryConditional value -> isNumeric(value.thenBranch());
            case Expr.NullCoalescing value -> isNumeric(value.left()) && isNumeric(value.right());
            default -> false;
        };
    }

    private int localTemp(Expr expression) {
        return switch (expression) {
            case Expr.VariableAccess value when "temp".equals(value.scope()) -> numericTemps.getInt(value.name());
            case Expr.Group value -> localTemp(value.expression());
            default -> -1;
        };
    }

    private boolean numericOperand(Expr expression) {
        // 算术可将缺失值转换为零；对象赋值和 query 参数仍必须保留缺失状态。
        return isNumeric(expression) || localTemp(expression) >= 0;
    }

    private int localObject(Expr expression) {
        return switch (expression) {
            case Expr.VariableAccess value when "temp".equals(value.scope()) -> objectTemps.getInt(value.name());
            case Expr.Group value -> localObject(value.expression());
            default -> -1;
        };
    }

    private int storeNumber(CodeBuilder code, Expr expression, int runtime) {
        int slot = nextSlot++;
        numericExpression(code, expression, runtime);
        code.fstore(slot);
        return slot;
    }

    private void numericExpression(CodeBuilder code, Expr expression, int runtime) {
        int hoisted = hoistedNumbers.getInt(expression);
        if (hoisted >= 0) {
            code.fload(hoisted);
            return;
        }
        switch (expression) {
            case Expr.NumberLiteral value -> code.ldc(value.value());
            case Expr.StringLiteral _ -> code.fconst_0();
            case Expr.This _ -> code.aload(runtime).invokeinterface(RUNTIME, "getThis", MethodTypeDesc.of(CD_float));
            case Expr.Group value -> numericExpression(code, value.expression(), runtime);
            case Expr.VariableAccess value -> {
                int slot = localTemp(value);
                if (slot >= 0) {
                    code.fload(slot);
                } else if (localObject(value) >= 0) {
                    code.aload(localObject(value));
                    toFloat(code);
                } else if ("array".equals(value.scope())) {
                    getVariable(code, runtime, value.scope(), value.name());
                    toFloat(code);
                } else {
                    String method = switch (value.scope()) {
                        case "variable" -> "getVariableNumber";
                        case "temp" -> "getTempNumber";
                        case "context" -> "getContextNumber";
                        default -> throw new MolangCompileException("Unknown variable scope: " + value.scope());
                    };
                    code.aload("temp".equals(value.scope()) ? 1 : runtime);
                    path(code, value.name());
                    code.invokeinterface(RUNTIME, method, MethodTypeDesc.of(CD_float, PATH));
                }
            }
            case Expr.VariableAssign value when isNumeric(value.value()) -> numericAssign(code, value.scope(), value.name(), value.value(), runtime);
            case Expr.CompoundAssign value -> numericAssign(code, value.scope(), value.name(),
                    new Expr.Binary(new Expr.VariableAccess(value.scope(), value.name()), value.operator().substring(0, 1), value.value()), runtime);
            case Expr.Unary value -> {
                numericExpression(code, value.operand(), runtime);
                if ("-".equals(value.operator())) {
                    code.fneg();
                } else {
                    code.fconst_0().fcmpl();
                    comparisonResult(code, "==");
                }
            }
            case Expr.Binary value -> numericBinary(code, value, runtime);
            case Expr.Ternary value -> numericConditional(code, value.condition(), value.thenBranch(), value.elseBranch(), runtime);
            case Expr.BinaryConditional value -> numericConditional(code, value.condition(), value.thenBranch(), new Expr.NumberLiteral(0), runtime);
            case Expr.NullCoalescing value -> numericCoalescing(code, value, runtime);
            case Expr.MathCall value -> numericMath(code, value, runtime);
            case Expr.QueryCall value -> numericQuery(code, value, runtime);
            case Expr.ArrayAccess value -> arrayNumber(code, value, runtime, false);
            case Expr.IndexAccess value -> indexNumber(code, value.target(), value.index(), runtime, false);
            case Expr.ArrayAssign value when isNumeric(value.value()) -> {
                int index = storeNumber(code, value.index(), runtime);
                int result = storeNumber(code, value.value(), runtime);
                code.aload("temp".equals(value.scope()) ? 1 : runtime);
                path(code, value.name());
                code.fload(index).f2i().fload(result);
                code.invokeinterface(RUNTIME, "temp".equals(value.scope()) ? "setTempElementNumber" : "setVariableElementNumber", MethodTypeDesc.of(CD_void, PATH, CD_int, CD_float));
                code.fload(result);
            }
            case Expr.MemberAssign value when isNumeric(value.value()) -> {
                int target = storeExpression(code, value.target(), runtime);
                int result = storeNumber(code, value.value(), runtime);
                code.aload(target);
                path(code, value.name());
                code.fload(result);
                box(code);
                code.invokestatic(VALUES, "setMember", MethodTypeDesc.of(CD_void, CD_Object, PATH, CD_Object));
                code.aload(runtime).invokeinterface(RUNTIME, "invalidateQueries", MTD_void).fload(result);
            }
            case Expr.Scope value -> {
                statement(code, value.block(), runtime);
                code.fconst_0();
            }
            case Expr.ContextSwitch value -> {
                int target = storeExpression(code, value.target(), runtime);
                int alternate = nextSlot++;
                Label missing = code.newLabel();
                Label done = code.newLabel();
                code.aload(target).invokestatic(VALUES, "asRuntime", MethodTypeDesc.of(RUNTIME, CD_Object)).astore(alternate);
                code.aload(alternate).ifnull(missing);
                switchesContext = true;
                boolean previousContext = foreignContext;
                foreignContext = true;
                code.aload(1).invokeinterface(RUNTIME, "invalidateQueries", MTD_void);
                try {
                    numericExpression(code, value.expression(), alternate);
                } finally {
                    foreignContext = previousContext;
                }
                code.goto_(done).labelBinding(missing).fconst_0().labelBinding(done);
            }
            case Expr.Break _ -> {
                jump(code, breaks, "break");
                code.fconst_0();
            }
            case Expr.Continue _ -> {
                jump(code, continues, "continue");
                code.fconst_0();
            }
            default -> {
                expression(code, expression, runtime);
                toFloat(code);
            }
        }
    }

    private void numericAssign(CodeBuilder code, String scope, String name, Expr value, int runtime) {
        int result = storeNumber(code, value, runtime);
        int local = "temp".equals(scope) ? numericTemps.getInt(name) : -1;
        if (local >= 0) {
            code.fload(result).fstore(local).iconst_1().istore(local + 1);
        } else if ("temp".equals(scope) && objectTemps.containsKey(name)) {
            code.fload(result);
            box(code);
            code.astore(objectTemps.getInt(name));
        } else {
            code.aload("temp".equals(scope) ? 1 : runtime);
            path(code, name);
            code.fload(result)
                    .invokeinterface(RUNTIME, "temp".equals(scope) ? "setTempNumber" : "setVariableNumber",
                            MethodTypeDesc.of(CD_void, PATH, CD_float));
        }
        code.fload(result);
    }

    private void numericConditional(CodeBuilder code, Expr condition, Expr thenBranch, Expr elseBranch, int runtime) {
        Label otherwise = code.newLabel();
        Label done = code.newLabel();
        numericExpression(code, condition, runtime);
        code.fconst_0().fcmpl().ifeq(otherwise);
        numericExpression(code, thenBranch, runtime);
        code.goto_(done).labelBinding(otherwise);
        numericExpression(code, elseBranch, runtime);
        code.labelBinding(done);
    }

    private void numericCoalescing(CodeBuilder code, Expr.NullCoalescing value, int runtime) {
        Label present = code.newLabel();
        Label done = code.newLabel();
        int local = localTemp(value.left());
        if (local >= 0) {
            code.iload(local + 1).ifne(present);
            numericExpression(code, value.right(), runtime);
            code.goto_(done).labelBinding(present).fload(local).labelBinding(done);
        } else {
            int result = storeExpression(code, value.left(), runtime);
            code.aload(result).invokestatic(VALUES, "isResolved", MethodTypeDesc.of(CD_boolean, CD_Object)).ifne(present);
            numericExpression(code, value.right(), runtime);
            code.goto_(done).labelBinding(present).aload(result);
            toFloat(code);
            code.labelBinding(done);
        }
    }

    private record Operand(int slot, boolean number, boolean packed) {
    }

    private Operand storeOperand(CodeBuilder code, Expr value, int runtime) {
        boolean numeric = numericOperand(value);
        return new Operand(numeric ? storeNumber(code, value, runtime) : storeExpression(code, value, runtime), numeric, false);
    }

    private Operand storeArithmeticOperand(CodeBuilder code, Expr value, int runtime) {
        Expr unwrapped = value;
        while (unwrapped instanceof Expr.Group(Expr expression)) {
            unwrapped = expression;
        }
        if (unwrapped instanceof Expr.ArrayAccess array) {
            int slot = nextSlot;
            nextSlot += 2;
            arrayNumber(code, array, runtime, true);
            code.lstore(slot);
            return new Operand(slot, false, true);
        }
        if (unwrapped instanceof Expr.IndexAccess(Expr target, Expr index)) {
            int slot = nextSlot;
            nextSlot += 2;
            indexNumber(code, target, index, runtime, true);
            code.lstore(slot);
            return new Operand(slot, false, true);
        }
        if (!numericOperand(value) && unwrapped instanceof Expr.VariableAccess variable && !"array".equals(variable.scope())) {
            int slot = nextSlot;
            nextSlot += 2;
            String method = switch (variable.scope()) {
                case "variable" -> "getVariableOperand";
                case "temp" -> "getTempOperand";
                case "context" -> "getContextOperand";
                default -> throw new MolangCompileException("Unknown variable scope: " + variable.scope());
            };
            code.aload("temp".equals(variable.scope()) ? 1 : runtime);
            int objectLocal = localObject(variable);
            if (objectLocal >= 0) {
                code.pop().aload(objectLocal).invokestatic(VALUES, "numericOperand", MethodTypeDesc.of(CD_long, CD_Object)).lstore(slot);
            } else {
                path(code, variable.name());
                code.invokeinterface(RUNTIME, method, MethodTypeDesc.of(CD_long, PATH)).lstore(slot);
            }
            return new Operand(slot, false, true);
        }
        return storeOperand(code, value, runtime);
    }

    private static void loadNumber(CodeBuilder code, Operand operand) {
        if (operand.packed()) {
            code.lload(operand.slot()).l2i().invokestatic(FLOAT, "intBitsToFloat", MethodTypeDesc.of(CD_float, CD_int));
        } else if (operand.number()) {
            code.fload(operand.slot());
        } else {
            code.aload(operand.slot());
            toFloat(code);
        }
    }

    private static void loadObject(CodeBuilder code, Operand operand) {
        if (operand.number()) {
            code.fload(operand.slot());
            box(code);
        } else {
            code.aload(operand.slot());
        }
    }

    private void numericBinary(CodeBuilder code, Expr.Binary value, int runtime) {
        String operator = value.operator();
        if ("&&".equals(operator)) {
            numericConditional(code, value.left(), new Expr.Unary("!", new Expr.Unary("!", value.right())), new Expr.NumberLiteral(0), runtime);
            return;
        }
        if ("||".equals(operator)) {
            numericConditional(code, value.left(), new Expr.NumberLiteral(1), new Expr.Unary("!", new Expr.Unary("!", value.right())), runtime);
            return;
        }
        boolean equality = "==".equals(operator) || "!=".equals(operator);
        Operand left = equality ? storeOperand(code, value.left(), runtime) : storeArithmeticOperand(code, value.left(), runtime);
        Operand right = equality ? storeOperand(code, value.right(), runtime) : storeArithmeticOperand(code, value.right(), runtime);
        if (equality) {
            if (left.number() && right.number()) {
                loadNumber(code, left);
                loadNumber(code, right);
                code.fcmpl();
                comparisonResult(code, operator);
            } else {
                loadObject(code, left);
                loadObject(code, right);
                code.invokestatic(VALUES, "equal", MethodTypeDesc.of(CD_boolean, CD_Object, CD_Object));
                comparisonResult(code, "==".equals(operator) ? "!=" : "==");
            }
            return;
        }
        Label invalid = code.newLabel();
        Label done = code.newLabel();
        boolean nullableOperation = !left.number() || !right.number() || "/".equals(operator) || "%".equals(operator);
        if (left.packed()) {
            code.lload(left.slot()).ldc(32).lushr().l2i().ifne(invalid);
        } else if (!left.number()) {
            code.aload(left.slot()).instanceOf(CD_String).ifne(invalid);
        }
        if (right.packed()) {
            code.lload(right.slot()).ldc(32).lushr().l2i().ifne(invalid);
        } else if (!right.number()) {
            code.aload(right.slot()).instanceOf(CD_String).ifne(invalid);
        }
        if ("/".equals(operator) || "%".equals(operator)) {
            loadNumber(code, right);
            code.fconst_0().fcmpl().ifeq(invalid);
        }
        loadNumber(code, left);
        loadNumber(code, right);
        switch (operator) {
            case "+" -> code.fadd();
            case "-" -> code.fsub();
            case "*" -> code.fmul();
            case "/" -> code.fdiv();
            case "%" -> code.frem();
            case "<", "<=" -> {
                code.fcmpg();
                comparisonResult(code, operator);
            }
            case ">", ">=" -> {
                code.fcmpl();
                comparisonResult(code, operator);
            }
            default -> throw new MolangCompileException("Unknown binary operator: " + operator);
        }
        if (nullableOperation) {
            code.goto_(done).labelBinding(invalid).fconst_0().labelBinding(done);
        }
    }

    private static void comparisonResult(CodeBuilder code, String operator) {
        Label matched = code.newLabel();
        Label done = code.newLabel();
        switch (operator) {
            case "==" -> code.ifeq(matched);
            case "!=" -> code.ifne(matched);
            case "<" -> code.iflt(matched);
            case "<=" -> code.ifle(matched);
            case ">" -> code.ifgt(matched);
            case ">=" -> code.ifge(matched);
            default -> throw new MolangCompileException("Unknown comparison operator: " + operator);
        }
        code.fconst_0().goto_(done).labelBinding(matched).fconst_1().labelBinding(done);
    }

    private void numericMath(CodeBuilder code, Expr.MathCall call, int runtime) {
        int arity = mathArity(call.function());
        int[] slots = new int[arity];
        for (int i = 0; i < arity; i++) {
            slots[i] = storeNumber(code, i < call.args().size() ? call.args().get(i) : new Expr.NumberLiteral(0), runtime);
        }
        for (int slot : slots) {
            code.fload(slot);
        }
        ClassDesc[] parameters = new ClassDesc[arity];
        Arrays.fill(parameters, CD_float);
        code.invokestatic(MATH, call.function(), MethodTypeDesc.of(CD_float, parameters));
    }

    private static int mathArity(String function) {
        MolangFunctions.MathFunction definition = MolangFunctions.math(function);
        if (definition == null) {
            throw new MolangCompileException("Unknown math function: math." + function);
        }
        return definition.arity();
    }

    private void numericQuery(CodeBuilder code, Expr.QueryCall call, int runtime) {
        if (MolangFunctions.builtinQuery(call.name())) {
            numericBuiltin(code, call, runtime);
        } else if (call.args().size() <= 3 && call.args().stream().allMatch(this::isNumeric)) {
            int[] slots = new int[call.args().size()];
            for (int i = 0; i < slots.length; i++) {
                slots[i] = storeNumber(code, call.args().get(i), runtime);
            }
            code.aload(runtime);
            queryRef(code, call);
            for (int slot : slots) {
                code.fload(slot);
            }
            ClassDesc[] parameters = new ClassDesc[slots.length + 1];
            parameters[0] = queryParameterType();
            Arrays.fill(parameters, 1, parameters.length, CD_float);
            code.invokeinterface(RUNTIME, "queryNumber" + slots.length, MethodTypeDesc.of(CD_float, parameters));
        } else if (call.args().size() <= 3) {
            queryOperand(code, call, runtime);
            code.l2i().invokestatic(FLOAT, "intBitsToFloat", MethodTypeDesc.of(CD_float, CD_int));
        } else {
            query(code, call, runtime);
            toFloat(code);
        }
    }

    private void queryOperand(CodeBuilder code, Expr.QueryCall call, int runtime) {
        int[] arguments = new int[call.args().size()];
        for (int i = 0; i < arguments.length; i++) {
            arguments[i] = storeExpression(code, call.args().get(i), runtime);
        }
        code.aload(runtime);
        queryRef(code, call);
        for (int argument : arguments) {
            code.aload(argument);
        }
        ClassDesc[] parameters = new ClassDesc[arguments.length + 1];
        parameters[0] = queryParameterType();
        Arrays.fill(parameters, 1, parameters.length, CD_Object);
        code.invokeinterface(RUNTIME, "queryOperand" + arguments.length, MethodTypeDesc.of(CD_long, parameters));
    }

    private void numericBuiltin(CodeBuilder code, Expr.QueryCall call, int runtime) {
        switch (call.name()) {
            case "count" -> {
                int count = nextSlot++;
                code.fconst_0().fstore(count);
                for (Expr argument : call.args()) {
                    if (numericOperand(argument)) {
                        numericExpression(code, argument, runtime);
                        code.pop().fload(count).fconst_1().fadd().fstore(count);
                    } else {
                        int value = storeExpression(code, argument, runtime);
                        int contribution = nextSlot++;
                        Label scalar = code.newLabel();
                        Label done = code.newLabel();
                        code.aload(value).invokestatic(VALUES, "isArray", MethodTypeDesc.of(CD_boolean, CD_Object)).ifeq(scalar);
                        code.aload(value).invokestatic(VALUES, "length", MethodTypeDesc.of(CD_int, CD_Object)).i2f();
                        code.goto_(done).labelBinding(scalar).fconst_1().labelBinding(done).fstore(contribution);
                        code.fload(count).fload(contribution).fadd().fstore(count);
                    }
                }
                code.fload(count);
            }
            case "all", "any" -> {
                Operand[] arguments = new Operand[call.args().size()];
                for (int i = 0; i < arguments.length; i++) {
                    arguments[i] = storeOperand(code, call.args().get(i), runtime);
                }
                if (arguments.length < 2) {
                    code.fconst_0();
                    return;
                }
                boolean all = "all".equals(call.name());
                Label decisive = code.newLabel();
                Label done = code.newLabel();
                for (int i = 1; i < arguments.length; i++) {
                    Operand first = arguments[0];
                    Operand next = arguments[i];
                    if (first.number() && next.number()) {
                        loadNumber(code, first);
                        loadNumber(code, next);
                        code.fcmpl();
                        if (all) {
                            code.ifne(decisive);
                        } else {
                            code.ifeq(decisive);
                        }
                    } else {
                        loadObject(code, first);
                        loadObject(code, next);
                        code.invokestatic(VALUES, "equal", MethodTypeDesc.of(CD_boolean, CD_Object, CD_Object));
                        if (all) {
                            code.ifeq(decisive);
                        } else {
                            code.ifne(decisive);
                        }
                    }
                }
                code.ldc(all ? 1f : 0f).goto_(done).labelBinding(decisive)
                        .ldc(all ? 0f : 1f).labelBinding(done);
            }
            case "approx_eq" -> {
                int[] arguments = numericArguments(code, call, runtime);
                if (arguments.length < 2) {
                    code.fconst_0();
                    return;
                }
                Label mismatch = code.newLabel();
                Label done = code.newLabel();
                for (int i = 1; i < arguments.length; i++) {
                    code.fload(arguments[i]).fload(arguments[0]).fsub()
                            .invokestatic(CD_Math, "abs", MethodTypeDesc.of(CD_float, CD_float))
                            .ldc(0.000001f).fcmpl().ifge(mismatch);
                }
                code.fconst_1().goto_(done).labelBinding(mismatch).fconst_0().labelBinding(done);
            }
            case "in_range" -> {
                int[] arguments = numericArguments(code, call, runtime);
                if (arguments.length != 3) {
                    code.fconst_0();
                    return;
                }
                Label outside = code.newLabel();
                Label done = code.newLabel();
                code.fload(arguments[0]).fload(arguments[1]).fcmpl().iflt(outside);
                code.fload(arguments[0]).fload(arguments[2]).fcmpg().ifgt(outside);
                code.fconst_1().goto_(done).labelBinding(outside).fconst_0().labelBinding(done);
            }
            case "log", "debug_output" -> {
                int[] arguments = numericArguments(code, call, runtime);
                code.aload(runtime);
                if (arguments.length == 0) {
                    code.fconst_0();
                } else {
                    code.fload(arguments[0]);
                }
                code.invokeinterface(RUNTIME, "log".equals(call.name()) ? "log" : "debugOutput", MethodTypeDesc.of(CD_float, CD_float));
            }
            default -> throw new MolangCompileException("Unknown builtin query: " + call.name());
        }
    }

    private int[] numericArguments(CodeBuilder code, Expr.QueryCall call, int runtime) {
        int[] arguments = new int[call.args().size()];
        for (int i = 0; i < arguments.length; i++) {
            arguments[i] = storeNumber(code, call.args().get(i), runtime);
        }
        return arguments;
    }

    private void expression(CodeBuilder code, Expr expression, int runtime) {
        int objectLocal = localObject(expression);
        if (objectLocal >= 0) {
            code.aload(objectLocal);
            return;
        }
        int local = localTemp(expression);
        if (local >= 0) {
            Label missing = code.newLabel();
            Label done = code.newLabel();
            code.iload(local + 1).ifeq(missing).fload(local);
            box(code);
            code.goto_(done).labelBinding(missing).aconst_null().labelBinding(done);
            return;
        }
        if (isNumeric(expression)) {
            numericExpression(code, expression, runtime);
            box(code);
            return;
        }
        switch (expression) {
            case Expr.NumberLiteral _, Expr.This _, Expr.CompoundAssign _, Expr.Unary _, Expr.Binary _, Expr.MathCall _ -> {
                numericExpression(code, expression, runtime);
                box(code);
            }
            case Expr.StringLiteral value -> code.ldc(value.value());
            case Expr.Group value -> expression(code, value.expression(), runtime);
            case Expr.VariableAccess value -> getVariable(code, runtime, value.scope(), value.name());
            case Expr.VariableAssign value -> {
                int result = storeExpression(code, value.value(), runtime);
                if ("temp".equals(value.scope()) && objectTemps.containsKey(value.name())) {
                    code.aload(result).invokestatic(VALUES, "copy", MethodTypeDesc.of(CD_Object, CD_Object))
                            .astore(objectTemps.getInt(value.name()));
                } else {
                    code.aload("temp".equals(value.scope()) ? 1 : runtime);
                    path(code, value.name());
                    code.aload(result);
                    setVariable(code, value.scope());
                }
                code.aload(result);
            }
            case Expr.MemberAccess value -> {
                expression(code, value.target(), runtime);
                path(code, value.name());
                code.invokestatic(VALUES, "getMember", MethodTypeDesc.of(CD_Object, CD_Object, PATH));
            }
            case Expr.MemberAssign value -> {
                int target = storeExpression(code, value.target(), runtime);
                int result = storeExpression(code, value.value(), runtime);
                code.aload(target);
                path(code, value.name());
                code.aload(result).invokestatic(VALUES, "setMember", MethodTypeDesc.of(CD_void, CD_Object, PATH, CD_Object));
                code.aload(runtime).invokeinterface(RUNTIME, "invalidateQueries", MTD_void);
                code.aload(result);
            }
            case Expr.ArrayAccess value -> {
                if ("variable".equals(value.scope())) {
                    int index = storeExpression(code, value.index(), runtime);
                    code.aload(runtime);
                    path(code, value.name());
                    code.aload(index);
                    toFloat(code);
                    code.f2i().invokeinterface(RUNTIME, "getVariableElementValue", MethodTypeDesc.of(CD_Object, PATH, CD_int));
                } else {
                    index(code, new Expr.VariableAccess(value.scope(), value.name()), value.index(), runtime);
                }
            }
            case Expr.IndexAccess value -> index(code, value.target(), value.index(), runtime);
            case Expr.ArrayAssign value -> arrayAssign(code, value, runtime);
            case Expr.Ternary value -> conditional(code, value.condition(), value.thenBranch(), value.elseBranch(), runtime);
            case Expr.BinaryConditional value -> conditional(code, value.condition(), value.thenBranch(), new Expr.NumberLiteral(0), runtime);
            case Expr.NullCoalescing value -> {
                int result = storeExpression(code, value.left(), runtime);
                Label present = code.newLabel();
                Label done = code.newLabel();
                code.aload(result).invokestatic(VALUES, "isResolved", MethodTypeDesc.of(CD_boolean, CD_Object)).ifne(present);
                expression(code, value.right(), runtime);
                code.goto_(done).labelBinding(present).aload(result).labelBinding(done);
            }
            case Expr.Scope value -> {
                statement(code, value.block(), runtime);
                number(code, 0);
            }
            case Expr.ContextSwitch value -> contextSwitch(code, value, runtime);
            case Expr.QueryCall value -> query(code, value, runtime);
            case Expr.Break _ -> {
                jump(code, breaks, "break");
                number(code, 0);
            }
            case Expr.Continue _ -> {
                jump(code, continues, "continue");
                number(code, 0);
            }
        }
    }

    private void path(CodeBuilder code, String name) {
        int index = pathIndexes.getInt(name);
        if (index < 0) {
            index = paths.size();
            paths.add(new MolangPath(name, index));
            pathIndexes.put(name, index);
        }
        code.aload(0).getfield(COMPILED, "paths", PATH_ARRAY).ldc(index).aaload();
    }

    private void queryRef(CodeBuilder code, Expr.QueryCall call) {
        MolangQueryDefinition definition = foreignContext ? null : options.querySchema().get(call.name());
        if (definition == null) {
            definition = MolangQueryDefinition.dynamic(call.name());
        }
        if (call.args().size() < definition.minArguments() || call.args().size() > definition.maxArguments()) {
            throw new MolangCompileException("Invalid argument count for query." + call.name());
        }
        for (int argument = 0; argument < call.args().size(); argument++) {
            Expr value = call.args().get(argument);
            while (value instanceof Expr.Group(Expr expression)) {
                value = expression;
            }
            MolangQueryDefinition.ValueType expected = definition.argumentType(argument);
            if (expected != MolangQueryDefinition.ValueType.DYNAMIC
                    && ((value instanceof Expr.NumberLiteral && expected != MolangQueryDefinition.ValueType.NUMBER)
                    || (value instanceof Expr.StringLiteral && expected != MolangQueryDefinition.ValueType.STRING))) {
                throw new MolangCompileException("Invalid argument type for query." + call.name() + " at index " + argument);
            }
        }
        String key = foreignContext ? call.name() + "@foreign" : call.name();
        int index = queryIndexes.getInt(key);
        if (index < 0) {
            index = queries.size();
            queries.add(new MolangQueryRef(call.name(), index, definition, foreignContext));
            queryIndexes.put(key, index);
        }
        if (queryParameterType().equals(CD_String)) {
            // 通用入口使用常量查询名称，便于 JIT 内联查找；绑定入口使用预解析查询句柄。
            code.ldc(call.name());
        } else {
            code.aload(0).getfield(COMPILED, "queries", QUERY_ARRAY).ldc(index).aaload();
        }
    }

    private ClassDesc queryParameterType() {
        return options.stableVariableReads() || !options.querySchema().definitions().isEmpty() ? QUERY_REF : CD_String;
    }

    private int storeExpression(CodeBuilder code, Expr expression, int runtime) {
        int result = nextSlot++;
        expression(code, expression, runtime);
        code.astore(result);
        return result;
    }

    private void getVariable(CodeBuilder code, int runtime, String scope, String name) {
        if ("temp".equals(scope) && objectTemps.containsKey(name)) {
            code.aload(objectTemps.getInt(name));
            return;
        }
        String method = switch (scope) {
            case "variable" -> "getVariableValue";
            case "temp" -> "getTempValue";
            case "context" -> "getContextValue";
            case "array" -> "getArrayValue";
            default -> throw new MolangCompileException("Unknown variable scope: " + scope);
        };
        code.aload("temp".equals(scope) ? 1 : runtime);
        path(code, name);
        code.invokeinterface(RUNTIME, method, MethodTypeDesc.of(CD_Object, PATH));
    }

    private static void setVariable(CodeBuilder code, String scope) {
        String method = switch (scope) {
            case "variable" -> "setVariableValue";
            case "temp" -> "setTempValue";
            default -> throw new MolangCompileException("Cannot assign to scope: " + scope);
        };
        code.invokeinterface(RUNTIME, method, MethodTypeDesc.of(CD_void, PATH, CD_Object));
    }

    private void arrayNumber(CodeBuilder code, Expr.ArrayAccess value, int runtime, boolean packed) {
        if ("variable".equals(value.scope())) {
            int offset = storeNumber(code, value.index(), runtime);
            code.aload(runtime);
            path(code, value.name());
            code.fload(offset).f2i().invokeinterface(RUNTIME, packed ? "getVariableElementOperand" : "getVariableElementNumber", MethodTypeDesc.of(packed ? CD_long : CD_float, PATH, CD_int));
        } else {
            indexNumber(code, new Expr.VariableAccess(value.scope(), value.name()), value.index(), runtime, packed);
        }
    }

    private void indexNumber(CodeBuilder code, Expr target, Expr index, int runtime, boolean packed) {
        int array = storeExpression(code, target, runtime);
        int offset = storeNumber(code, index, runtime);
        code.aload(array).fload(offset).f2i().invokestatic(VALUES, packed ? "getElementOperand" : "getElementFloat", MethodTypeDesc.of(packed ? CD_long : CD_float, CD_Object, CD_int));
    }

    private void index(CodeBuilder code, Expr target, Expr index, int runtime) {
        int array = storeExpression(code, target, runtime);
        int offset = storeExpression(code, index, runtime);
        code.aload(array).aload(offset);
        toFloat(code);
        code.f2i().invokestatic(VALUES, "getElement", MethodTypeDesc.of(CD_Object, CD_Object, CD_int));
    }

    private void arrayAssign(CodeBuilder code, Expr.ArrayAssign value, int runtime) {
        int index = storeExpression(code, value.index(), runtime);
        int result = storeExpression(code, value.value(), runtime);
        code.aload("temp".equals(value.scope()) ? 1 : runtime);
        path(code, value.name());
        code.aload(index);
        toFloat(code);
        code.f2i().aload(result).invokeinterface(RUNTIME, "temp".equals(value.scope()) ? "setTempElementValue" : "setVariableElementValue", MethodTypeDesc.of(CD_void, PATH, CD_int, CD_Object));
        code.aload(result);
    }

    private void conditional(CodeBuilder code, Expr condition, Expr thenBranch, Expr elseBranch, int runtime) {
        Label otherwise = code.newLabel();
        Label done = code.newLabel();
        numericExpression(code, condition, runtime);
        code.fconst_0().fcmpl().ifeq(otherwise);
        expression(code, thenBranch, runtime);
        code.goto_(done).labelBinding(otherwise);
        expression(code, elseBranch, runtime);
        code.labelBinding(done);
    }

    private void contextSwitch(CodeBuilder code, Expr.ContextSwitch value, int runtime) {
        int target = storeExpression(code, value.target(), runtime);
        int alternate = nextSlot++;
        Label missing = code.newLabel();
        Label done = code.newLabel();
        code.aload(target).invokestatic(VALUES, "asRuntime", MethodTypeDesc.of(RUNTIME, CD_Object)).astore(alternate);
        code.aload(alternate).ifnull(missing);
        switchesContext = true;
        boolean previousContext = foreignContext;
        foreignContext = true;
        code.aload(1).invokeinterface(RUNTIME, "invalidateQueries", MTD_void);
        try {
            expression(code, value.expression(), alternate);
        } finally {
            foreignContext = previousContext;
        }
        code.goto_(done).labelBinding(missing).aconst_null().labelBinding(done);
    }

    private void query(CodeBuilder code, Expr.QueryCall call, int runtime) {
        if (call.args().size() <= 3) {
            int[] arguments = new int[call.args().size()];
            for (int i = 0; i < arguments.length; i++) {
                arguments[i] = storeExpression(code, call.args().get(i), runtime);
            }
            code.aload(runtime);
            queryRef(code, call);
            for (int argument : arguments) {
                code.aload(argument);
            }
            ClassDesc[] parameters = new ClassDesc[arguments.length + 1];
            parameters[0] = queryParameterType();
            Arrays.fill(parameters, 1, parameters.length, CD_Object);
            code.invokeinterface(RUNTIME, "queryValue" + arguments.length, MethodTypeDesc.of(CD_Object, parameters));
            return;
        }
        int arguments = nextSlot++;
        code.ldc(call.args().size()).anewarray(CD_Object).astore(arguments);
        for (int i = 0; i < call.args().size(); i++) {
            int argument = storeExpression(code, call.args().get(i), runtime);
            code.aload(arguments).ldc(i).aload(argument).aastore();
        }
        code.aload(runtime);
        queryRef(code, call);
        code.aload(arguments).invokeinterface(RUNTIME, "queryValue", MethodTypeDesc.of(CD_Object, queryParameterType(), OBJECT_ARRAY));
    }

    private static void jump(CodeBuilder code, Deque<Label> labels, String keyword) {
        if (labels.isEmpty()) {
            throw new MolangCompileException(keyword + " is only valid inside a loop");
        }
        code.goto_(labels.peek());
    }

    private static void number(CodeBuilder code, float value) {
        code.ldc(value);
        box(code);
    }

    private static void box(CodeBuilder code) {
        code.invokestatic(FLOAT, "valueOf", MethodTypeDesc.of(FLOAT, CD_float));
    }

    private static void toFloat(CodeBuilder code) {
        code.invokestatic(VALUES, "toFloat", MethodTypeDesc.of(CD_float, CD_Object));
    }
}
