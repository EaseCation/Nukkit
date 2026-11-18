package cn.nukkit.molang;

import cn.nukkit.molang.runtime.MolangRuntime;

import java.util.List;

/**
 * 一帧内按声明顺序执行的一组已绑定表达式，输出写入调用方复用的数组。
 */
public final class MolangBatch {
    private final MolangRuntime runtime;
    private final MolangBoundExpression[] expressions;

    public MolangBatch(MolangRuntime runtime, List<MolangExpression> expressions) {
        this.runtime = runtime;
        this.expressions = new MolangBoundExpression[expressions.size()];
        for (int i = 0; i < this.expressions.length; i++) {
            this.expressions[i] = expressions.get(i).bind(runtime);
        }
    }

    public int size() {
        return expressions.length;
    }

    public void evaluate(float[] output) {
        if (output.length < expressions.length) {
            throw new IllegalArgumentException("Molang batch output is too small");
        }
        for (int i = 0; i < expressions.length; i++) {
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
