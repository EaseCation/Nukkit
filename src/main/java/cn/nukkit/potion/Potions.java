package cn.nukkit.potion;

import cn.nukkit.GameVersion;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import javax.annotation.Nullable;
import java.util.Map;

import static cn.nukkit.GameVersion.*;
import static cn.nukkit.potion.PotionID.*;

public final class Potions {
    private static final Map<String, Potion> BY_IDENTIFIER = new Object2ObjectOpenHashMap<>();

    public static void registerVanillaPotions() {
        registerPotion(WATER, new Potion(WATER, "water", "water", Effect.getEffect(Effect.NO_EFFECT)));
        registerPotion(MUNDANE, new Potion(MUNDANE, "mundane", "mundane", Effect.getEffect(Effect.NO_EFFECT)));
        registerPotion(MUNDANE_LONG, new Potion(MUNDANE_LONG, "long_mundane", "mundane.extended", Effect.getEffect(Effect.NO_EFFECT)));
        registerPotion(THICK, new Potion(THICK, "thick", "thick", Effect.getEffect(Effect.NO_EFFECT)));
        registerPotion(AWKWARD, new Potion(AWKWARD, "awkward", "awkward", Effect.getEffect(Effect.NO_EFFECT)));
        registerPotion(NIGHTVISION, new Potion(NIGHTVISION, "nightvision", "nightVision", Effect.getEffect(Effect.NIGHT_VISION).setDuration(180 * 20)));
        registerPotion(NIGHTVISION_LONG, new Potion(NIGHTVISION_LONG, "long_nightvision", "nightVision", Effect.getEffect(Effect.NIGHT_VISION).setDuration(480 * 20)));
        registerPotion(INVISIBILITY, new Potion(INVISIBILITY, "invisibility", "invisibility", Effect.getEffect(Effect.INVISIBILITY).setDuration(180 * 20)));
        registerPotion(INVISIBILITY_LONG, new Potion(INVISIBILITY_LONG, "long_invisibility", "invisibility", Effect.getEffect(Effect.INVISIBILITY).setDuration(480 * 20)));
        registerPotion(LEAPING, new Potion(LEAPING, "leaping", "jump", Effect.getEffect(Effect.JUMP_BOOST).setDuration(180 * 20)));
        registerPotion(LEAPING_LONG, new Potion(LEAPING_LONG, "long_leaping", "jump", Effect.getEffect(Effect.JUMP_BOOST).setDuration(480 * 20)));
        registerPotion(LEAPING_STRONG, new Potion(LEAPING_STRONG, "strong_leaping", "jump", Effect.getEffect(Effect.JUMP_BOOST).setDuration(90 * 20).setAmplifier(1)));
        registerPotion(FIRE_RESISTANCE, new Potion(FIRE_RESISTANCE, "fire_resistance", "fireResistance", Effect.getEffect(Effect.FIRE_RESISTANCE).setDuration(180 * 20)));
        registerPotion(FIRE_RESISTANCE_LONG, new Potion(FIRE_RESISTANCE_LONG, "long_fire_resistance", "fireResistance", Effect.getEffect(Effect.FIRE_RESISTANCE).setDuration(480 * 20)));
        registerPotion(SWIFTNESS, new Potion(SWIFTNESS, "swiftness", "moveSpeed", Effect.getEffect(Effect.SPEED).setDuration(180 * 20)));
        registerPotion(SWIFTNESS_LONG, new Potion(SWIFTNESS_LONG, "long_swiftness", "moveSpeed", Effect.getEffect(Effect.SPEED).setDuration(480 * 20)));
        registerPotion(SWIFTNESS_STRONG, new Potion(SWIFTNESS_STRONG, "strong_swiftness", "moveSpeed", Effect.getEffect(Effect.SPEED).setDuration(90 * 20).setAmplifier(1)));
        registerPotion(SLOWNESS, new Potion(SLOWNESS, "slowness", "moveSlowdown", Effect.getEffect(Effect.SLOWNESS).setDuration(90 * 20)));
        registerPotion(SLOWNESS_LONG, new Potion(SLOWNESS_LONG, "long_slowness", "moveSlowdown", Effect.getEffect(Effect.SLOWNESS).setDuration(240 * 20)));
        registerPotion(WATER_BREATHING, new Potion(WATER_BREATHING, "water_breathing", "waterBreathing", Effect.getEffect(Effect.WATER_BREATHING).setDuration(180 * 20)));
        registerPotion(WATER_BREATHING_LONG, new Potion(WATER_BREATHING_LONG, "long_water_breathing", "waterBreathing", Effect.getEffect(Effect.WATER_BREATHING).setDuration(480 * 20)));
        registerPotion(HEALING, new Potion(HEALING, "healing", "heal", Effect.getEffect(Effect.INSTANT_HEALTH)));
        registerPotion(HEALING_STRONG, new Potion(HEALING_STRONG, "strong_healing", "heal", Effect.getEffect(Effect.INSTANT_HEALTH).setAmplifier(1)));
        registerPotion(HARMING, new Potion(HARMING, "harming", "harm", Effect.getEffect(Effect.INSTANT_DAMAGE)));
        registerPotion(HARMING_STRONG, new Potion(HARMING_STRONG, "strong_harming", "harm", Effect.getEffect(Effect.INSTANT_DAMAGE).setAmplifier(1)));
        registerPotion(POISON, new Potion(POISON, "poison", "poison", Effect.getEffect(Effect.POISON).setDuration(45 * 20)));
        registerPotion(POISON_LONG, new Potion(POISON_LONG, "long_poison", "poison", Effect.getEffect(Effect.POISON).setDuration(120 * 20)));
        registerPotion(POISON_STRONG, new Potion(POISON_STRONG, "strong_poison", "poison", Effect.getEffect(Effect.POISON).setDuration(22 * 20).setAmplifier(1)));
        registerPotion(REGENERATION, new Potion(REGENERATION, "regeneration", "regeneration", Effect.getEffect(Effect.REGENERATION).setDuration(45 * 20)));
        registerPotion(REGENERATION_LONG, new Potion(REGENERATION_LONG, "long_regeneration", "regeneration", Effect.getEffect(Effect.REGENERATION).setDuration(120 * 20)));
        registerPotion(REGENERATION_STRONG, new Potion(REGENERATION_STRONG, "strong_regeneration", "regeneration", Effect.getEffect(Effect.REGENERATION).setDuration(22 * 20).setAmplifier(1)));
        registerPotion(STRENGTH, new Potion(STRENGTH, "strength", "damageBoost", Effect.getEffect(Effect.STRENGTH).setDuration(180 * 20)));
        registerPotion(STRENGTH_LONG, new Potion(STRENGTH_LONG, "long_strength", "damageBoost", Effect.getEffect(Effect.STRENGTH).setDuration(480 * 20)));
        registerPotion(STRENGTH_STRONG, new Potion(STRENGTH_STRONG, "strong_strength", "damageBoost", Effect.getEffect(Effect.STRENGTH).setDuration(90 * 20).setAmplifier(1)));
        registerPotion(WEAKNESS, new Potion(WEAKNESS, "weakness", "weakness", Effect.getEffect(Effect.WEAKNESS).setDuration(90 * 20)));
        registerPotion(WEAKNESS_LONG, new Potion(WEAKNESS_LONG, "long_weakness", "weakness", Effect.getEffect(Effect.WEAKNESS).setDuration(240 * 20)));
        registerPotion(WITHER, new Potion(WITHER, "wither", "wither", Effect.getEffect(Effect.WITHER).setDuration(40 * 20).setAmplifier(1)));

        registerPotion(TURTLE_MASTER, new Potion(TURTLE_MASTER, "turtle_master", "turtleMaster", Effect.getEffect(Effect.SLOWNESS).setDuration(20 * 20).setAmplifier(3), Effect.getEffect(Effect.RESISTANCE).setDuration(20 * 20).setAmplifier(2)), V1_5_0);
        registerPotion(TURTLE_MASTER_LONG, new Potion(TURTLE_MASTER_LONG, "long_turtle_master", "turtleMaster", Effect.getEffect(Effect.SLOWNESS).setDuration(40 * 20).setAmplifier(3), Effect.getEffect(Effect.RESISTANCE).setDuration(40 * 20).setAmplifier(2)), V1_5_0);
        registerPotion(TURTLE_MASTER_STRONG, new Potion(TURTLE_MASTER_STRONG, "strong_turtle_master", "turtleMaster", Effect.getEffect(Effect.SLOWNESS).setDuration(20 * 20).setAmplifier(5), Effect.getEffect(Effect.RESISTANCE).setDuration(20 * 20).setAmplifier(3)), V1_5_0);

        registerPotion(SLOW_FALLING, new Potion(SLOW_FALLING, "slow_falling", "slowFalling", Effect.getEffect(Effect.SLOW_FALLING).setDuration(90 * 20)), V1_6_0);
        registerPotion(SLOW_FALLING_LONG, new Potion(SLOW_FALLING_LONG, "long_slow_falling", "slowFalling", Effect.getEffect(Effect.SLOW_FALLING).setDuration(240 * 20)), V1_6_0);

        registerPotion(SLOWNESS_STRONG, new Potion(SLOWNESS_STRONG, "strong_slowness", "moveSlowdown", Effect.getEffect(Effect.SLOWNESS).setDuration(20 * 20).setAmplifier(3)), V1_16_0);

    }

    private static Potion registerPotion(int id, Potion potion) {
        if (Potion.potions[id] == null) {
            Potion.potions[id] = potion;
        }
        BY_IDENTIFIER.put(potion.getIdentifier(), potion);
        return Potion.potions[id];
    }

    /**
     * @param version min required base game version
     */
    private static Potion registerPotion(int id, Potion potion, GameVersion version) {
        if (!version.isAvailable()) {
//            return null;
        }
        return registerPotion(id, potion);
    }

    @Nullable
    public static Potion getPotionByIdentifier(String identifier) {
        Potion potion = BY_IDENTIFIER.get(identifier);
        if (potion == null) {
            return null;
        }
        return potion;
    }

    public static Map<String, Potion> getPotions() {
        return BY_IDENTIFIER;
    }

    private Potions() {
        throw new IllegalStateException();
    }
}
