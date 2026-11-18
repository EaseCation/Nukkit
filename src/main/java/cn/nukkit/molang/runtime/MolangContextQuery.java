package cn.nukkit.molang.runtime;

import javax.annotation.Nullable;

/**
 * 可在 runtime 模板之间共享的查询函数，通过当前实例取得实体或世界宿主。
 */
@FunctionalInterface
public interface MolangContextQuery {
    @Nullable
    Object query(MolangRuntime runtime, Object... arguments);
}
