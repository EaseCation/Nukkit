package cn.nukkit.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.entity.knockback.KnockbackManager;
import cn.nukkit.entity.knockback.KnockbackProfile;
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
        this(damager, entity, cause, damage, profileBaseH(entity), profileBaseV(entity));
    }

    public EntityDamageByEntityEvent(Entity damager, Entity entity, DamageCause cause, Map<DamageModifier, Float> modifiers) {
        this(damager, entity, cause, modifiers, profileBaseH(entity), profileBaseV(entity));
    }

    public EntityDamageByEntityEvent(Entity damager, Entity entity, DamageCause cause, float damage, float knockBackH, float knockBackV) {
        super(entity, cause, damage);
        this.damager = damager;
        this.knockbackProfile = createPerHitProfile(entity, knockBackH, knockBackV);
        this.addAttackerModifiers(damager);
    }

    public EntityDamageByEntityEvent(Entity damager, Entity entity, DamageCause cause, Map<DamageModifier, Float> modifiers, float knockBackH, float knockBackV) {
        this(damager, entity, cause, modifiers, knockBackH, knockBackV, Enchantment.EMPTY);
    }

    public EntityDamageByEntityEvent(Entity damager, Entity entity, DamageCause cause, Map<DamageModifier, Float> modifiers, float knockBackH, float knockBackV, Enchantment[] enchantments) {
        super(entity, cause, modifiers);
        this.damager = damager;
        this.knockbackProfile = createPerHitProfile(entity, knockBackH, knockBackV);
        this.enchantments = enchantments;
        if (cause == DamageCause.ENTITY_ATTACK) {
            this.addAttackerModifiers(damager);
        }
    }

    /**
     * 从受害者实体的 Profile 创建 per-hit 副本，并设置传入的基础击退值
     */
    private static KnockbackProfile createPerHitProfile(Entity entity, float knockBackH, float knockBackV) {
        KnockbackProfile source = entity instanceof EntityLiving living
                ? living.getKnockbackProfile() : KnockbackManager.get().getDefaultProfile();
        return source.copy().setBaseH(knockBackH).setBaseV(knockBackV);
    }

    private static float profileBaseH(Entity entity) {
        return entity instanceof EntityLiving living
                ? living.getKnockbackProfile().getBaseH() : GLOBAL_KNOCKBACK_H;
    }

    private static float profileBaseV(Entity entity) {
        return entity instanceof EntityLiving living
                ? living.getKnockbackProfile().getBaseV() : GLOBAL_KNOCKBACK_V;
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
     * 获取 per-hit 击退 Profile（实体 Profile 的副本，可在事件处理中修改算法参数）
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
    public void setKnockBackH(float knockBackH) {
        knockbackProfile.setBaseH(knockBackH).setEnchantLevel(0);
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
    public void setKnockBackV(float knockBackV) {
        knockbackProfile.setBaseV(knockBackV).setEnchantLevel(0);
    }

    public void setKnockBack(float knockBack) {
        this.setKnockBack(knockBack, knockBack);
    }

    /**
     * 设置击退总值（清零附魔等级，使 effectiveBase = 传入值）
     */
    public void setKnockBack(float knockBackH, float knockBackV) {
        knockbackProfile.setBaseH(knockBackH).setBaseV(knockBackV).setEnchantLevel(0);
    }

    public void clearKnockBack() {
        this.setKnockBack(0, 0);
    }

    public boolean isDefaultKnockback() {
        return knockbackProfile.getEffectiveBaseH() == GLOBAL_KNOCKBACK_H
                && knockbackProfile.getEffectiveBaseV() == GLOBAL_KNOCKBACK_V;
    }

    public Enchantment[] getWeaponEnchantments() {
        return enchantments;
    }
}
