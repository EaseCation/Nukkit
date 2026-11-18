package cn.nukkit.molang.compiler;

import cn.nukkit.molang.MolangCompileOptions;
import cn.nukkit.molang.MolangExpression;
import cn.nukkit.molang.parser.ast.Stmt;
import cn.nukkit.molang.runtime.MolangMath;

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

    public MolangExpression compile() {
        return SegmentedMolangCompiler.compile(program, options);
    }
}
