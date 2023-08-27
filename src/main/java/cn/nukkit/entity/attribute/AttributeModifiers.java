package cn.nukkit.entity.attribute;

import cn.nukkit.entity.attribute.Attribute.Operand;
import cn.nukkit.entity.attribute.AttributeModifier.Operation;
import cn.nukkit.item.enchantment.EnchantmentSoulSpeed;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public final class AttributeModifiers {
    /**
     * SharedModifiers::DAMAGE_BOOST attack_damage
     */
    public static final AttributeModifier DAMAGE_BOOST = new AttributeModifier("648D7064-6A60-4F59-8ABE-C2C23A6DD7A9", "DamageBoost", 2.5f, Operation.MULTIPLY_TOTAL, Operand.CURRENT, false);

    /**
     * SharedModifiers::HEALTH_BOOST health
     */
    public static final AttributeModifier HEALTH_BOOST = new AttributeModifier("5D6F0BA2-1186-46AC-B896-C61C5CEE99CC", "HealthBoost", 4, Operation.ADDITION, Operand.MAX, false);

    /**
     * SharedModifiers::MOVEMENT_SPEED movement
     */
    public static final AttributeModifier MOVEMENT_SPEED = new AttributeModifier("91AEAA56-376B-4498-935B-2F7F68070635", "MovementSpeed", 0.2f, Operation.MULTIPLY_TOTAL, Operand.CURRENT, false);

    /**
     * SharedModifiers::MOVEMENT_SLOWDOWN movement
     */
    public static final AttributeModifier MOVEMENT_SLOWDOWN = new AttributeModifier("7107DE5E-7CE8-4030-940E-514C1F160890", "MovementSlowdown", -0.15f, Operation.MULTIPLY_TOTAL, Operand.CURRENT, false);

    /**
     * SharedModifiers::WEAKNESS attack_damage
     */
    public static final AttributeModifier WEAKNESS = new AttributeModifier("22653B89-116E-49DC-9B6B-9971489B5BE5", "Weakness", 2, Operation.ADDITION, Operand.CURRENT, false);

    /**
     * Mob::setSprinting movement
     */
    public static final AttributeModifier SPRINTING_BOOST = new AttributeModifier("D208FC00-42AA-4AAD-9276-D5446530DE43", "Sprinting speed boost", 0.3f, Operation.MULTIPLY_TOTAL, Operand.CURRENT, false);

    /**
     * Zombie::reloadHardcoded (Spawned)
     */
    public static final UUID ZOMBIE_SPAWN_BONUS_UUID = UUID.fromString("C30FCD33-1E56-46A0-B46E-D73DDF6972FD");

    /**
     * knockback_resistance
     */
    public static AttributeModifier zombieSpawnBonusKnockbackResistance() {
        return new AttributeModifier(ZOMBIE_SPAWN_BONUS_UUID, "RandomSpawnBonus", ThreadLocalRandom.current().nextFloat() * 0.05f, Operation.ADDITION, Operand.CURRENT);
    }

    /**
     * follow_range
     */
    @Nullable
    public static AttributeModifier zombieSpawnBonusFollowRange() {
        float followRangeModifier = ThreadLocalRandom.current().nextFloat() * 1.5f;
        if (followRangeModifier <= 1) {
            return null;
        }
        return new AttributeModifier(ZOMBIE_SPAWN_BONUS_UUID, "RandomSpawnBonus", followRangeModifier, Operation.MULTIPLY_TOTAL, Operand.CURRENT);
    }

    /**
     * EnderMan::SPEED_MODIFIER_ATTACKING movement
     */
    public static final AttributeModifier ENDERMAN_SPEED_MODIFIER_ATTACKING = new AttributeModifier("020E0DFB-87AE-4653-9556-831010E291A0", "Attacking speed boost", 0.15f, Operation.ADDITION, Operand.MIN);

    public static final UUID WITHER_MAX_HEALTH_CAP_UUID = UUID.fromString("57D213F1-9DBE-4194-B3BA-89D3EF237171");

    /**
     * WitherBoss::changePhase health
     */
    public static AttributeModifier witherChangePhase(float maxHealth) {
        return new AttributeModifier(WITHER_MAX_HEALTH_CAP_UUID, "PhaseHealthCap", maxHealth, Operation.CAP, Operand.CURRENT);
    }

    /**
     * Shulker::COVERED_ARMOR_MODIFIER armor???
     */
    public static final AttributeModifier SHULKER_COVERED_ARMOR_MODIFIER = new AttributeModifier("D984A847-60C7-423F-94C5-D2F902057847", "Closed Armor Modifier", 20, Operation.ADDITION, Operand.CURRENT);

    /**
     * BoostableComponent::boost movement
     */
    public static final AttributeModifier SPEED_MODIFIER_BOOSTING = new AttributeModifier("D984A847-60C7-423F-94C5-D2F902057847", "Boosting speed boost", 1.35f, Operation.MULTIPLY_BASE, Operand.CURRENT);

    public static final UUID SPEED_MODIFIER_DRINKING_UUID = UUID.fromString("5CD17E52-A79A-43D3-A529-90FDE04B181E");

    /**
     * DrinkPotionGoal (Witch) movement
     */
    public static AttributeModifier drinkingPotion(float walkSpeedModifier) {
        return new AttributeModifier(SPEED_MODIFIER_DRINKING_UUID, "Drinking speed penalty", walkSpeedModifier, Operation.ADDITION, Operand.MIN);
    }

    /**
     * KnockbackArmorUpdater::onEvent (+0.1 per Netherite Armor) knockback_resistance
     */
    public static AttributeModifier armorKnockbackResistance(int slot, float knockbackResistance) {
        String name = "knockback_modifier_" + slot;
        return new AttributeModifier(UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8)), name, knockbackResistance, Operation.ADDITION, Operand.CURRENT);
    }

    public static final UUID SOUL_SPEED_BOOST_UUID = UUID.fromString("87f46a96-686f-4796-b035-22e16ee9e038");

    /**
     * Mob::_processSoulSpeed movement
     */
    public static AttributeModifier soulSpeed(int enchantLevel) {
        return new AttributeModifier(SOUL_SPEED_BOOST_UUID, "Sprinting speed boost", EnchantmentSoulSpeed.getSpeedBoost(enchantLevel), Operation.ADDITION, Operand.CURRENT, false);
    }

    public static final UUID FREEZE_EFFECT_UUID = UUID.fromString("f1800c58-22ab-11eb-adc1-0242ac120002");

    /**
     * FreezingSystemInternal::processFreezeEffect (Mob::addSpeedModifier) movement
     */
    public static AttributeModifier freezeEffect(float freezeEffectStrength) {
        return new AttributeModifier(FREEZE_EFFECT_UUID, "Freeze effect", freezeEffectStrength * -0.05f, Operation.ADDITION, Operand.CURRENT);
    }

    public static void initializeVanillaAttributeModifiers() {
    }

    private AttributeModifiers() {
        throw new IllegalStateException();
    }
}
