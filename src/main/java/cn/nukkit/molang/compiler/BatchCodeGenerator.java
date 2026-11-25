package cn.nukkit.molang.compiler;

import cn.nukkit.molang.MolangExpression;
import cn.nukkit.molang.parser.ast.Expr;
import cn.nukkit.molang.runtime.*;

import java.lang.classfile.ClassBuilder;
import java.lang.classfile.ClassFile;
import java.lang.classfile.CodeBuilder;
import java.lang.classfile.Label;
import java.lang.constant.ClassDesc;
import java.lang.constant.DynamicConstantDesc;
import java.lang.constant.MethodTypeDesc;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.List;

import static java.lang.classfile.ClassFile.*;
import static java.lang.constant.ConstantDescs.*;

/**
 * 为同一组程序和绑定形态生成一个短批次类。类数据只引用无宿主程序和路径。
 */
final class BatchCodeGenerator {
    private static final ClassDesc GENERATED = ClassDesc.of(MolangBatchCompiler.Bound.class.getPackageName(), "CompiledBatch");
    private static final ClassDesc DISPATCH = MolangBatchCompiler.Bound.class.describeConstable().orElseThrow();
    private static final ClassDesc PROGRAM = MolangExpression.class.describeConstable().orElseThrow();
    private static final ClassDesc RUNTIME = MolangRuntime.class.describeConstable().orElseThrow();
    private static final ClassDesc RUNTIME_BUILDER = MolangRuntimeBuilder.class.describeConstable().orElseThrow();
    private static final ClassDesc FUNCTION = MolangNumberContextQuery.class.describeConstable().orElseThrow();
    private static final ClassDesc BINDING = MolangBindingPlan.class.describeConstable().orElseThrow();
    private static final ClassDesc VARIABLES = MolangBindingPlan.LinkedVariables.class.describeConstable().orElseThrow();
    private static final ClassDesc PATH = MolangPath.class.describeConstable().orElseThrow();
    private static final MethodTypeDesc INIT = MethodTypeDesc.of(CD_void, PROGRAM.arrayType(), RUNTIME.arrayType(), FUNCTION.arrayType().arrayType(), CD_int.arrayType().arrayType(), RUNTIME);
    private static final MethodTypeDesc EVALUATE_BATCH = MethodTypeDesc.of(CD_void, CD_float.arrayType());
    private static final MethodTypeDesc EVALUATE = MethodTypeDesc.of(CD_float, RUNTIME);
    private static final MethodTypeDesc READ_OPERAND = MethodTypeDesc.of(CD_long, CD_int, PATH);
    private static final MethodTypeDesc READ_QUERY = MethodTypeDesc.of(CD_float, FUNCTION, RUNTIME);

    private final MolangBatchCompiler.Plan plan;
    private final List<Object> data;
    private final List<DynamicConstantDesc<MolangExpression>> programs;
    private final List<List<DynamicConstantDesc<MolangPath>>> paths;
    private final boolean usesVariables;
    private final boolean usesOwner;

