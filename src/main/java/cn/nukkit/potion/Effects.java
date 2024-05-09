package cn.nukkit.potion;

import cn.nukkit.GameVersion;
import cn.nukkit.entity.attribute.Attribute;
import cn.nukkit.entity.attribute.AttributeModifier;
import cn.nukkit.entity.attribute.AttributeModifiers;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import javax.annotation.Nullable;
import java.util.Map;

import static cn.nukkit.GameVersion.*;
import static cn.nukkit.SharedConstants.*;
import static cn.nukkit.potion.EffectID.*;

public final class Effects {
    private static final Map<String, Effect> IDENTIFIER_TO_EFFECT = new Object2ObjectOpenHashMap<>();

    public static void registerVanillaEffects() {
        Effect.effects[NO_EFFECT] = new Effect(NO_EFFECT, "clear", "%potion.empty", 56, 93, 198);

        registerEffect(SPEED, new Effect(SPEED, EffectNames.SPEED, "%potion.moveSpeed", ENABLE_NEW_EFFECT_COLOR ? 51 : 124, ENABLE_NEW_EFFECT_COLOR ? 235 : 175, ENABLE_NEW_EFFECT_COLOR ? 255 : 198, Int2ObjectMaps.singleton(Attribute.MOVEMENT, AttributeModifiers.MOVEMENT_SPEED)));
        registerEffect(SLOWNESS, new Effect(SLOWNESS, EffectNames.SLOWNESS, "%potion.moveSlowdown", ENABLE_NEW_EFFECT_COLOR ? 139 : 90, ENABLE_NEW_EFFECT_COLOR ? 175 : 108, ENABLE_NEW_EFFECT_COLOR ? 224 : 129, true, Int2ObjectMaps.singleton(Attribute.MOVEMENT, AttributeModifiers.MOVEMENT_SLOWDOWN)));
        registerEffect(HASTE, new Effect(HASTE, EffectNames.HASTE, "%potion.digSpeed", 217, 192, 67));
        registerEffect(MINING_FATIGUE, new Effect(MINING_FATIGUE, EffectNames.MINING_FATIGUE, "%potion.digSlowDown", 74, 66, 23, true));
        registerEffect(STRENGTH, new Effect(STRENGTH, EffectNames.STRENGTH, "%potion.damageBoost", ENABLE_NEW_EFFECT_COLOR ? 255 : 147, ENABLE_NEW_EFFECT_COLOR ? 199 : 36, ENABLE_NEW_EFFECT_COLOR ? 0 : 35, Int2ObjectMaps.singleton(Attribute.ATTACK_DAMAGE, AttributeModifiers.DAMAGE_BOOST)) {
            @Override
            protected float getAttributeModifierValue(AttributeModifier modifier) {
                return 1.3f * (amplifier + 1);
            }
        });
        registerEffect(INSTANT_HEALTH, new InstantEffect(INSTANT_HEALTH, EffectNames.INSTANT_HEALTH, "%potion.heal", 248, 36, 35));
        registerEffect(INSTANT_DAMAGE, new InstantEffect(INSTANT_DAMAGE, EffectNames.INSTANT_DAMAGE, "%potion.harm", ENABLE_NEW_EFFECT_COLOR ? 169 : 67, ENABLE_NEW_EFFECT_COLOR ? 101 : 10, ENABLE_NEW_EFFECT_COLOR ? 106 : 9, true));
        registerEffect(JUMP_BOOST, new Effect(JUMP_BOOST, EffectNames.JUMP_BOOST, "%potion.jump", ENABLE_NEW_EFFECT_COLOR ? 253 : 34, 255, ENABLE_NEW_EFFECT_COLOR ? 132 : 76));
        registerEffect(NAUSEA, new Effect(NAUSEA, EffectNames.NAUSEA, "%potion.confusion", 85, 29, 74, true));
        registerEffect(REGENERATION, new Effect(REGENERATION, EffectNames.REGENERATION, "%potion.regeneration", 205, 92, 171));
        registerEffect(RESISTANCE, new Effect(RESISTANCE, EffectNames.RESISTANCE, "%potion.resistance", ENABLE_NEW_EFFECT_COLOR ? 145 : 153, ENABLE_NEW_EFFECT_COLOR ? 70 : 69, ENABLE_NEW_EFFECT_COLOR ? 240 : 58));
        registerEffect(FIRE_RESISTANCE, new Effect(FIRE_RESISTANCE, EffectNames.FIRE_RESISTANCE, "%potion.fireResistance", ENABLE_NEW_EFFECT_COLOR ? 255 : 228, ENABLE_NEW_EFFECT_COLOR ? 153 : 154, ENABLE_NEW_EFFECT_COLOR ? 0 : 58));
        registerEffect(WATER_BREATHING, new Effect(WATER_BREATHING, EffectNames.WATER_BREATHING, "%potion.waterBreathing", ENABLE_NEW_EFFECT_COLOR ? 152 : 46, ENABLE_NEW_EFFECT_COLOR ? 218 : 82, ENABLE_NEW_EFFECT_COLOR ? 192 : 153));
        registerEffect(INVISIBILITY, new Effect(INVISIBILITY, EffectNames.INVISIBILITY, "%potion.invisibility", ENABLE_NEW_EFFECT_COLOR ? 246 : 127, ENABLE_NEW_EFFECT_COLOR ? 246 : 131, ENABLE_NEW_EFFECT_COLOR ? 246 : 146));
        registerEffect(BLINDNESS, new Effect(BLINDNESS, EffectNames.BLINDNESS, "%potion.blindness", 191, 192, 192));
        registerEffect(NIGHT_VISION, new Effect(NIGHT_VISION, EffectNames.NIGHT_VISION, "%potion.nightVision", ENABLE_NEW_EFFECT_COLOR ? 194 : 0, ENABLE_NEW_EFFECT_COLOR ? 255 : 0, ENABLE_NEW_EFFECT_COLOR ? 102 : 139));
        registerEffect(HUNGER, new Effect(HUNGER, EffectNames.HUNGER, "%potion.hunger", 46, 139, 87));
        registerEffect(WEAKNESS, new Effect(WEAKNESS, EffectNames.WEAKNESS, "%potion.weakness", 72, 77, 72, true, Int2ObjectMaps.singleton(Attribute.ATTACK_DAMAGE, AttributeModifiers.WEAKNESS)) {
            @Override
            protected float getAttributeModifierValue(AttributeModifier modifier) {
                return -0.5f * (amplifier + 1);
            }
        });
        registerEffect(POISON, new Effect(POISON, EffectNames.POISON, "%potion.poison", ENABLE_NEW_EFFECT_COLOR ? 135 : 78, ENABLE_NEW_EFFECT_COLOR ? 163 : 147, ENABLE_NEW_EFFECT_COLOR ? 99 : 49, true));
        registerEffect(WITHER, new Effect(WITHER, EffectNames.WITHER, "%potion.wither", ENABLE_NEW_EFFECT_COLOR ? 115 : 53, ENABLE_NEW_EFFECT_COLOR ? 97 : 42, ENABLE_NEW_EFFECT_COLOR ? 86 : 39, true));
        registerEffect(HEALTH_BOOST, new Effect(HEALTH_BOOST, EffectNames.HEALTH_BOOST, "%potion.healthBoost", 248, 125, 35, Int2ObjectMaps.singleton(Attribute.HEALTH, AttributeModifiers.HEALTH_BOOST)));
        registerEffect(ABSORPTION, new Effect(ABSORPTION, EffectNames.ABSORPTION, "%potion.absorption", 36, 107, 251));
        registerEffect(SATURATION, new InstantEffect(SATURATION, EffectNames.SATURATION, "%potion.saturation", 255, 0, 255));
        registerEffect(LEVITATION, new Effect(LEVITATION, EffectNames.LEVITATION, "%potion.levitation", 206, 255, 255, true));
        registerEffect(FATAL_POISON, new Effect(FATAL_POISON, EffectNames.FATAL_POISON, "%potion.poison", 78, 147, 49, true));

        registerEffect(CONDUIT_POWER, new Effect(CONDUIT_POWER, EffectNames.CONDUIT_POWER, "%potion.conduitPower", 29, 194, 209), V1_5_0);

        registerEffect(SLOW_FALLING, new Effect(SLOW_FALLING, EffectNames.SLOW_FALLING, "%potion.slowFalling", ENABLE_NEW_EFFECT_COLOR ? 243 : 206, ENABLE_NEW_EFFECT_COLOR ? 207 : 255, ENABLE_NEW_EFFECT_COLOR ? 185 : 255), V1_6_0);

        registerEffect(BAD_OMEN, new Effect(BAD_OMEN, EffectNames.BAD_OMEN, "%effect.badOmen", 11, 97, 56).setVisible(false), V1_11_0);
        registerEffect(VILLAGE_HERO, new Effect(VILLAGE_HERO, EffectNames.VILLAGE_HERO, "%effect.villageHero", 68, 255, 68), V1_11_0);

        registerEffect(DARKNESS, new Effect(DARKNESS, EffectNames.DARKNESS, "%effect.darkness", 41, 39, 33).setVisible(false), V1_19_0);

        registerEffect(TRIAL_OMEN, new Effect(TRIAL_OMEN, EffectNames.TRIAL_OMEN, "%effect.trial_omen", 22, 166, 166), V1_21_0);
        registerEffect(WIND_CHARGED, new Effect(WIND_CHARGED, EffectNames.WIND_CHARGED, "%effect.wind_charged", 189, 201, 255, true), V1_21_0);
        registerEffect(WEAVING, new Effect(WEAVING, EffectNames.WEAVING, "%effect.weaving", 120, 105, 90, true), V1_21_0);
        registerEffect(OOZING, new Effect(OOZING, EffectNames.OOZING, "%effect.oozing", 153, 255, 163, true), V1_21_0);
        registerEffect(INFESTED, new Effect(INFESTED, EffectNames.INFESTED, "%effect.infested", 140, 155, 140, true), V1_21_0);
        registerEffect(RAID_OMEN, new Effect(RAID_OMEN, EffectNames.RAID_OMEN, "%effect.raid_omen", 222, 64, 88), V1_21_0);

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
//            return null;
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
