package cn.nukkit.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.EventException;
import com.google.common.collect.ImmutableMap;

import java.util.EnumMap;
import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EntityDamageEvent extends EntityEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private int attackCooldown = 10;
    private final DamageCause cause;

    private final Map<DamageModifier, Float> modifiers;
    private final Map<DamageModifier, Float> originals;

    public EntityDamageEvent(Entity entity, DamageCause cause, float damage) {
        this(entity, cause, new EnumMap<DamageModifier, Float>(DamageModifier.class) {
            {
                put(DamageModifier.BASE, damage);
            }
        });
    }

    public EntityDamageEvent(Entity entity, DamageCause cause, Map<DamageModifier, Float> modifiers) {
        this.entity = entity;
        this.cause = cause;
        this.modifiers = new EnumMap<>(modifiers);

        this.originals = ImmutableMap.copyOf(this.modifiers);

        if (!this.modifiers.containsKey(DamageModifier.BASE)) {
            throw new EventException("BASE Damage modifier missing");
        }

        if (cause == DamageCause.SUICIDE || cause == DamageCause.VOID || cause == DamageCause.HUNGER) {
            return;
        }
        Effect resistance = entity.getEffect(Effect.RESISTANCE);
        if (resistance != null) {
            this.setDamage(-(this.getDamage(DamageModifier.BASE) * 0.2f * (resistance.getAmplifier() + 1)), DamageModifier.RESISTANCE);
        }
    }

    public DamageCause getCause() {
        return cause;
    }

    public float getOriginalDamage() {
        return this.getOriginalDamage(DamageModifier.BASE);
    }

    public float getOriginalDamage(DamageModifier type) {
        Float damage = this.originals.get(type);
        if (damage != null) {
            return damage;
        }

        return 0;
    }

    public float getDamage() {
        return this.getDamage(DamageModifier.BASE);
    }

    public float getDamage(DamageModifier type) {
        Float damage = this.modifiers.get(type);
        if (damage != null) {
            return damage;
        }

        return 0;
    }

    public void setDamage(float damage) {
        this.setDamage(damage, DamageModifier.BASE);
    }

    public void setDamage(float damage, DamageModifier type) {
        this.modifiers.put(type, damage);
    }

    public boolean isApplicable(DamageModifier type) {
        return this.modifiers.containsKey(type);
    }

    public float getFinalDamage() {
        float damage = 0;
        for (Float d : this.modifiers.values()) {
            if (d != null) {
                damage += d;
            }
        }

        return damage;
    }

    public int getAttackCooldown() {
        return this.attackCooldown;
    }

    public void setAttackCooldown(int attackCooldown) {
        this.attackCooldown = attackCooldown;
    }

    public boolean canBeReducedByArmor() {
        switch (this.cause) {
            case FIRE_TICK:
            case FREEZE:
            case TEMPERATURE:
            case SUFFOCATION:
            case DROWNING:
            case HUNGER:
            case FALL:
            case STALAGMITE:
            case FLY_INTO_WALL:
            case VOID:
            case MAGIC:
            case WITHER:
            case SONIC_BOOM:
            case SUICIDE:
                return false;
        }
        return true;
    }

    public Map<DamageModifier, Float> getOriginals() {
        return originals;
    }

    public Map<DamageModifier, Float> getModifiers() {
        return modifiers;
    }

    public enum DamageModifier {
        /**
         * Raw amount of damage
         */
        BASE,
        /**
         * Damage reduction caused by wearing armor
         */
        ARMOR,
        /**
         * Additional damage caused by damager's Strength potion effect
         */
        STRENGTH,
        /**
         * Damage reduction caused by damager's Weakness potion effect
         */
        WEAKNESS,
        /**
         * Damage reduction caused by the Resistance potion effect
         */
        RESISTANCE,
        /**
         * Damage reduction caused by the Damage absorption effect
         */
        ABSORPTION,
        /**
         * Damage reduction caused by the armor enchantments worn.
         */
        ARMOR_ENCHANTMENTS,
        /**
         * Enchantment: Bane of Arthropods
         */
        BANE_OF_ARTHROPODS,
        /**
         * Enchantment: Smite
         */
        SMITE
    }

    public enum DamageCause {
//        OVERRIDE,
        /**
         * Damage caused by contact with a block such as a Cactus
         */
        CONTACT,
        /**
         * Damage caused by being attacked by another entity
         */
        ENTITY_ATTACK,
        /**
         * Damage caused by being hit by a projectile such as an Arrow
         */
        PROJECTILE,
        /**
         * Damage caused by being put in a block
         */
        SUFFOCATION,
        /**
         * Fall damage
         */
        FALL,
        /**
         * Damage caused by standing in fire
         */
        FIRE,
        /**
         * Burn damage
         */
        FIRE_TICK,
        /**
         * Damage caused by standing in lava
         */
        LAVA,
        /**
         * Damage caused by running out of air underwater
         */
        DROWNING,
        /**
         * Block explosion damage
         */
        BLOCK_EXPLOSION,
        /**
         * Entity explosion damage
         */
        ENTITY_EXPLOSION,
        /**
         * Damage caused by falling into the void
         */
        VOID,
        /**
         * Player commits suicide
         */
        SUICIDE,
        /**
         * Potion or spell damage
         */
        MAGIC,
        WITHER,
        /**
         * Damage caused by hunger
         */
        HUNGER,
        ANVIL,
        THORNS,
        FALLING_BLOCK,
        PISTON,
        FLY_INTO_WALL,
        MAGMA,
        FIREWORKS,
        /**
         * Damage caused by being struck by lightning
         */
        LIGHTNING,
        CHARGING,
        TEMPERATURE,
        /**
         * Damage caused from freezing.
         */
        FREEZE,
        STALACTITE,
        STALAGMITE,
        RAM_ATTACK,
        SONIC_BOOM,
        CAMPFIRE,
        SOUL_CAMPFIRE,
        MACE_SMASH,
        /**
         * Plugins
         */
        CUSTOM,
    }
}
