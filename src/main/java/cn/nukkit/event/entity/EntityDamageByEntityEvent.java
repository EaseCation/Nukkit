package cn.nukkit.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.knockback.KnockbackManager;
import cn.nukkit.entity.knockback.KnockbackProfile;
import cn.nukkit.entity.knockback.KnockbackSourceType;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.potion.Effect;

import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EntityDamageByEntityEvent extends EntityDamageEvent {

    public static final float GLOBAL_KNOCKBACK_H = 0.29f;
    public static final float GLOBAL_KNOCKBACK_V = 0.29f;

    private final Entity damager;

    private final KnockbackProfile knockbackProfile;

    private Enchantment[] enchantments;

    public EntityDamageByEntityEvent(Entity damager, Entity entity, DamageCause cause, float damage) {
        this(damager, entity, cause, damage, KnockbackSourceType.GENERIC);
    }

    public EntityDamageByEntityEvent(Entity damager, Entity entity, DamageCause cause, float damage, KnockbackSourceType sourceType) {
        this(damager, entity, cause, damage, profileBaseH(damager, sourceType), profileBaseV(damager, sourceType));
    }

    public EntityDamageByEntityEvent(Entity damager, Entity entity, DamageCause cause, Map<DamageModifier, Float> modifiers) {
        this(damager, entity, cause, modifiers, KnockbackSourceType.GENERIC);
    }

    public EntityDamageByEntityEvent(Entity damager, Entity entity, DamageCause cause, Map<DamageModifier, Float> modifiers, KnockbackSourceType sourceType) {
        this(damager, entity, cause, modifiers, profileBaseH(damager, sourceType), profileBaseV(damager, sourceType));
    }

    public EntityDamageByEntityEvent(Entity damager, Entity entity, DamageCause cause, float damage, float knockBackH, float knockBackV) {
        super(entity, cause, damage);
        this.damager = damager;
        this.knockbackProfile = createPerHitProfile(damager, knockBackH, knockBackV);
        this.addAttackerModifiers(damager);
    }

    public EntityDamageByEntityEvent(Entity damager, Entity entity, DamageCause cause, Map<DamageModifier, Float> modifiers, float knockBackH, float knockBackV) {
        this(damager, entity, cause, modifiers, knockBackH, knockBackV, Enchantment.EMPTY);
    }

    public EntityDamageByEntityEvent(Entity damager, Entity entity, DamageCause cause, Map<DamageModifier, Float> modifiers, float knockBackH, float knockBackV, Enchantment[] enchantments) {
        super(entity, cause, modifiers);
        this.damager = damager;
        this.knockbackProfile = createPerHitProfile(damager, knockBackH, knockBackV);
        this.enchantments = enchantments;
        if (cause == DamageCause.ENTITY_ATTACK) {
            this.addAttackerModifiers(damager);
        }
    }

    /**
     * 从攻击者（damager）的 Profile 创建 per-hit 副本，并设置传入的基础击退值
     */
    private static KnockbackProfile createPerHitProfile(Entity damager, float knockBackH, float knockBackV) {
        KnockbackProfile source = damager instanceof EntityLiving living
                ? living.getKnockbackProfile() : KnockbackManager.get().getDefaultProfile();
        return source.copy().setBaseH(knockBackH).setBaseV(knockBackV);
    }

    private static float profileBaseH(Entity damager) {
        return profileBaseH(damager, KnockbackSourceType.GENERIC);
    }

    private static float profileBaseH(Entity damager, KnockbackSourceType sourceType) {
        return damager instanceof EntityLiving living
                ? living.getKnockbackProfile().getBaseH(sourceType)
                : KnockbackManager.get().getDefaultProfile().getBaseH(sourceType);
    }

    private static float profileBaseV(Entity damager) {
        return profileBaseV(damager, KnockbackSourceType.GENERIC);
    }

    private static float profileBaseV(Entity damager, KnockbackSourceType sourceType) {
        return damager instanceof EntityLiving living
                ? living.getKnockbackProfile().getBaseV(sourceType)
                : KnockbackManager.get().getDefaultProfile().getBaseV(sourceType);
    }

    protected void addAttackerModifiers(Entity damager) {
        Effect strength = damager.getEffect(Effect.STRENGTH);
        if (strength != null) {
            this.setDamage(this.getDamage(DamageModifier.BASE) * 0.3f * (strength.getAmplifier() + 1), DamageModifier.STRENGTH);
        }

        Effect weakness = damager.getEffect(Effect.WEAKNESS);
        if (weakness != null) {
            this.setDamage(-(this.getDamage(DamageModifier.BASE) * 0.2f * (weakness.getAmplifier() + 1)), DamageModifier.WEAKNESS);
        }
    }

    public Entity getDamager() {
        return damager;
    }

    /**
     * 获取 per-hit 击退 Profile（攻击者 Profile 的副本，可在事件处理中修改算法参数）
     */
    public KnockbackProfile getKnockbackProfile() {
        return knockbackProfile;
    }

    public boolean hasKnockBack() {
        return knockbackProfile.getEffectiveBaseH() != 0 || knockbackProfile.getEffectiveBaseV() != 0;
    }

    /**
     * 返回有效水平击退值（base + enchantLevel * enchantBonus）
     */
    public float getKnockBackH() {
        return knockbackProfile.getEffectiveBaseH();
    }

    /**
     * 设置水平击退总值（清零附魔等级，使 effectiveBase = knockBackH）
     */
    public void setKnockbackBaseH(float knockBackH) {
        knockbackProfile.setBaseH(knockBackH).setEnchantLevel(0);
    }

    /**
     * 旧击退入口，仅作为 Profile 兼容层保留。新代码请使用 {@link #setKnockbackBaseH(float)}。
     */
    @Deprecated
    public void setKnockBackH(float knockBackH) {
        this.setKnockbackBaseH(knockBackH);
    }

    /**
     * 返回有效垂直击退值（base + enchantLevel * enchantBonus）
     */
    public float getKnockBackV() {
        return knockbackProfile.getEffectiveBaseV();
    }

    /**
     * 设置垂直击退总值（清零附魔等级，使 effectiveBase = knockBackV）
     */
    public void setKnockbackBaseV(float knockBackV) {
        knockbackProfile.setBaseV(knockBackV).setEnchantLevel(0);
    }

    /**
     * 旧击退入口，仅作为 Profile 兼容层保留。新代码请使用 {@link #setKnockbackBaseV(float)}。
     */
    @Deprecated
    public void setKnockBackV(float knockBackV) {
        this.setKnockbackBaseV(knockBackV);
    }

    public void setKnockbackBase(float knockBack) {
        this.setKnockbackBase(knockBack, knockBack);
    }

    /**
     * 旧击退入口，仅作为 Profile 兼容层保留。新代码请使用 {@link #setKnockbackBase(float)}。
     */
    @Deprecated
    public void setKnockBack(float knockBack) {
        this.setKnockbackBase(knockBack, knockBack);
    }

    /**
     * 设置击退总值（清零附魔等级，使 effectiveBase = 传入值）
     */
    public void setKnockbackBase(float knockBackH, float knockBackV) {
        knockbackProfile.setBaseH(knockBackH).setBaseV(knockBackV).setEnchantLevel(0);
    }

    /**
     * 旧击退入口，仅作为 Profile 兼容层保留。新代码请使用 {@link #setKnockbackBase(float, float)}。
     */
    @Deprecated
    public void setKnockBack(float knockBackH, float knockBackV) {
        this.setKnockbackBase(knockBackH, knockBackV);
    }

    public void clearKnockback() {
        this.setKnockbackBase(0, 0);
    }

    /**
     * 旧击退入口，仅作为 Profile 兼容层保留。新代码请使用 {@link #clearKnockback()}。
     */
    @Deprecated
    public void clearKnockBack() {
        this.clearKnockback();
    }

    /**
     * 复制另一个事件的 per-hit Profile，适用于转发伤害事件时保持击退语义。
     */
    public void copyKnockbackProfileFrom(EntityDamageByEntityEvent source) {
        this.knockbackProfile.copyFrom(source.getKnockbackProfile());
    }

    public boolean isDefaultKnockback() {
        return knockbackProfile.getEffectiveBaseH() == GLOBAL_KNOCKBACK_H
                && knockbackProfile.getEffectiveBaseV() == GLOBAL_KNOCKBACK_V;
    }

    public Enchantment[] getWeaponEnchantments() {
        return enchantments;
    }
}
