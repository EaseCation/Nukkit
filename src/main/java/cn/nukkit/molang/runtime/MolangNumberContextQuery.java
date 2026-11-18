package cn.nukkit.molang.runtime;

/**
 * 显式数值返回的宿主查询；不经过 Object 返回边界，也不能返回缺失值。
 */
@FunctionalInterface
public interface MolangNumberContextQuery {
    float query(MolangRuntime runtime, Object... arguments);
}
