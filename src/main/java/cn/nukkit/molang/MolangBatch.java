package cn.nukkit.molang;

import cn.nukkit.molang.runtime.MolangRuntime;
import cn.nukkit.molang.compiler.MolangBatchCompiler;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 一帧内按声明顺序执行的一组已绑定表达式，输出写入调用方复用的数组。
 */
public final class MolangBatch {
    private final MolangRuntime runtime;
    private final int size;
    @Nullable
    private final MolangBoundExpression[] expressions;
    @Nullable
    private final MolangBatchCompiler.Bound dispatch;

    public MolangBatch(MolangRuntime runtime, List<MolangExpression> expressions) {
        this.runtime = runtime;
        this.size = expressions.size();
        this.dispatch = null;
        this.expressions = new MolangBoundExpression[size];
        for (int i = 0; i < this.expressions.length; i++) {
            this.expressions[i] = expressions.get(i).bind(runtime);
        }
    }

    MolangBatch(MolangRuntime runtime, int size, MolangBatchCompiler.Bound dispatch) {
        this.runtime = runtime;
        this.size = size;
        this.dispatch = dispatch;
        this.expressions = null;
    }

    public int size() {
        return size;
    }

    public void evaluate(float[] output) {
        if (output.length < size) {
            throw new IllegalArgumentException("Molang batch output is too small");
        }
        if (dispatch != null) {
            dispatch.evaluate(output);
            return;
        }
        for (int i = 0; i < size; i++) {
            output[i] = expressions[i].evaluate();
        }
    }

    public void evaluateFrame(long frameId, float[] output) {
        runtime.beginFrame(frameId);
        try {
            evaluate(output);
        } finally {
            runtime.endFrame();
        }
    }
}
