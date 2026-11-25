package cn.nukkit.molang.compiler;

import cn.nukkit.molang.MolangCompileOptions;
import cn.nukkit.molang.MolangBoundExpression;
import cn.nukkit.molang.MolangExpression;
import cn.nukkit.molang.parser.ast.Expr;
import cn.nukkit.molang.parser.ast.Stmt;
import cn.nukkit.molang.runtime.*;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.lang.classfile.ClassFile;
import java.lang.classfile.CodeBuilder;
import java.lang.classfile.Label;
import java.lang.constant.ClassDesc;
import java.lang.constant.DynamicConstantDesc;
import java.lang.constant.MethodTypeDesc;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.util.*;
import javax.annotation.Nullable;

import static java.lang.classfile.ClassFile.*;
import static java.lang.constant.ConstantDescs.*;

/**
 * 为运行时类型生成 JVM 字节码；解析和节点分派仅发生在编译期间。
 */
final class TypedMolangCompiler {
    private static final int MAX_SPECIALIZED_LOOP_DEPTH = 2;
    private static final int MAX_SPECIALIZED_LOOP_NODES = 120;
    private static final int UNSUPPORTED_SINGLE_READ = 2;

    private static final ClassDesc COMPILED = ClassDesc.of(TypedMolangCompiler.class.getPackageName(), "CompiledExpr");
    private static final ClassDesc EXPRESSION = MolangExpression.class.describeConstable().orElseThrow();
    private static final ClassDesc UNBOUND = MolangBatchCompiler.UnboundProgram.class.describeConstable().orElseThrow();
    private static final ClassDesc RUNTIME = MolangRuntime.class.describeConstable().orElseThrow();
    private static final ClassDesc VALUES = MolangValues.class.describeConstable().orElseThrow();
    private static final ClassDesc MATH = MolangMath.class.describeConstable().orElseThrow();
    private static final ClassDesc PATH = MolangPath.class.describeConstable().orElseThrow();
    private static final ClassDesc QUERY_SITE = MolangQuerySite.class.describeConstable().orElseThrow();
    private static final ClassDesc QUERY_REF = MolangQueryRef.class.describeConstable().orElseThrow();
    private static final ClassDesc BINDING_PLAN = MolangBindingPlan.class.describeConstable().orElseThrow();
    private static final ClassDesc LINKED_QUERY = MolangBindingPlan.LinkedQueryExpression.class.describeConstable().orElseThrow();
    private static final ClassDesc LINKED_VARIABLES = MolangBindingPlan.LinkedVariables.class.describeConstable().orElseThrow();
    private static final ClassDesc NUMBER_CALLBACK = MolangNumberContextQuery.class.describeConstable().orElseThrow();
    private static final DynamicConstantDesc<MolangBindingPlan> PLAN = DynamicConstantDesc.ofNamed(BSM_CLASS_DATA_AT, DEFAULT_NAME, BINDING_PLAN, 0);
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
    @Nullable
    private Stmt.ForEach ownedIteration;
    private final Object2IntMap<String> pathIndexes;
    private final List<MolangPath> paths;
    private final Object2IntMap<String> queryIndexes;
    private final List<MolangQueryRef> queries;
    // 每个代码段只保存实际引用的符号，首项留给整个绑定计划。
    private final Map<Object, DynamicConstantDesc<?>> symbolConstants = new LinkedHashMap<>();
    private final Map<String, MolangQuerySite> genericSites = new HashMap<>();
    private final Set<Integer> linkableVariables = new LinkedHashSet<>();
    private final Set<Integer> linkableNumbers = new LinkedHashSet<>();
    private boolean linkedBody;
    private final boolean singleEvaluationRead;
    private boolean propertyInputs;
    @Nullable
    private BatchNumberPlan batchPlan;
    @Nullable
    private BatchNumberPlan.Output batchOutput;
    private final Object2IntMap<String> numericTemps = new Object2IntOpenHashMap<>();
    private final Object2IntMap<String> objectTemps = new Object2IntOpenHashMap<>();
    private Object2IntMap<String> scalarInputs = Object2IntMaps.emptyMap();
    @Nullable
    private MolangExpression structFallback;
    private Object2IntMap<Expr> hoistedNumbers = new Object2IntOpenHashMap<>();
    private Map<Expr.VariableAccess, Operand> loopOperands = Map.of();
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
        this.localPlan = new NumericLocalPlan(this.program, options);
        this.singleEvaluationRead = !segmented && singleEvaluationRead();
        this.alwaysNumericResult = !segmented && new NumericResultPlan(this.program, options, localPlan.numericTemps()).numeric();
        pathIndexes.defaultReturnValue(-1);
        queryIndexes.defaultReturnValue(-1);
        numericTemps.defaultReturnValue(-1);
        objectTemps.defaultReturnValue(-1);
        hoistedNumbers.defaultReturnValue(-1);
    }

    // 只有不执行其它宿主代码的表达式，才允许在绑定到真实数值属性后省去求值作用域。
    private boolean propertyExpression() {
        return !segmented && structFallback == null && BatchNumberPlan.supports(program, options, false);
    }

    private boolean singleEvaluationRead() {
        if (program.statements().size() != 1) {
            return false;
        }
        return switch (program.statements().getFirst()) {
            case Stmt.ReturnStmt returned -> evaluationReads(returned.value()) == 1;
            case Stmt.ExpressionStmt statement -> evaluationReads(statement.expression()) == 1;
            default -> false;
        };
    }

    // 只接受不会观察宿主缓存的数值操作和 JVM 局部变量，未证明安全的节点按多次读取处理。
    private int evaluationReads(Expr expression) {
        return switch (expression) {
            case Expr.NumberLiteral _ -> 0;
            case Expr.Group group -> evaluationReads(group.expression());
            case Expr.Unary unary -> evaluationReads(unary.operand());
            case Expr.Binary binary -> evaluationReads(binary.left()) + evaluationReads(binary.right());
            case Expr.VariableAccess read -> "temp".equals(read.scope()) && localPlan.numericTemps().contains(read.name()) ? 0 : UNSUPPORTED_SINGLE_READ;
            case Expr.VariableAssign write -> "temp".equals(write.scope()) && localPlan.numericTemps().contains(write.name()) ? evaluationReads(write.value()) : UNSUPPORTED_SINGLE_READ;
            case Expr.QueryCall query -> {
                MolangQueryDefinition definition = options.querySchema().get(query.name());
                yield query.args().isEmpty() && !MolangFunctions.builtinQuery(query.name()) && definition != null && definition.stability() == MolangQueryDefinition.Stability.EVALUATION ? 1 : UNSUPPORTED_SINGLE_READ;
            }
            default -> UNSUPPORTED_SINGLE_READ;
        };
    }

    MolangExpression compile() {
        if (!segmented) {
            Stmt.Program expanded = TempStructCopies.expand(program);
            if (expanded != program) {
                TypedMolangCompiler fast = new TypedMolangCompiler(expanded, options, symbols, false);
                fast.scalarInputs = new Object2IntLinkedOpenHashMap<>();
                for (String name : TempStructCopies.inputs(expanded)) {
                    fast.scalarInputs.put(name, -1);
                    fast.internPath(name);
                }
                fast.structFallback = compileClass();
                return fast.compileClass();
            }
        }
        if (!segmented && localPlan.needsRuntimeTemps()) {
            Stmt.ForEach loop = OwnedIteration.find(program, localPlan.numericTemps());
            if (loop != null && smallLoop(loop.body())) {
                ownedIteration = loop;
            }
        }
        return compileClass();
    }

    private MolangExpression compileClass() {
        propertyInputs = propertyExpression();
        byte[] bytes = classBytes();
        try {
            MethodHandles.Lookup lookup = defineClass(bytes);
            MolangExpression compiled = (MolangExpression) lookup.findConstructor(lookup.lookupClass(), MethodType.methodType(void.class)).invoke();
            boolean scoped = localPlan.needsRuntimeTemps() || switchesContext;
            if (segmented || !scoped && linkableVariables.isEmpty()) {
                return compiled;
            }
            NumericLink link = null;
            NumericLink ownedLink = null;
            if (!linkableNumbers.isEmpty() || !linkableVariables.isEmpty()) {
                linkedBody = true;
                byte[] linkedBytes = classBytes();
                MethodHandles.Lookup direct = defineClass(linkedBytes);
                MethodType init = MethodType.methodType(void.class, MolangRuntime.class, MolangNumberContextQuery[].class, int[].class, boolean.class);
                if (ownedIteration != null) {
                    init = init.appendParameterTypes(boolean.class);
                }
                MethodHandle factory = direct.findConstructor(direct.lookupClass(), init).asType(init.changeReturnType(MolangExpression.class));
                int[] queries = linkableNumbers.stream().mapToInt(Integer::intValue).toArray();
                int[] paths = linkableVariables.stream().mapToInt(Integer::intValue).toArray();
                boolean single = singleEvaluationRead && structFallback == null;
                MolangBindingPlan symbols = compiled.bindingPlan();
                BatchNumberPlan batch = null;
                if (structFallback == null && queries.length > 0 && queries.length == symbols.queryCount()
                        && paths.length == symbols.pathCount() && (propertyInputs || options.stableVariableReads()
                        && BatchNumberPlan.supports(program, options, true))) {
                    batch = BatchNumberPlan.create(program, options, symbols, queries, paths, single, bytes, linkedBytes);
                }
                if (ownedIteration != null) {
                    int last = init.parameterCount() - 1;
                    ownedLink = new NumericLink(MethodHandles.insertArguments(factory, last, true), queries, paths, single, propertyInputs, batch);
                    factory = MethodHandles.insertArguments(factory, last, false);
                }
                link = new NumericLink(factory, queries, paths, single, propertyInputs, batch);
            }
            if (ownedIteration != null) {
                MolangExpression owned = (MolangExpression) lookup.findConstructor(lookup.lookupClass(), MethodType.methodType(void.class, boolean.class)).invoke(true);
                return new OwnedIteration(new ScopedExpression(compiled, switchesContext, link), new ScopedExpression(owned, switchesContext, ownedLink));
            }
            return scoped ? new ScopedExpression(compiled, switchesContext, link) : new UnscopedLink(compiled, link, canRunLoopOnOwner());
        } catch (MolangCompileException exception) {
            throw exception;
        } catch (Throwable exception) {
            throw new MolangCompileException("Failed to load typed expression", exception);
        }
    }

    private byte[] classBytes() {
        boolean queryScope = linkedBody && !switchesContext && !linkableNumbers.isEmpty();
        String numBody = queryScope ? "evaluateBody" : "evaluate";
        String objBody = queryScope ? "evaluateValueBody" : "evaluateValue";
        return ClassFile.of().build(COMPILED, builder -> {
            builder.withFlags(ACC_PUBLIC | ACC_FINAL | ACC_SYNTHETIC);
            builder.withInterfaceSymbols(linkedBody ? EXPRESSION : UNBOUND);
            if (ownedIteration != null) {
                builder.withField("ownedTemps", CD_boolean, ACC_PRIVATE | ACC_FINAL);
            }
            if (linkedBody) {
                if (queryScope) {
                    builder.withSuperclass(LINKED_QUERY);
                } else if (!linkableVariables.isEmpty()) {
                    builder.withSuperclass(LINKED_VARIABLES);
                }
                if (!queryScope && !linkableNumbers.isEmpty()) {
                    builder.withField("owner", RUNTIME, ACC_PRIVATE | ACC_FINAL);
                }
                for (int path : linkableVariables) {
                    builder.withField("v" + path, CD_int, ACC_PRIVATE | ACC_FINAL);
                }
                for (int slot : linkableNumbers) {
                    builder.withField("q" + slot, NUMBER_CALLBACK, ACC_PRIVATE | ACC_FINAL);
                }
                MethodTypeDesc init = MethodTypeDesc.of(CD_void, RUNTIME, NUMBER_CALLBACK.arrayType(), CD_int.arrayType(), CD_boolean);
                if (ownedIteration != null) {
                    init = init.insertParameterTypes(init.parameterCount(), CD_boolean);
                }
                builder.withMethodBody(INIT_NAME, init, ACC_PRIVATE, code -> {
                    if (queryScope) {
                        code.aload(0).aload(1).iload(4).invokespecial(LINKED_QUERY, INIT_NAME, MethodTypeDesc.of(CD_void, RUNTIME, CD_boolean));
                    } else if (linkableVariables.isEmpty()) {
                        code.aload(0).invokespecial(CD_Object, INIT_NAME, MTD_void);
                    } else {
                        code.aload(0).aload(1).invokespecial(LINKED_VARIABLES, INIT_NAME, MethodTypeDesc.of(CD_void, RUNTIME));
                    }
                    if (!queryScope && !linkableNumbers.isEmpty()) {
                        code.aload(0).aload(1).putfield(COMPILED, "owner", RUNTIME);
                    }
                    int pathIndex = 0;
                    for (int path : linkableVariables) {
                        code.aload(0).aload(3).ldc(pathIndex++).iaload().putfield(COMPILED, "v" + path, CD_int);
                    }
                    int index = 0;
                    for (int slot : linkableNumbers) {
                        code.aload(0).aload(2).ldc(index++).aaload().putfield(COMPILED, "q" + slot, NUMBER_CALLBACK);
                    }
                    if (ownedIteration != null) {
                        code.aload(0).iload(5).putfield(COMPILED, "ownedTemps", CD_boolean);
                    }
                    code.return_();
                });
            } else {
                builder.withMethodBody(INIT_NAME, MTD_void, ACC_PUBLIC, code -> code.aload(0).invokespecial(CD_Object, INIT_NAME, MTD_void).return_());
                if (ownedIteration != null) {
                    builder.withMethodBody(INIT_NAME, MethodTypeDesc.of(CD_void, CD_boolean), ACC_PRIVATE, code ->
                            code.aload(0).invokespecial(COMPILED, INIT_NAME, MTD_void)
                                    .aload(0).iload(1).putfield(COMPILED, "ownedTemps", CD_boolean).return_());
                }
            }
            builder.withMethodBody("bindingPlan", MethodTypeDesc.of(BINDING_PLAN), ACC_PUBLIC, code -> code.ldc(PLAN).areturn());
            builder.withMethodBody(numBody, MethodTypeDesc.of(CD_float, RUNTIME), ACC_PUBLIC, code -> {
                generateBody(code, true);
            });
            builder.withMethodBody(objBody, MethodTypeDesc.of(CD_Object, RUNTIME), ACC_PUBLIC, code -> {
                if (alwaysNumericResult && structFallback == null) {
                    code.aload(0).aload(1).invokevirtual(COMPILED, numBody, MethodTypeDesc.of(CD_float, RUNTIME));
                    box(code);
                    code.areturn();
                } else {
                    generateBody(code, false);
                }
            });
            // 在初始化时完成常量链接，首次游戏求值直接读取已解析的路径和查询描述。
            builder.withMethodBody(CLASS_INIT_NAME, MTD_void, ACC_STATIC, code -> {
                code.ldc(PLAN).pop();
                for (DynamicConstantDesc<?> symbol : symbolConstants.values()) {
                    code.ldc(symbol).pop();
                }
                code.return_();
            });
        });
    }

    private MethodHandles.Lookup defineClass(byte[] bytes) throws IllegalAccessException {
        symbols.switchesContext |= switchesContext;
        List<Object> classData = new ArrayList<>(symbolConstants.size() + 1);
        classData.add(new MolangBindingPlan(paths, queries));
        classData.addAll(symbolConstants.keySet());
        return MethodHandles.lookup().defineHiddenClassWithClassData(bytes, List.copyOf(classData), true, MethodHandles.Lookup.ClassOption.NESTMATE);
    }

    // 字面循环只读取单层变量、写入 JVM 数值局部槽时，可在绑定宿主上直接执行原程序。
    private boolean canRunLoopOnOwner() {
        if (structFallback != null || !options.stableVariableReads() || localPlan.needsRuntimeTemps() || !localPlan.objectTemps().isEmpty()) {
            return false;
        }
        Set<String> locals = localPlan.numericTemps();
        List<Stmt> statements = program.statements();
        if (statements.isEmpty() || !(statements.getLast() instanceof Stmt.ReturnStmt(Expr.VariableAccess result)) || !"temp".equals(result.scope()) || !locals.contains(result.name())) {
            return false;
        }
        boolean found = false;
        for (int i = 0; i < statements.size() - 1; i++) {
            Stmt statement = statements.get(i);
            if (statement instanceof Stmt.ExpressionStmt(Expr.VariableAssign assign) && "temp".equals(assign.scope()) && locals.contains(assign.name()) && assign.value() instanceof Expr.NumberLiteral) {
                continue;
            }
            if (found || !(statement instanceof Stmt.Loop(Expr count, Stmt body)) || !(count instanceof Expr.NumberLiteral)) {
                return false;
            }
            List<Expr.VariableAccess> reads = LoopOperandReads.find(body, locals);
            if (reads.isEmpty() || reads.stream().anyMatch(read -> read.name().indexOf('.') >= 0)) {
                return false;
            }
            found = true;
        }
        return found;
    }

    @Nullable
    static MolangExpression batchOwnerBody(MolangExpression program) {
        program = OwnedIteration.original(program);
        return program instanceof UnscopedLink local && local.localLoop ? local.compiled : null;
    }

    private record UnscopedLink(MolangExpression compiled, NumericLink link, boolean localLoop) implements MolangExpression {
        @Override
        public float evaluate(MolangRuntime runtime) {
            return compiled.evaluate(runtime);
        }

        @Override
        @Nullable
        public Object evaluateValue(MolangRuntime runtime) {
            return compiled.evaluateValue(runtime);
        }

        @Override
        public MolangBindingPlan bindingPlan() {
            return compiled.bindingPlan();
        }

        @Override
        public MolangBoundExpression bind(MolangRuntime runtime) {
            MolangBindingPlan plan = bindingPlan();
            MolangRuntime view = runtime.bind(plan);
            MolangExpression linked = link.bind(plan, view);
            return new MolangBoundExpression(linked != null ? linked : compiled, view);
        }
    }

    private record NumericLink(MethodHandle factory, int[] slots, int[] paths, boolean singleEvaluationRead, boolean propertyExpression, @Nullable BatchNumberPlan batch) {
        private static final MolangNumberContextQuery[] NO_QUERIES = {};
        private static final int[] NO_VARIABLES = {};

        private boolean onlyLinkedQueries(MolangBindingPlan plan) {
            return propertyExpression && slots.length != 0 && slots.length == plan.queryCount() && plan.pathCount() == 0;
        }

        @Nullable
        MolangExpression bind(MolangBindingPlan plan, MolangRuntime view) {
            return bind(plan, view, false);
        }

        @Nullable
        MolangExpression bind(MolangBindingPlan plan, MolangRuntime view, boolean managedByBatch) {
            MolangNumberContextQuery[] functions = slots.length == 0 ? NO_QUERIES : new MolangNumberContextQuery[slots.length];
            MolangRuntime owner = onlyLinkedQueries(plan) ? plan.linkProperties(view, slots, functions) : null;
            boolean managesScope = owner == null && !managedByBatch;
            if (owner == null) {
                owner = singleEvaluationRead ? plan.linkSingleEvaluationQuery(view, slots[0], functions) : plan.linkCompiledNumberQueries(view, slots, functions);
                if (owner == null) {
                    return null;
                }
            }
            int[] variableSlots = paths.length == 0 ? NO_VARIABLES : new int[paths.length];
            if (paths.length != 0 && !plan.linkVariableRoots(view, paths, variableSlots) && slots.length == 0) {
                return null;
            }
            try {
                return (MolangExpression) factory.invokeExact(owner, functions, variableSlots, managesScope);
            } catch (Throwable error) {
                throw new MolangCompileException("Failed to bind numeric expression", error);
            }
        }
    }

    @Nullable
    static MolangExpression batchBody(MolangExpression expression) {
        expression = OwnedIteration.original(expression);
        return expression instanceof ScopedExpression scoped && !scoped.invalidatesQueries ? scoped.compiled : null;
    }

    @Nullable
    static MolangExpression linkBatchBody(MolangExpression expression, MolangRuntime view, boolean managedScope) {
        expression = OwnedIteration.original(expression);
        if (expression instanceof UnscopedLink unscoped) {
            return unscoped.link.bind(unscoped.bindingPlan(), view);
        }
        if (expression instanceof ScopedExpression scoped) {
            return scoped.linkedBody(view, managedScope);
        }
        return null;
    }

    @Nullable
    static BatchNumberPlan batchNumber(MolangExpression program) {
        program = OwnedIteration.original(program);
        return program instanceof ScopedExpression scoped && !scoped.invalidatesQueries && scoped.link != null ? scoped.link.batch : null;
    }

    static boolean isUnboundProgram(MolangExpression program) {
        program = OwnedIteration.original(program);
        if (program instanceof ScopedExpression scoped) {
            return scoped.compiled instanceof MolangBatchCompiler.UnboundProgram;
        }
        if (program instanceof UnscopedLink unscoped) {
            return unscoped.compiled instanceof MolangBatchCompiler.UnboundProgram;
        }
        return program instanceof MolangBatchCompiler.UnboundProgram;
    }

    void emitBatch(CodeBuilder code, Expr value, BatchNumberPlan plan, BatchNumberPlan.Output output) {
        numericReturn = true;
        nextSlot = BatchNumberPlan.FIRST_EXPRESSION_LOCAL;
        batchPlan = plan;
        batchOutput = output;
        numericExpression(code, value, 3);
    }

    private record ScopedExpression(MolangExpression compiled, boolean invalidatesQueries, @Nullable NumericLink link) implements MolangExpression {
        @Override
        public MolangBoundExpression bind(MolangRuntime runtime) {
            MolangBindingPlan plan = bindingPlan();
            MolangRuntime view = runtime.bind(plan);
            MolangExpression linked = linkedBody(view, false);
            // 所有读取都已绑定到真实宿主时，执行体无需保留解析查询时使用的视图。
            if (linked instanceof MolangBindingPlan.LinkedQueryExpression && link.onlyLinkedQueries(plan) && plan.matchesView(runtime, view)) {
                return new MolangBoundExpression(linked, runtime);
            }
            return new MolangBoundExpression(linked != null ? linked : this, view);
        }

        @Nullable
        private MolangExpression linkedBody(MolangRuntime view, boolean managedByBatch) {
            MolangExpression linked = link == null ? null : link.bind(bindingPlan(), view, managedByBatch);
            if (linked == null || managedByBatch || linked instanceof MolangBindingPlan.LinkedQueryExpression) {
                return linked;
            }
            return new ScopedExpression(linked, invalidatesQueries, null);
        }

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
        if (structFallback != null) {
            guardStructInputs(code, numeric);
        }
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

    private void guardStructInputs(CodeBuilder code, boolean numeric) {
        Label fallback = code.newLabel();
        Label ready = code.newLabel();
        code.aload(1).invokestatic(VALUES, "isBuiltinStorage", MethodTypeDesc.of(CD_boolean, RUNTIME)).ifeq(fallback);
        for (Object2IntMap.Entry<String> input : scalarInputs.object2IntEntrySet()) {
            int local = nextSlot++;
            input.setValue(local);
            code.aload(1);
            path(code, input.getKey());
            code.invokeinterface(RUNTIME, "getVariableValue", MethodTypeDesc.of(CD_Object, PATH))
                    .dup().astore(local)
                    .invokestatic(VALUES, "isScalarInput", MethodTypeDesc.of(CD_boolean, CD_Object)).ifeq(fallback);
        }
        code.goto_(ready).labelBinding(fallback);
        loadSymbol(code, structFallback, EXPRESSION);
        code.aload(1);
        if (numeric) {
            code.invokeinterface(EXPRESSION, "evaluate", MethodTypeDesc.of(CD_float, RUNTIME)).freturn();
        } else {
            code.invokeinterface(EXPRESSION, "evaluateValue", MethodTypeDesc.of(CD_Object, RUNTIME)).areturn();
        }
        code.labelBinding(ready);
    }

    private void statement(CodeBuilder code, Stmt statement, int runtime) {
        int scratchBase = nextSlot;
        switch (statement) {
            case Stmt.Program value -> value.statements().forEach(child -> statement(code, child, runtime));
            case Stmt.Block value -> value.statements().forEach(child -> statement(code, child, runtime));
            case Stmt.ExpressionStmt value -> {
                if (value.expression() instanceof Expr.VariableAssign(String scope, String name, Expr.ContextSwitch(Expr target, Expr.VariableAccess(String fieldScope, String field)))
                        && "variable".equals(scope) && "variable".equals(fieldScope)) {
                    int reference = storeExpression(code, target, runtime);
                    switchesContext = true;
                    code.aload(1).aload(runtime).aload(reference);
                    path(code, field);
                    path(code, name);
                    code.invokestatic(MolangActorContext.class.describeConstable().orElseThrow(), "assignArrow",
                            MethodTypeDesc.of(CD_void, RUNTIME, RUNTIME, CD_Object, PATH, PATH));
                } else if (value.expression() instanceof Expr.VariableAssign(String targetScope, String targetName, Expr targetValue)
                        && "variable".equals(targetScope)
                        && targetValue instanceof Expr.VariableAccess(String sourceScope, String sourceName)
                        && "variable".equals(sourceScope)) {
                    code.aload(runtime);
                    path(code, targetName);
                    path(code, sourceName);
                    code.invokeinterface(RUNTIME, "copyVariableValue", MethodTypeDesc.of(CD_void, PATH, PATH));
                } else if (value.expression() instanceof Expr.ArrayAssign(String targetScope, String targetName, Expr.VariableAccess(String indexScope, String indexName), Expr.VariableAccess(String sourceScope, String sourceName))
                        && "variable".equals(targetScope) && "variable".equals(indexScope) && "variable".equals(sourceScope)) {
                    code.aload(runtime);
                    path(code, targetName);
                    path(code, indexName);
                    path(code, sourceName);
                    code.invokestatic(VALUES, "copyVariableToElement", MethodTypeDesc.of(CD_void, RUNTIME, PATH, PATH, PATH));
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
                .filter(expression -> !hoistedNumbers.containsKey(expression))
                .toList();
        if (breaks.size() >= MAX_SPECIALIZED_LOOP_DEPTH || !smallLoop(loop.body())) {
            loopBody(code, count, loop.body(), runtime);
            return;
        }
        if (invariants.isEmpty()) {
            List<Expr.VariableAccess> reads = segmented || foreignContext ? List.of() : LoopOperandReads.find(loop.body(), numericTemps.keySet());
            if (reads.isEmpty()) {
                loopBody(code, count, loop.body(), runtime);
            } else {
                Label done = code.newLabel();
                code.iload(count).ifle(done);
                withLoopOperands(code, runtime, reads, () -> loopBody(code, count, loop.body(), runtime));
                code.labelBinding(done);
            }
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

    private void withLoopOperands(CodeBuilder code, int runtime, List<Expr.VariableAccess> reads, Runnable emitBody) {
        if (reads.isEmpty()) {
            emitBody.run();
            return;
        }
        Label fallback = code.newLabel();
        Label done = code.newLabel();
        code.aload(runtime).invokestatic(VALUES, "isBuiltinStorage", MethodTypeDesc.of(CD_boolean, RUNTIME)).ifeq(fallback);
        Map<Expr.VariableAccess, Operand> previous = loopOperands;
        Map<Expr.VariableAccess, Operand> saved = new HashMap<>(previous);
        for (Expr.VariableAccess read : reads) {
            saved.put(read, storeArithmeticOperand(code, read, runtime));
        }
        loopOperands = saved;
        emitBody.run();
        loopOperands = previous;
        code.goto_(done).labelBinding(fallback);
        emitBody.run();
        code.labelBinding(done);
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
        if (!segmented && !foreignContext && NumericForEachPlan.eligible(loop) && smallLoop(loop.body())) {
            Label fallback = code.newLabel();
            Label complete = code.newLabel();
            code.iload(length).ifle(complete);
            code.aload(runtime);
            if (options.stableVariableReads()) {
                code.invokeinterface(RUNTIME, "supportsLocalTempAccess", MethodTypeDesc.of(CD_boolean));
            } else {
                code.invokestatic(VALUES, "isBuiltinStorage", MethodTypeDesc.of(CD_boolean, RUNTIME));
            }
            code.ifeq(fallback);
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
        code.fconst_0().fstore(targetSlot).iconst_0().istore(targetSlot + 1);
        List<Expr.VariableAccess> reads = LoopOperandReads.find(loop.body(), numericTemps.keySet());
        withLoopOperands(code, runtime, reads, () -> numericForEachBody(code, loop, runtime, array, index, length, targetSlot));
        if (previous >= 0) {
            numericTemps.put(target, previous);
        } else {
            numericTemps.removeInt(target);
        }
        if (previousObject >= 0) {
            objectTemps.put(target, previousObject);
        }
        // 正常退出和 break 都使用最后实际访问的元素；公开的循环主体始终写回该值。
        code.aload(1);
        path(code, target);
        code.fload(targetSlot);
        if (loop == ownedIteration) {
            Label ordinary = code.newLabel();
            Label done = code.newLabel();
            code.aload(0).getfield(COMPILED, "ownedTemps", CD_boolean).ifeq(ordinary)
                    .invokestatic(VALUES, "finishOwnedIteration", MethodTypeDesc.of(CD_void, RUNTIME, PATH, CD_float))
                    .goto_(done).labelBinding(ordinary)
                    .invokeinterface(RUNTIME, "setTempNumber", MethodTypeDesc.of(CD_void, PATH, CD_float)).labelBinding(done);
        } else {
            code.invokeinterface(RUNTIME, "setTempNumber", MethodTypeDesc.of(CD_void, PATH, CD_float));
        }
    }

    private void numericForEachBody(CodeBuilder code, Stmt.ForEach loop, int runtime, int array, int index, int length, int targetSlot) {
        Label start = code.newLabel();
        Label end = code.newLabel();
        Label next = code.newLabel();
        code.labelBinding(start).iload(index).iload(length).if_icmpge(end);
        code.aload(array).iload(index).invokestatic(VALUES, "getElementFloat", MethodTypeDesc.of(CD_float, CD_Object, CD_int))
                .fstore(targetSlot).iconst_1().istore(targetSlot + 1);
        breaks.push(end);
        continues.push(next);
        statement(code, loop.body(), runtime);
        continues.pop();
        breaks.pop();
        code.labelBinding(next).iinc(index, 1).goto_(start).labelBinding(end);
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
        if (batchOutput == null) {
            numericBody(code, expression, runtime);
            return;
        }
        int previous = batchOutput.previousLocal(expression);
        if (previous >= 0) {
            code.fload(previous);
            return;
        }
        numericBody(code, expression, runtime);
        int save = batchOutput.captureLocal(expression);
        if (save >= 0) {
            code.dup().fstore(save);
        }
    }

    private void numericBody(CodeBuilder code, Expr expression, int runtime) {
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
                    variableNumber(code, value, runtime, false);
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

    private record Operand(int slot, boolean number, boolean packed, int deferred) {
        private Operand(int slot, boolean number, boolean packed) {
            this(slot, number, packed, -1);
        }
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
        if (batchOutput != null && unwrapped instanceof Expr.VariableAccess variable) {
            int bits = nextSlot;
            nextSlot += 2;
            batchOutput.readVariableOperand(code, batchPlan.variableIndex(variable.name()));
            code.lstore(bits);
            return new Operand(bits, false, true);
        }
        if (unwrapped instanceof Expr.ContextSwitch(Expr arrowTarget, Expr arrowExpression)
                && arrowExpression instanceof Expr.VariableAccess(String fieldScope, String fieldName)
                && "variable".equals(fieldScope)) {
            return storeArrowOperand(code, arrowTarget, fieldName, runtime);
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
            Operand saved = loopOperands.get(variable);
            if (saved != null) {
                return saved;
            }
            int slot = nextSlot;
            nextSlot += 2;
            int objectLocal = "variable".equals(variable.scope()) ? scalarInputs.getOrDefault(variable.name(), -1) : localObject(variable);
            if (objectLocal >= 0) {
                code.aload(objectLocal).invokestatic(VALUES, "numericOperand", MethodTypeDesc.of(CD_long, CD_Object));
            } else {
                variableNumber(code, variable, runtime, true);
            }
            code.lstore(slot);
            return new Operand(slot, false, true);
        }
        return storeOperand(code, value, runtime);
    }

    private void variableNumber(CodeBuilder code, Expr.VariableAccess variable, int runtime, boolean packed) {
        String name = variable.name();
        boolean direct = !segmented && !foreignContext && runtime == 1 && options.stableVariableReads() && "variable".equals(variable.scope());
        ClassDesc result = packed ? CD_long : CD_float;
        if (linkedBody && direct) {
            loadLinkedVariable(code, name);
            String method = name.indexOf('.') >= 0 ? (packed ? "readNestedOperand" : "readNestedNumber") : (packed ? "readOperand" : "readNumber");
            code.invokevirtual(LINKED_VARIABLES, method, MethodTypeDesc.of(result, CD_int, PATH));
            return;
        }

        String method = switch (variable.scope()) {
            case "variable" -> packed ? "getVariableOperand" : "getVariableNumber";
            case "temp" -> packed ? "getTempOperand" : "getTempNumber";
            case "context" -> packed ? "getContextOperand" : "getContextNumber";
            default -> throw new MolangCompileException("Unknown variable scope: " + variable.scope());
        };
        code.aload("temp".equals(variable.scope()) ? 1 : runtime);
        path(code, name);
        code.invokeinterface(RUNTIME, method, MethodTypeDesc.of(result, PATH));
        if (direct) {
            linkableVariables.add(pathIndexes.getInt(name));
        }
    }

    private Operand storeArrowOperand(CodeBuilder code, Expr target, String field, int runtime) {
        int reference = storeExpression(code, target, runtime);
        int alternate = nextSlot++;
        int deferred = nextSlot++;
        int bits = nextSlot;
        nextSlot += 2;
        Label typed = code.newLabel();
        Label done = code.newLabel();
        switchesContext = true;

        code.aconst_null().astore(deferred).lconst_0().lstore(bits);
        code.aload(reference).invokestatic(VALUES, "asRuntime", MethodTypeDesc.of(RUNTIME, CD_Object)).astore(alternate);
        code.aload(alternate).ifnull(done);
        code.aload(1).invokeinterface(RUNTIME, "invalidateQueries", MTD_void);
        code.aload(alternate).invokestatic(MolangActorContext.class.describeConstable().orElseThrow(), "isPublishedView", MethodTypeDesc.of(CD_boolean, RUNTIME)).ifeq(typed);
        code.aload(alternate);
        path(code, field);
        code.invokeinterface(RUNTIME, "getVariableOperand", MethodTypeDesc.of(CD_long, PATH)).lstore(bits).goto_(done);
        code.labelBinding(typed).aload(alternate);
        path(code, field);
        code.invokeinterface(RUNTIME, "getVariableValue", MethodTypeDesc.of(CD_Object, PATH)).astore(deferred);
        code.aload(deferred).instanceOf(CD_String).ifeq(done).ldc(MolangValues.STRING_OPERAND).lstore(bits);
        code.labelBinding(done);
        return new Operand(bits, false, true, deferred);
    }

    private static void loadNumber(CodeBuilder code, Operand operand) {
        if (operand.deferred() >= 0) {
            // 自定义宿主的 getter 已执行，Number 转换可能调用外部代码，因此仍在原位置执行。
            Label fromBits = code.newLabel();
            Label done = code.newLabel();
            code.aload(operand.deferred()).ifnull(fromBits).aload(operand.deferred());
            toFloat(code);
            code.goto_(done).labelBinding(fromBits).lload(operand.slot()).l2i()
                    .invokestatic(FLOAT, "intBitsToFloat", MethodTypeDesc.of(CD_float, CD_int)).labelBinding(done);
        } else if (operand.packed()) {
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
        Expr.VariableAccess linkedVariable = equality ? equalityVariable(value, runtime) : null;
        if (linkedBody && linkedVariable != null) {
            boolean variableFirst = linkedVariable == value.left();
            Expr number = variableFirst ? value.right() : value.left();
            linkedEquality(code, linkedVariable.name(), number, variableFirst && !(number instanceof Expr.NumberLiteral), runtime);
            if ("!=".equals(operator)) {
                code.iconst_1().ixor();
            }
            code.i2f();
            return;
        }
        Operand left = equality ? storeOperand(code, value.left(), runtime) : storeArithmeticOperand(code, value.left(), runtime);
        Operand right = equality ? storeOperand(code, value.right(), runtime) : storeArithmeticOperand(code, value.right(), runtime);
        if (linkedVariable != null) {
            linkableVariables.add(pathIndexes.getInt(linkedVariable.name()));
        }
        if (equality) {
            if (left.number() && right.number()) {
                loadNumber(code, left);
                loadNumber(code, right);
                code.fcmpl();
                comparisonResult(code, operator);
                return;
            }
            objectEquality(code, left, right);
            comparisonResult(code, "==".equals(operator) ? "!=" : "==");
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

    private void objectEquality(CodeBuilder code, Operand left, Operand right) {
        if (left.number() || right.number()) {
            loadObject(code, left.number() ? right : left);
            loadNumber(code, left.number() ? left : right);
            code.invokestatic(VALUES, "equalNumber", MethodTypeDesc.of(CD_boolean, CD_Object, CD_float));
        } else {
            loadObject(code, left);
            loadObject(code, right);
            code.invokestatic(VALUES, "equal", MethodTypeDesc.of(CD_boolean, CD_Object, CD_Object));
        }
    }

    @Nullable
    private Expr.VariableAccess equalityVariable(Expr.Binary binary, int runtime) {
        if (!options.stableVariableReads() || segmented || foreignContext || runtime != 1 || batchOutput != null) {
            return null;
        }
        Expr other;
        if (numericOperand(binary.left())) {
            other = binary.right();
        } else if (numericOperand(binary.right())) {
            other = binary.left();
        } else {
            return null;
        }
        return other instanceof Expr.VariableAccess field && "variable".equals(field.scope()) && field.name().indexOf('.') < 0 && !scalarInputs.containsKey(field.name()) ? field : null;
    }

    private void loadLinkedVariable(CodeBuilder code, String name) {
        int index = pathIndexes.getInt(name);
        code.aload(0).aload(0).getfield(COMPILED, "v" + index, CD_int);
        path(code, name);
    }

    private void linkedEquality(CodeBuilder code, String name, Expr number, boolean snapshot, int runtime) {
        if (!snapshot) {
            int result = storeNumber(code, number, runtime);
            loadLinkedVariable(code, name);
            code.fload(result).invokevirtual(LINKED_VARIABLES, "equalsNumber", MethodTypeDesc.of(CD_boolean, CD_int, PATH, CD_float));
            return;
        }
        int bits = nextSlot;
        nextSlot += 2;
        int value = nextSlot++;
        Label captured = code.newLabel();
        // 先保存左操作数；右侧可以更改变量，外部 Number 的转换必须在右侧求值之后。
        code.aconst_null().astore(value);
        loadLinkedVariable(code, name);
        code.invokevirtual(LINKED_VARIABLES, "equalityBits", MethodTypeDesc.of(CD_long, CD_int, PATH))
                .lstore(bits).lload(bits).lconst_0().lcmp().ifge(captured);
        loadLinkedVariable(code, name);
        code.invokevirtual(LINKED_VARIABLES, "equalityValue", MethodTypeDesc.of(CD_Object, CD_int, PATH)).astore(value);
        code.labelBinding(captured);
        int result = storeNumber(code, number, runtime);
        code.lload(bits).aload(value).fload(result).invokestatic(VALUES, "equalSnapshot", MethodTypeDesc.of(CD_boolean, CD_long, CD_Object, CD_float));
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
        // small integer pow
        if ("pow".equals(call.function()) && call.args().size() >= 2 && call.args().get(1) instanceof Expr.NumberLiteral(float exponent) && (exponent == 3 || exponent == 4)) {
            numericExpression(code, call.args().getFirst(), runtime);
            code.ldc((int) exponent).invokestatic(MolangFunctions.class.describeConstable().orElseThrow(), "smallIntegerPower", MethodTypeDesc.of(CD_float, CD_float, CD_int));
            return;
        }

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
        if (batchOutput != null) {
            batchOutput.readQuery(code, batchPlan.queryIndex(call.name()));
            return;
        }
        if (MolangFunctions.builtinQuery(call.name())) {
            numericBuiltin(code, call, runtime);
        } else if (call.args().size() <= 3 && call.args().stream().allMatch(this::isNumeric)) {
            MolangQueryDefinition definition = options.querySchema().get(call.name());
            boolean directNumber = !segmented && !foreignContext && call.args().isEmpty() && definition != null
                    && definition.returnType() == MolangQueryDefinition.ValueType.NUMBER && !definition.nullable()
                    && definition.minArguments() == 0 && definition.isReadOnly()
                    && (definition.stability() == MolangQueryDefinition.Stability.NONE
                    || definition.stability() == MolangQueryDefinition.Stability.FRAME
                    || singleEvaluationRead && structFallback == null || propertyInputs);
            if (linkedBody && directNumber) {
                int slot = queryIndexes.getInt(call.name());
                if (!switchesContext) {
                    code.aload(0).aload(0).getfield(COMPILED, "q" + slot, NUMBER_CALLBACK);
                    code.invokevirtual(LINKED_QUERY, "number0", MethodTypeDesc.of(CD_float, NUMBER_CALLBACK));
                } else {
                    code.aload(0).getfield(COMPILED, "q" + slot, NUMBER_CALLBACK).aload(0).getfield(COMPILED, "owner", RUNTIME);
                    code.invokestatic(BINDING_PLAN, "invokeNumber0", MethodTypeDesc.of(CD_float, NUMBER_CALLBACK, RUNTIME));
                }
                return;
            }
            boolean unary = !segmented && !foreignContext && call.args().size() == 1 && definition != null && definition.isReadOnlyUnaryNumber() && !definition.nullable();
            if (unary && linkedBody) {
                int argument = storeNumber(code, call.args().getFirst(), runtime);
                int slot = queryIndexes.getInt(call.name());
                if (!switchesContext) {
                    code.aload(0).aload(0).getfield(COMPILED, "q" + slot, NUMBER_CALLBACK).fload(argument)
                            .invokevirtual(LINKED_QUERY, "number1", MethodTypeDesc.of(CD_float, NUMBER_CALLBACK, CD_float));
                } else {
                    code.aload(0).getfield(COMPILED, "q" + slot, NUMBER_CALLBACK).aload(0).getfield(COMPILED, "owner", RUNTIME)
                            .fload(argument).invokestatic(BINDING_PLAN, "invokeNumber1", MethodTypeDesc.of(CD_float, NUMBER_CALLBACK, RUNTIME, CD_float));
                }
                return;
            }
            int[] slots = new int[call.args().size()];
            for (int i = 0; i < slots.length; i++) {
                slots[i] = storeNumber(code, call.args().get(i), runtime);
            }
            code.aload(runtime);
            boolean indexed = slots.length == 0 && queryParameterType().equals(CD_String);
            queryRef(code, call, indexed);
            if (indexed) {
                code.invokestatic(QUERY_SITE, "queryNumber0", MethodTypeDesc.of(CD_float, RUNTIME, QUERY_SITE));
                return;
            }
            if (directNumber || unary) {
                linkableNumbers.add(queryIndexes.getInt(call.name()));
            }
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
                        objectEquality(code, first, next);
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
                    code.aload(result);
                    if (structFallback == null) {
                        code.invokestatic(VALUES, "copy", MethodTypeDesc.of(CD_Object, CD_Object));
                    }
                    code.astore(objectTemps.getInt(value.name()));
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
        loadSymbol(code, internPath(name), PATH);
    }

    private MolangPath internPath(String name) {
        int index = pathIndexes.getInt(name);
        if (index < 0) {
            index = paths.size();
            paths.add(new MolangPath(name, index));
            pathIndexes.put(name, index);
        }
        return paths.get(index);
    }

    private void queryRef(CodeBuilder code, Expr.QueryCall call) {
        queryRef(code, call, false);
    }

    private void queryRef(CodeBuilder code, Expr.QueryCall call, boolean indexed) {
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
        if (indexed) {
            loadSymbol(code, genericSites.computeIfAbsent(call.name(), MolangQuerySite::new), QUERY_SITE);
        } else if (queryParameterType().equals(CD_String)) {
            code.ldc(call.name());
        } else {
            loadSymbol(code, queries.get(index), QUERY_REF);
        }
    }

    private void loadSymbol(CodeBuilder code, Object symbol, ClassDesc type) {
        code.ldc(symbolConstants.computeIfAbsent(symbol, _ -> DynamicConstantDesc.ofNamed(BSM_CLASS_DATA_AT, DEFAULT_NAME, type, symbolConstants.size() + 1)));
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
        int input = "variable".equals(scope) ? scalarInputs.getOrDefault(name, -1) : -1;
        if (input >= 0) {
            code.aload(input);
            return;
        }
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
            boolean indexed = arguments.length <= 1 && queryParameterType().equals(CD_String);
            queryRef(code, call, indexed);
            for (int argument : arguments) {
                code.aload(argument);
            }
            if (indexed) {
                MethodTypeDesc descriptor = arguments.length == 0 ? MethodTypeDesc.of(CD_Object, RUNTIME, QUERY_SITE) : MethodTypeDesc.of(CD_Object, RUNTIME, QUERY_SITE, CD_Object);
                code.invokestatic(QUERY_SITE, "queryValue" + arguments.length, descriptor);
                return;
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
