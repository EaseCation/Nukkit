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
        registerPotion(WATER, new Potion(WATER, PotionNames.WATER, "water", Effect.getEffect(Effect.NO_EFFECT)));
        registerPotion(MUNDANE, new Potion(MUNDANE, PotionNames.MUNDANE, "mundane", Effect.getEffect(Effect.NO_EFFECT)));
        registerPotion(LONG_MUNDANE, new Potion(LONG_MUNDANE, PotionNames.LONG_MUNDANE, "mundane.extended", Effect.getEffect(Effect.NO_EFFECT)));
        registerPotion(THICK, new Potion(THICK, PotionNames.THICK, "thick", Effect.getEffect(Effect.NO_EFFECT)));
        registerPotion(AWKWARD, new Potion(AWKWARD, PotionNames.AWKWARD, "awkward", Effect.getEffect(Effect.NO_EFFECT)));
        registerPotion(NIGHTVISION, new Potion(NIGHTVISION, PotionNames.NIGHTVISION, "nightVision", Effect.getEffect(Effect.NIGHT_VISION).setDuration(180 * 20)));
        registerPotion(LONG_NIGHTVISION, new Potion(LONG_NIGHTVISION, PotionNames.LONG_NIGHTVISION, "nightVision", Effect.getEffect(Effect.NIGHT_VISION).setDuration(480 * 20)));
        registerPotion(INVISIBILITY, new Potion(INVISIBILITY, PotionNames.INVISIBILITY, "invisibility", Effect.getEffect(Effect.INVISIBILITY).setDuration(180 * 20)));
        registerPotion(LONG_INVISIBILITY, new Potion(LONG_INVISIBILITY, PotionNames.LONG_INVISIBILITY, "invisibility", Effect.getEffect(Effect.INVISIBILITY).setDuration(480 * 20)));
        registerPotion(LEAPING, new Potion(LEAPING, PotionNames.LEAPING, "jump", Effect.getEffect(Effect.JUMP_BOOST).setDuration(180 * 20)));
        registerPotion(LONG_LEAPING, new Potion(LONG_LEAPING, PotionNames.LONG_LEAPING, "jump", Effect.getEffect(Effect.JUMP_BOOST).setDuration(480 * 20)));
        registerPotion(STRONG_LEAPING, new Potion(STRONG_LEAPING, PotionNames.STRONG_LEAPING, "jump", Effect.getEffect(Effect.JUMP_BOOST).setDuration(90 * 20).setAmplifier(1)));
        registerPotion(FIRE_RESISTANCE, new Potion(FIRE_RESISTANCE, PotionNames.FIRE_RESISTANCE, "fireResistance", Effect.getEffect(Effect.FIRE_RESISTANCE).setDuration(180 * 20)));
        registerPotion(LONG_FIRE_RESISTANCE, new Potion(LONG_FIRE_RESISTANCE, PotionNames.LONG_FIRE_RESISTANCE, "fireResistance", Effect.getEffect(Effect.FIRE_RESISTANCE).setDuration(480 * 20)));
        registerPotion(SWIFTNESS, new Potion(SWIFTNESS, PotionNames.SWIFTNESS, "moveSpeed", Effect.getEffect(Effect.SPEED).setDuration(180 * 20)));
        registerPotion(LONG_SWIFTNESS, new Potion(LONG_SWIFTNESS, PotionNames.LONG_SWIFTNESS, "moveSpeed", Effect.getEffect(Effect.SPEED).setDuration(480 * 20)));
        registerPotion(STRONG_SWIFTNESS, new Potion(STRONG_SWIFTNESS, PotionNames.STRONG_SWIFTNESS, "moveSpeed", Effect.getEffect(Effect.SPEED).setDuration(90 * 20).setAmplifier(1)));
        registerPotion(SLOWNESS, new Potion(SLOWNESS, PotionNames.SLOWNESS, "moveSlowdown", Effect.getEffect(Effect.SLOWNESS).setDuration(90 * 20)));
        registerPotion(LONG_SLOWNESS, new Potion(LONG_SLOWNESS, PotionNames.LONG_SLOWNESS, "moveSlowdown", Effect.getEffect(Effect.SLOWNESS).setDuration(240 * 20)));
        registerPotion(WATER_BREATHING, new Potion(WATER_BREATHING, PotionNames.WATER_BREATHING, "waterBreathing", Effect.getEffect(Effect.WATER_BREATHING).setDuration(180 * 20)));
        registerPotion(LONG_WATER_BREATHING, new Potion(LONG_WATER_BREATHING, PotionNames.LONG_WATER_BREATHING, "waterBreathing", Effect.getEffect(Effect.WATER_BREATHING).setDuration(480 * 20)));
        registerPotion(HEALING, new Potion(HEALING, PotionNames.HEALING, "heal", Effect.getEffect(Effect.INSTANT_HEALTH)));
        registerPotion(STRONG_HEALING, new Potion(STRONG_HEALING, PotionNames.STRONG_HEALING, "heal", Effect.getEffect(Effect.INSTANT_HEALTH).setAmplifier(1)));
        registerPotion(HARMING, new Potion(HARMING, PotionNames.HARMING, "harm", Effect.getEffect(Effect.INSTANT_DAMAGE)));
        registerPotion(STRONG_HARMING, new Potion(STRONG_HARMING, PotionNames.STRONG_HARMING, "harm", Effect.getEffect(Effect.INSTANT_DAMAGE).setAmplifier(1)));
        registerPotion(POISON, new Potion(POISON, PotionNames.POISON, "poison", Effect.getEffect(Effect.POISON).setDuration(45 * 20)));
        registerPotion(LONG_POISON, new Potion(LONG_POISON, PotionNames.LONG_POISON, "poison", Effect.getEffect(Effect.POISON).setDuration(120 * 20)));
        registerPotion(STRONG_POISON, new Potion(STRONG_POISON, PotionNames.STRONG_POISON, "poison", Effect.getEffect(Effect.POISON).setDuration(22 * 20).setAmplifier(1)));
        registerPotion(REGENERATION, new Potion(REGENERATION, PotionNames.REGENERATION, "regeneration", Effect.getEffect(Effect.REGENERATION).setDuration(45 * 20)));
        registerPotion(LONG_REGENERATION, new Potion(LONG_REGENERATION, PotionNames.LONG_REGENERATION, "regeneration", Effect.getEffect(Effect.REGENERATION).setDuration(120 * 20)));
        registerPotion(STRONG_REGENERATION, new Potion(STRONG_REGENERATION, PotionNames.STRONG_REGENERATION, "regeneration", Effect.getEffect(Effect.REGENERATION).setDuration(22 * 20).setAmplifier(1)));
        registerPotion(STRENGTH, new Potion(STRENGTH, PotionNames.STRENGTH, "damageBoost", Effect.getEffect(Effect.STRENGTH).setDuration(180 * 20)));
        registerPotion(LONG_STRENGTH, new Potion(LONG_STRENGTH, PotionNames.LONG_STRENGTH, "damageBoost", Effect.getEffect(Effect.STRENGTH).setDuration(480 * 20)));
        registerPotion(STRONG_STRENGTH, new Potion(STRONG_STRENGTH, PotionNames.STRONG_STRENGTH, "damageBoost", Effect.getEffect(Effect.STRENGTH).setDuration(90 * 20).setAmplifier(1)));
        registerPotion(WEAKNESS, new Potion(WEAKNESS, PotionNames.WEAKNESS, "weakness", Effect.getEffect(Effect.WEAKNESS).setDuration(90 * 20)));
        registerPotion(LONG_WEAKNESS, new Potion(LONG_WEAKNESS, PotionNames.LONG_WEAKNESS, "weakness", Effect.getEffect(Effect.WEAKNESS).setDuration(240 * 20)));
        registerPotion(WITHER, new Potion(WITHER, PotionNames.WITHER, "wither", Effect.getEffect(Effect.WITHER).setDuration(40 * 20).setAmplifier(1)));

        registerPotion(TURTLE_MASTER, new Potion(TURTLE_MASTER, PotionNames.TURTLE_MASTER, "turtleMaster", Effect.getEffect(Effect.SLOWNESS).setDuration(20 * 20).setAmplifier(3), Effect.getEffect(Effect.RESISTANCE).setDuration(20 * 20).setAmplifier(2)), V1_5_0);
        registerPotion(LONG_TURTLE_MASTER, new Potion(LONG_TURTLE_MASTER, PotionNames.LONG_TURTLE_MASTER, "turtleMaster", Effect.getEffect(Effect.SLOWNESS).setDuration(40 * 20).setAmplifier(3), Effect.getEffect(Effect.RESISTANCE).setDuration(40 * 20).setAmplifier(2)), V1_5_0);
        registerPotion(STRONG_TURTLE_MASTER, new Potion(STRONG_TURTLE_MASTER, PotionNames.STRONG_TURTLE_MASTER, "turtleMaster", Effect.getEffect(Effect.SLOWNESS).setDuration(20 * 20).setAmplifier(5), Effect.getEffect(Effect.RESISTANCE).setDuration(20 * 20).setAmplifier(3)), V1_5_0);

        registerPotion(SLOW_FALLING, new Potion(SLOW_FALLING, PotionNames.SLOW_FALLING, "slowFalling", Effect.getEffect(Effect.SLOW_FALLING).setDuration(90 * 20)), V1_6_0);
        registerPotion(LONG_SLOW_FALLING, new Potion(LONG_SLOW_FALLING, PotionNames.LONG_SLOW_FALLING, "slowFalling", Effect.getEffect(Effect.SLOW_FALLING).setDuration(240 * 20)), V1_6_0);

        registerPotion(STRONG_SLOWNESS, new Potion(STRONG_SLOWNESS, PotionNames.STRONG_SLOWNESS, "moveSlowdown", Effect.getEffect(Effect.SLOWNESS).setDuration(20 * 20).setAmplifier(3)), V1_16_0);

        registerPotion(WIND_CHARGED, new Potion(WIND_CHARGED, PotionNames.WIND_CHARGED, "windCharged", Effect.getEffect(Effect.WIND_CHARGED).setDuration(180 * 20)), V1_21_0);
        registerPotion(WEAVING, new Potion(WEAVING, PotionNames.WEAVING, "weaving", Effect.getEffect(Effect.WEAVING).setDuration(180 * 20)), V1_21_0);
        registerPotion(OOZING, new Potion(OOZING, PotionNames.OOZING, "oozing", Effect.getEffect(Effect.OOZING).setDuration(180 * 20)), V1_21_0);
        registerPotion(INFESTED, new Potion(INFESTED, PotionNames.INFESTED, "infested", Effect.getEffect(Effect.INFESTED).setDuration(180 * 20)), V1_21_0);

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
        return getPotionByIdentifier(identifier, true);
    }

    @Nullable
    public static Potion getPotionByIdentifier(String identifier, boolean namespaced) {
        if (namespaced && identifier.startsWith("minecraft:")) {
            identifier = identifier.substring(10);
        }

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
