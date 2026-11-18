package cn.nukkit.molang.runtime;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * 初始化期冻结的布局、默认值与查询定义；每个实例仍拥有独立的可变值。
 */
public final class MolangRuntimeTemplate {
    private final Function<Object, MolangRuntime> factory;
    @Nullable
    private final Object defaultHost;

    MolangRuntimeTemplate(Function<Object, MolangRuntime> factory, @Nullable Object defaultHost) {
        this.factory = factory;
        this.defaultHost = defaultHost;
    }

    public MolangRuntime newRuntime() {
        return factory.apply(defaultHost);
    }

    public MolangRuntime newRuntime(@Nullable Object host) {
        return factory.apply(host);
    }
}
