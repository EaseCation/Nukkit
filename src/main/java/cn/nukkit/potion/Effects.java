package cn.nukkit.potion;

import cn.nukkit.GameVersion;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import javax.annotation.Nullable;
import java.util.Map;

import static cn.nukkit.GameVersion.*;
import static cn.nukkit.potion.EffectID.*;

public final class Effects {
    private static final Map<String, Effect> IDENTIFIER_TO_EFFECT = new Object2ObjectOpenHashMap<>();

    public static void registerVanillaEffects() {
        registerEffect(SPEED, new Effect(SPEED, "speed", "%potion.moveSpeed", 124, 175, 198));
        registerEffect(SLOWNESS, new Effect(SLOWNESS, "slowness", "%potion.moveSlowdown", 90, 108, 129, true));
        registerEffect(HASTE, new Effect(HASTE, "haste", "%potion.digSpeed", 217, 192, 67));
        registerEffect(MINING_FATIGUE, new Effect(MINING_FATIGUE, "mining_fatigue", "%potion.digSlowDown", 74, 66, 23, true));
        registerEffect(STRENGTH, new Effect(STRENGTH, "strength", "%potion.damageBoost", 147, 36, 35));
        registerEffect(INSTANT_HEALTH, new InstantEffect(INSTANT_HEALTH, "instant_health", "%potion.heal", 248, 36, 35));
        registerEffect(INSTANT_DAMAGE, new InstantEffect(INSTANT_DAMAGE, "instant_damage", "%potion.harm", 67, 10, 9, true));
        registerEffect(JUMP_BOOST, new Effect(JUMP_BOOST, "jump_boost", "%potion.jump", 34, 255, 76));
        registerEffect(NAUSEA, new Effect(NAUSEA, "nausea", "%potion.confusion", 85, 29, 74, true));
        registerEffect(REGENERATION, new Effect(REGENERATION, "regeneration", "%potion.regeneration", 205, 92, 171));
        registerEffect(RESISTANCE, new Effect(RESISTANCE, "resistance", "%potion.resistance", 153, 69, 58));
        registerEffect(FIRE_RESISTANCE, new Effect(FIRE_RESISTANCE, "fire_resistance", "%potion.fireResistance", 228, 154, 58));
        registerEffect(WATER_BREATHING, new Effect(WATER_BREATHING, "water_breathing", "%potion.waterBreathing", 46, 82, 153));
        registerEffect(INVISIBILITY, new Effect(INVISIBILITY, "invisibility", "%potion.invisibility", 127, 131, 146));
        registerEffect(BLINDNESS, new Effect(BLINDNESS, "blindness", "%potion.blindness", 191, 192, 192));
        registerEffect(NIGHT_VISION, new Effect(NIGHT_VISION, "night_vision", "%potion.nightVision", 0, 0, 139));
        registerEffect(HUNGER, new Effect(HUNGER, "hunger", "%potion.hunger", 46, 139, 87));
        registerEffect(WEAKNESS, new Effect(WEAKNESS, "weakness", "%potion.weakness", 72, 77, 72, true));
        registerEffect(POISON, new Effect(POISON, "poison", "%potion.poison", 78, 147, 49, true));
        registerEffect(WITHER, new Effect(WITHER, "wither", "%potion.wither", 53, 42, 39, true));
        registerEffect(HEALTH_BOOST, new Effect(HEALTH_BOOST, "health_boost", "%potion.healthBoost", 248, 125, 35));
        registerEffect(ABSORPTION, new Effect(ABSORPTION, "absorption", "%potion.absorption", 36, 107, 251));
        registerEffect(SATURATION, new Effect(SATURATION, "saturation", "%potion.saturation", 255, 0, 255));
        registerEffect(LEVITATION, new Effect(LEVITATION, "levitation", "%potion.levitation", 206, 255, 255, true));
        registerEffect(FATAL_POISON, new Effect(FATAL_POISON, "fatal_poison", "%potion.poison", 78, 147, 49, true));

        registerEffect(CONDUIT_POWER, new Effect(CONDUIT_POWER, "conduit_power", "%potion.conduitPower", 29, 194, 209), V1_5_0);

        registerEffect(SLOW_FALLING, new Effect(SLOW_FALLING, "slow_falling", "%potion.slowFalling", 206, 255, 255), V1_6_0);

        registerEffect(BAD_OMEN, new Effect(BAD_OMEN, "bad_omen", "%badOmen", 11, 97, 56, true), V1_11_0);
        registerEffect(VILLAGE_HERO, new Effect(VILLAGE_HERO, "village_hero", "%villageHero", 68, 255, 68).setVisible(false), V1_11_0);

        registerEffect(DARKNESS, new Effect(DARKNESS, "darkness", "%darkness", 41, 39, 33, true).setVisible(false), V1_19_0);

    }

    private static Effect registerEffect(int id, Effect effect) {
        if (Effect.effects[id] == null) {
            Effect.effects[id] = effect;
        }
        IDENTIFIER_TO_EFFECT.put(effect.getIdentifier(), effect);
        return Effect.effects[id];
    }

    /**
     * @param version min required base game version
     */
    private static Effect registerEffect(int id, Effect effect, GameVersion version) {
        if (!version.isAvailable()) {
            return null;
        }
        return registerEffect(id, effect);
    }

    @Nullable
    public static Effect getEffectByIdentifier(String identifier) {
        Effect effect = IDENTIFIER_TO_EFFECT.get(identifier);
        if (effect == null) {
            return null;
        }
        return effect.clone();
    }

    public static Map<String, Effect> getEffects() {
        return IDENTIFIER_TO_EFFECT;
    }

    private Effects() {
        throw new IllegalStateException();
    }
}