    BatchCodeGenerator(MolangBatchCompiler.Plan plan) {
        this.plan = plan;
        usesOwner = plan.scopeMask() != 0 || plan.numberMask() != 0 || plan.ownerMask() != 0;
        data = new ArrayList<>(plan.programs());
        int size = plan.programs().size();
        programs = new ArrayList<>(size);
        paths = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            programs.add(DynamicConstantDesc.ofNamed(BSM_CLASS_DATA_AT, DEFAULT_NAME, PROGRAM, i));
            BatchNumberPlan number = plan.number(i);
            if (number == null || number.pathCount() == 0) {
                paths.add(List.of());
                continue;
            }
            List<DynamicConstantDesc<MolangPath>> positionPaths = new ArrayList<>(number.pathCount());
            for (int j = 0; j < number.pathCount(); j++) {
                positionPaths.add(DynamicConstantDesc.ofNamed(BSM_CLASS_DATA_AT, DEFAULT_NAME, PATH, data.size()));
                data.add(number.path(j));
            }
            paths.add(positionPaths);
        }
        usesVariables = data.size() != size;
    }

    MolangBatchCompiler.Factory compile() {
        byte[] bytes = ClassFile.of().build(GENERATED, builder -> {
            builder.withFlags(ACC_FINAL | ACC_SYNTHETIC);
            builder.withInterfaceSymbols(DISPATCH);
            if (usesVariables) {
                builder.withSuperclass(VARIABLES);
            }
            if (usesOwner) {
                builder.withField("owner", RUNTIME, ACC_PRIVATE | ACC_FINAL);
            }
            if (plan.hasHostFields()) {
                fields(builder);
            } else {
                builder.withField("views", RUNTIME.arrayType(), ACC_PRIVATE | ACC_FINAL);
            }
            builder.withMethodBody(INIT_NAME, INIT, ACC_PRIVATE, this::initialize);
            builder.withMethodBody("evaluate", EVALUATE_BATCH, ACC_PUBLIC, this::evaluate);
            builder.withMethodBody(CLASS_INIT_NAME, MTD_void, ACC_STATIC, code -> {
                for (DynamicConstantDesc<MolangExpression> program : programs) {
                    code.ldc(program).pop();
                }
                for (List<DynamicConstantDesc<MolangPath>> position : paths) {
                    for (DynamicConstantDesc<MolangPath> path : position) {
                        code.ldc(path).pop();
                    }
                }
                code.return_();
            });
        });
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup().defineHiddenClassWithClassData(bytes, List.copyOf(data), true);
            MethodType init = MethodType.methodType(void.class, MolangExpression[].class, MolangRuntime[].class, MolangNumberContextQuery[][].class, int[][].class, MolangRuntime.class);
            MethodHandle constructor = lookup.findConstructor(lookup.lookupClass(), init).asType(init.changeReturnType(MolangBatchCompiler.Bound.class));
            return new MolangBatchCompiler.Factory(plan, (bindings, views, runtime) -> {
                MolangExpression[] targets = bindings == null ? null : bindings.targets();
                MolangNumberContextQuery[][] queries = bindings == null ? null : bindings.queries();
                int[][] slots = bindings == null ? null : bindings.variableSlots();
                return (MolangBatchCompiler.Bound) constructor.invokeExact(targets, views, queries, slots, runtime);
            });
        } catch (Throwable error) {
            throw new MolangCompileException("Failed to create Molang batch dispatcher", error);
        }
    }

    private void fields(ClassBuilder builder) {
        for (int i = 0; i < programs.size(); i++) {
            if (plan.linked(i)) {
                builder.withField("p" + i, PROGRAM, ACC_PRIVATE | ACC_FINAL);
            }
            BatchNumberPlan number = plan.number(i);
            if (number == null) {
                if (!plan.usesOwner(i)) {
                    builder.withField("v" + i, RUNTIME, ACC_PRIVATE | ACC_FINAL);
                }
                continue;
            }
            for (int j = 0; j < number.queryCount(); j++) {
                builder.withField(query(i, j), FUNCTION, ACC_PRIVATE | ACC_FINAL);
            }
            for (int j = 0; j < number.pathCount(); j++) {
                builder.withField(slot(i, j), CD_int, ACC_PRIVATE | ACC_FINAL);
            }
        }
    }

    private void initialize(CodeBuilder code) {
        if (usesVariables) {
            code.aload(0).aload(5).invokespecial(VARIABLES, INIT_NAME, MethodTypeDesc.of(CD_void, RUNTIME));
        } else {
            code.aload(0).invokespecial(CD_Object, INIT_NAME, MTD_void);
        }
        if (usesOwner) {
            code.aload(0).aload(5).putfield(GENERATED, "owner", RUNTIME);
        }
        if (!plan.hasHostFields()) {
            code.aload(0).aload(2).putfield(GENERATED, "views", RUNTIME.arrayType()).return_();
            return;
        }
        for (int i = 0; i < programs.size(); i++) {
            if (plan.linked(i)) {
                code.aload(0).aload(1).ldc(i).aaload().putfield(GENERATED, "p" + i, PROGRAM);
            }
            BatchNumberPlan number = plan.number(i);
            if (number == null) {
                if (!plan.usesOwner(i)) {
                    code.aload(0).aload(2).ldc(i).aaload().putfield(GENERATED, "v" + i, RUNTIME);
                }
                continue;
            }
            for (int j = 0; j < number.queryCount(); j++) {
                code.aload(0).aload(3).ldc(i).aaload().ldc(j).aaload().putfield(GENERATED, query(i, j), FUNCTION);
            }
            for (int j = 0; j < number.pathCount(); j++) {
                code.aload(0).aload(4).ldc(i).aaload().ldc(j).iaload().putfield(GENERATED, slot(i, j), CD_int);
            }
        }
        code.return_();
    }

    private void evaluate(CodeBuilder code) {
        Label failed = code.newLabel();
        if (usesOwner) {
            code.aload(0).getfield(GENERATED, "owner", RUNTIME).astore(3);
        }
        Label precedingStore = null;
        BatchSubexpressions shared = new BatchSubexpressions(plan);
        for (int i = 0; i < programs.size(); i++) {
            boolean scoped = plan.scopes(i);
            BatchNumberPlan number = plan.number(i);
            shared.begin(i, number);
            if (scoped && !plan.scopes(i - 1)) {
                code.aload(3).invokeinterface(RUNTIME, "beginEvaluation", MTD_void);
            }
            Label from = precedingStore != null ? precedingStore : code.newBoundLabel();
            precedingStore = null;
            Label to = code.newLabel();
            code.aload(1).ldc(i);
            if (number != null) {
                number.emit(code, new Output(i, shared));
            } else {
                call(code, i);
            }
            code.labelBinding(to);
            if (scoped) {
                code.exceptionCatchAll(from, to, failed);
                code.aload(3);
                if (plan.scopes(i + 1)) {
                    code.invokestatic(RUNTIME_BUILDER, "nextEvaluation", MethodTypeDesc.of(CD_void, RUNTIME));
                    // 下一项的作用域已打开，写入本项输出时的异常也由下一段处理。
                    precedingStore = code.newBoundLabel();
                } else {
                    code.invokeinterface(RUNTIME, "endEvaluation", MTD_void);
                }
            }
            // 本项作用域正常退出后才更新输出。
            code.fastore();
        }
        code.return_();
        if (plan.scopeMask() != 0) {
            code.labelBinding(failed).aload(3).invokeinterface(RUNTIME, "endEvaluation", MTD_void).athrow();
        }
    }

    private void call(CodeBuilder code, int i) {
        if (plan.linked(i)) {
            code.aload(0).getfield(GENERATED, "p" + i, PROGRAM);
        } else {
            code.ldc(programs.get(i));
        }
        if (plan.usesOwner(i)) {
            code.aload(3);
        } else if (plan.hasHostFields()) {
            code.aload(0).getfield(GENERATED, "v" + i, RUNTIME);
        } else {
            code.aload(0).getfield(GENERATED, "views", RUNTIME.arrayType()).ldc(i).aaload();
        }
        code.invokeinterface(PROGRAM, "evaluate", EVALUATE);
    }

    private static String query(int position, int index) {
        return "q" + position + "_" + index;
    }

    private static String slot(int position, int index) {
        return "s" + position + "_" + index;
    }

    private final class Output implements BatchNumberPlan.Output {
        private final int position;
        private final BatchSubexpressions shared;

        private Output(int position, BatchSubexpressions shared) {
            this.position = position;
            this.shared = shared;
        }

        @Override
        public void readQuery(CodeBuilder code, int index) {
            code.aload(0).getfield(GENERATED, query(position, index), FUNCTION).aload(3).invokestatic(BINDING, "invokeNumber0", READ_QUERY);
        }

        @Override
        public void readVariableOperand(CodeBuilder code, int index) {
            code.aload(0).aload(0).getfield(GENERATED, slot(position, index), CD_int)
                    .ldc(paths.get(position).get(index)).invokevirtual(VARIABLES, "readOperand", READ_OPERAND);
        }

        @Override
        public int previousLocal(Expr expression) {
            return shared.previousLocal(expression);
        }

        @Override
        public int captureLocal(Expr expression) {
            return shared.captureLocal(expression);
        }
    }
}
