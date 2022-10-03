package cn.nukkit.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.potion.Effect;

import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EntityDamageByEntityEvent extends EntityDamageEvent {

    public static float GLOBAL_KNOCKBACK_H = 0.29f;
    public static float GLOBAL_KNOCKBACK_V = 0.29f;

    private final Entity damager;

    private float knockBackH;
    private float knockBackV;

    private Enchantment[] enchantments;

    public EntityDamageByEntityEvent(Entity damager, Entity entity, DamageCause cause, float damage) {
        this(damager, entity, cause, damage, GLOBAL_KNOCKBACK_H, GLOBAL_KNOCKBACK_V);
    }

    public EntityDamageByEntityEvent(Entity damager, Entity entity, DamageCause cause, Map<DamageModifier, Float> modifiers) {
        this(damager, entity, cause, modifiers, GLOBAL_KNOCKBACK_H, GLOBAL_KNOCKBACK_V);
    }

    public EntityDamageByEntityEvent(Entity damager, Entity entity, DamageCause cause, float damage, float knockBackH, float knockBackV) {
        super(entity, cause, damage);
        this.damager = damager;
        this.knockBackH = knockBackH;
        this.knockBackV = knockBackV;
        this.addAttackerModifiers(damager);
    }

    public EntityDamageByEntityEvent(Entity damager, Entity entity, DamageCause cause, Map<DamageModifier, Float> modifiers, float knockBackH, float knockBackV) {
        this(damager, entity, cause, modifiers, knockBackH, knockBackV, new Enchantment[0]);
    }

    public EntityDamageByEntityEvent(Entity damager, Entity entity, DamageCause cause, Map<DamageModifier, Float> modifiers, float knockBackH, float knockBackV, Enchantment[] enchantments) {
        super(entity, cause, modifiers);
        this.damager = damager;
        this.knockBackH = knockBackH;
        this.knockBackV = knockBackV;
        this.enchantments = enchantments;
        this.addAttackerModifiers(damager);
    }

    protected void addAttackerModifiers(Entity damager) {
        if (damager.hasEffect(Effect.STRENGTH)) {
            this.setDamage((float) (this.getDamage(DamageModifier.BASE) * 0.3 * (damager.getEffect(Effect.STRENGTH).getAmplifier() + 1)), DamageModifier.STRENGTH);
        }

        if (damager.hasEffect(Effect.WEAKNESS)) {
            this.setDamage(-(float) (this.getDamage(DamageModifier.BASE) * 0.2 * (damager.getEffect(Effect.WEAKNESS).getAmplifier() + 1)), DamageModifier.WEAKNESS);
        }
    }

    public Entity getDamager() {
        return damager;
    }

    public boolean hasKnockBack() {
        return knockBackH != 0 || knockBackV != 0;
    }

    public float getKnockBackH() {
        return knockBackH;
    }

    public void setKnockBackH(float knockBackH) {
        this.knockBackH = knockBackH;
    }

    public float getKnockBackV() {
        return knockBackV;
    }

    public void setKnockBackV(float knockBackV) {
        this.knockBackV = knockBackV;
    }

    public void setKnockBack(float knockBack) {
        this.setKnockBack(knockBack, knockBack);
    }

    public void setKnockBack(float knockBackH, float knockBackV) {
        this.knockBackH = knockBackH;
        this.knockBackV = knockBackV;
    }

    public void clearKnockBack() {
        this.knockBackH = 0;
        this.knockBackV = 0;
    }

    public Enchantment[] getWeaponEnchantments() {
        return enchantments;
    }
}