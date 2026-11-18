package cn.nukkit.molang.runtime;

import javax.annotation.Nullable;

/**
 * 可失效的实体引用。解析得到的运行时只暴露宿主允许跨实体读取的内容。
 */
@FunctionalInterface
public interface MolangEntityReference {
    @Nullable
    MolangRuntime resolve();
}
