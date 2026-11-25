package cn.nukkit.molang.compiler;

import cn.nukkit.molang.MolangCompileOptions;
import cn.nukkit.molang.MolangExpression;
import cn.nukkit.molang.parser.ast.Stmt;
import cn.nukkit.molang.runtime.MolangMath;
import cn.nukkit.molang.runtime.MolangRuntime;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * 所有表达式共用语义优化和字节码生成，避免数值与类型路径的优化规则发生漂移。
 */
public final class MolangCompiler {
    private final Stmt.Program program;
    private final MolangCompileOptions options;

    public MolangCompiler(Stmt.Program program) {
        this(program, MolangCompileOptions.DEFAULT);
    }

    public MolangCompiler(Stmt.Program program, MolangCompileOptions options) {
        MolangMath.initialize();
        this.options = Objects.requireNonNull(options, "options");
        SegmentedMolangCompiler.validateInput(Objects.requireNonNull(program, "program"));
        this.program = AstOptimizer.optimize(program, options);
    }

    /**
     * 取得可由批调度器管理求值作用域的程序体。分段、切换实体等计划保留自己的收尾逻辑。
     */
    @Nullable
    public static MolangExpression batchBody(MolangExpression compiled) {
        return TypedMolangCompiler.batchBody(compiled);
    }

    /**
     * 为单个实体建立批执行对象。作用域由调度器接管时返回程序体，否则保留原程序的收尾操作。
     */
    @Nullable
    public static MolangExpression linkBatchBody(MolangExpression program, MolangRuntime view, boolean managedScope) {
        return TypedMolangCompiler.linkBatchBody(program, view, managedScope);
    }

    public MolangExpression compile() {
        return SegmentedMolangCompiler.compile(program, options);
    }
}
