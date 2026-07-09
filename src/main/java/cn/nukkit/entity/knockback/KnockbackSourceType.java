package cn.nukkit.entity.knockback;

/**
 * 击退来源分类，用于让默认战斗入口按来源读取 Profile 中的基础击退值。
 */
public enum KnockbackSourceType {
    GENERIC,
    ARROW,
    PROJECTILE,
    TRIDENT,
    FISHING_HOOK
}
