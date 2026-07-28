package cn.nukkit.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.knockback.KnockbackAlgorithm;
import cn.nukkit.entity.knockback.KnockbackManager;
import cn.nukkit.entity.knockback.KnockbackProfile;
import cn.nukkit.entity.knockback.KnockbackSource;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.potion.Effect;

import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EntityDamageByEntityEvent extends EntityDamageEvent {

    private enum KnockbackOverrideMode {
        NONE,
        DISABLED,
        LEGACY_ABSOLUTE
    }

    public static final float GLOBAL_KNOCKBACK_H = 0.29f;
    public static final float GLOBAL_KNOCKBACK_V = 0.29f;

    private final Entity damager;

    private final KnockbackProfile knockbackProfile;

    private KnockbackSource knockbackSource;

    private KnockbackOverrideMode knockbackOverrideMode;

    private boolean attackerSprinting;

    private float attackerYaw;

    private boolean hasKnockbackSourceMotion;

    private double knockbackSourceMotionX;

    private double knockbackSourceMotionZ;

    private Enchantment[] enchantments;

    public EntityDamageByEntityEvent(Entity damager, Entity entity, DamageCause cause, float damage) {
        this(damager, entity, cause, damage, KnockbackSource.GENERIC);
    }

    public EntityDamageByEntityEvent(Entity damager, Entity entity, DamageCause cause, float damage,
                                     KnockbackSource knockbackSource) {
        super(entity, cause, damage);
        this.damager = damager;
        this.knockbackProfile = createPerHitProfile(damager);
        this.knockbackSource = knockbackSource;
        this.knockbackOverrideMode = KnockbackOverrideMode.NONE;
        if (knockbackSource == KnockbackSource.MELEE) {
            this.captureAttackerState(damager);
        }
        this.addAttackerModifiers(damager);
    }

    public EntityDamageByEntityEvent(Entity damager, Entity entity, DamageCause cause, Map<DamageModifier, Float> modifiers) {
        this(damager, entity, cause, modifiers, Enchantment.EMPTY, KnockbackSource.GENERIC);
    }

    public EntityDamageByEntityEvent(Entity damager, Entity entity, DamageCause cause,
                                     Map<DamageModifier, Float> modifiers, Enchantment[] enchantments,
                                     KnockbackSource knockbackSource) {
        super(entity, cause, modifiers);
        this.damager = damager;
        this.knockbackProfile = createPerHitProfile(damager);
        this.knockbackSource = knockbackSource;
        this.knockbackOverrideMode = KnockbackOverrideMode.NONE;
        this.enchantments = enchantments;
        if (knockbackSource == KnockbackSource.MELEE) {
            this.captureAttackerState(damager);
        }
        if (cause == DamageCause.ENTITY_ATTACK) {
            this.addAttackerModifiers(damager);
        }
    }

    public EntityDamageByEntityEvent(Entity damager, Entity entity, DamageCause cause, float damage, float knockBackH, float knockBackV) {
        super(entity, cause, damage);
        this.damager = damager;
        this.knockbackProfile = createPerHitProfile(damager, knockBackH, knockBackV);
        this.knockbackSource = KnockbackSource.GENERIC;
        this.knockbackOverrideMode = KnockbackOverrideMode.LEGACY_ABSOLUTE;
        this.addAttackerModifiers(damager);
    }

    public EntityDamageByEntityEvent(Entity damager, Entity entity, DamageCause cause, Map<DamageModifier, Float> modifiers, float knockBackH, float knockBackV) {
        this(damager, entity, cause, modifiers, knockBackH, knockBackV, Enchantment.EMPTY);
    }

    public EntityDamageByEntityEvent(Entity damager, Entity entity, DamageCause cause, Map<DamageModifier, Float> modifiers, float knockBackH, float knockBackV, Enchantment[] enchantments) {
        super(entity, cause, modifiers);
        this.damager = damager;
        this.knockbackProfile = createPerHitProfile(damager, knockBackH, knockBackV);
        this.knockbackSource = KnockbackSource.GENERIC;
        this.knockbackOverrideMode = KnockbackOverrideMode.LEGACY_ABSOLUTE;
        this.enchantments = enchantments;
        if (cause == DamageCause.ENTITY_ATTACK) {
            this.addAttackerModifiers(damager);
        }
    }

    private void captureAttackerState(Entity damager) {
        this.attackerSprinting = damager.isSprinting();
        this.attackerYaw = (float) damager.getYaw();
    }

    /**
     * 从攻击者（damager）的 Profile 创建 per-hit 副本。
     */
    private static KnockbackProfile createPerHitProfile(Entity damager) {
        KnockbackProfile source = damager instanceof EntityLiving living
                ? living.getKnockbackProfile() : KnockbackManager.get().getDefaultProfile();
        KnockbackProfile profile = source.copy();
        return damager instanceof EntityLiving ? profile
                : profile.setBaseH(GLOBAL_KNOCKBACK_H).setBaseV(GLOBAL_KNOCKBACK_V);
    }

    private static KnockbackProfile createPerHitProfile(Entity damager, float knockBackH, float knockBackV) {
        return createPerHitProfile(damager).setBaseH(knockBackH).setBaseV(knockBackV);
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

    public KnockbackSource getKnockbackSource() {
        return knockbackSource;
    }

    public boolean wasAttackerSprinting() {
        return attackerSprinting;
    }

    public float getAttackerYaw() {
        return attackerYaw;
    }

    protected void captureKnockbackSourceMotion(Entity sourceEntity) {
        this.hasKnockbackSourceMotion = true;
        this.knockbackSourceMotionX = sourceEntity.motionX;
        this.knockbackSourceMotionZ = sourceEntity.motionZ;
    }

    public boolean hasKnockbackSourceMotion() {
        return hasKnockbackSourceMotion;
    }

    public double getKnockbackSourceMotionX() {
        return knockbackSourceMotionX;
    }

    public double getKnockbackSourceMotionZ() {
        return knockbackSourceMotionZ;
    }

    public boolean usesJavaEdition1_8Knockback() {
        return knockbackOverrideMode == KnockbackOverrideMode.NONE
                && knockbackSource != KnockbackSource.GENERIC
                && knockbackProfile.getAlgorithm() == KnockbackAlgorithm.JAVA_EDITION_1_8;
    }

    public boolean willApplyKnockback() {
        if (knockbackOverrideMode == KnockbackOverrideMode.DISABLED) {
            return false;
        }
        if (usesJavaEdition1_8Knockback()) {
            return true;
        }
        return knockbackProfile.getEffectiveBaseH() != 0 || knockbackProfile.getEffectiveBaseV() != 0;
    }

    public boolean hasKnockBack() {
        return willApplyKnockback();
    }

    /**
     * 返回有效水平击退值（base + enchantLevel * enchantBonus）
     */
    public float getKnockBackH() {
        return knockbackProfile.getEffectiveBaseH();
    }

    /**
     * 设置旧算法的水平绝对值，并清零附魔等级。
     */
    public void setKnockbackBaseH(float knockBackH) {
        knockbackProfile.setBaseH(knockBackH).setEnchantLevel(0);
        this.knockbackOverrideMode = KnockbackOverrideMode.LEGACY_ABSOLUTE;
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
     * 设置旧算法的垂直绝对值，并清零附魔等级。
     */
    public void setKnockbackBaseV(float knockBackV) {
        knockbackProfile.setBaseV(knockBackV).setEnchantLevel(0);
        this.knockbackOverrideMode = KnockbackOverrideMode.LEGACY_ABSOLUTE;
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
     * 设置旧算法的水平和垂直绝对值，并清零附魔等级。
     */
    public void setKnockbackBase(float knockBackH, float knockBackV) {
        knockbackProfile.setBaseH(knockBackH).setBaseV(knockBackV).setEnchantLevel(0);
        this.knockbackOverrideMode = KnockbackOverrideMode.LEGACY_ABSOLUTE;
    }

    /**
     * 旧击退入口，仅作为 Profile 兼容层保留。新代码请使用 {@link #setKnockbackBase(float, float)}。
     */
    @Deprecated
    public void setKnockBack(float knockBackH, float knockBackV) {
        this.setKnockbackBase(knockBackH, knockBackV);
    }

    public void clearKnockback() {
        boolean javaEdition1_8 = this.usesJavaEdition1_8Knockback();
        knockbackProfile.setBaseH(0).setBaseV(0).setEnchantLevel(0);
        // strict 的固定公式需要显式禁用；legacy 保留后续事件重新写入击退的旧语义。
        this.knockbackOverrideMode = javaEdition1_8
                ? KnockbackOverrideMode.DISABLED : KnockbackOverrideMode.LEGACY_ABSOLUTE;
    }

    /**
     * 旧击退入口，仅作为 Profile 兼容层保留。新代码请使用 {@link #clearKnockback()}。
     */
    @Deprecated
    public void clearKnockBack() {
        this.clearKnockback();
    }

    /**
     * 复制另一个事件的完整 per-hit 击退上下文。
     */
    public void copyKnockbackProfileFrom(EntityDamageByEntityEvent source) {
        this.knockbackProfile.copyFrom(source.getKnockbackProfile());
        this.knockbackSource = source.knockbackSource;
        this.knockbackOverrideMode = source.knockbackOverrideMode;
        this.attackerSprinting = source.attackerSprinting;
        this.attackerYaw = source.attackerYaw;
        this.hasKnockbackSourceMotion = source.hasKnockbackSourceMotion;
        this.knockbackSourceMotionX = source.knockbackSourceMotionX;
        this.knockbackSourceMotionZ = source.knockbackSourceMotionZ;
    }

    public boolean isDefaultKnockback() {
        return knockbackProfile.getEffectiveBaseH() == GLOBAL_KNOCKBACK_H
                && knockbackProfile.getEffectiveBaseV() == GLOBAL_KNOCKBACK_V;
    }

    public Enchantment[] getWeaponEnchantments() {
        return enchantments;
    }
}
