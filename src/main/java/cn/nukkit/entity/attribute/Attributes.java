package cn.nukkit.entity.attribute;

import cn.nukkit.entity.attribute.Attribute.RedefinitionMode;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import javax.annotation.Nullable;

import static cn.nukkit.entity.attribute.AttributeID.*;

public final class Attributes {
    public static final String DEPRECATED_FALL_DAMAGE = "minecraft:fall_damage"; // see SharedAttributes::checkIsDeprecated()

    private static final Attribute[] ATTRIBUTE_BY_ID = new Attribute[COUNT];
    private static final Object2ObjectMap<String, Attribute> ATTRIBUTE_BY_NAME = new Object2ObjectOpenHashMap<>(COUNT);

    public static void registerVanillaAttributes() {
        registerAttribute(HEALTH, "minecraft:health", 0, 20, 20, RedefinitionMode.KEEP_CURRENT);
        registerAttribute(ABSORPTION, "minecraft:absorption", 0, 16, 0, RedefinitionMode.UPDATE_TO_NEW_DEFAULT);
        registerAttribute(KNOCKBACK_RESISTANCE, "minecraft:knockback_resistance", 0, 1, 0, RedefinitionMode.UPDATE_TO_NEW_DEFAULT, false);
        registerAttribute(MOVEMENT, "minecraft:movement", 0, Float.MAX_VALUE, 0.1f, RedefinitionMode.UPDATE_TO_NEW_DEFAULT);
        registerAttribute(UNDERWATER_MOVEMENT, "minecraft:underwater_movement", 0, Float.MAX_VALUE, 0.02f, RedefinitionMode.UPDATE_TO_NEW_DEFAULT);
        registerAttribute(LAVA_MOVEMENT, "minecraft:lava_movement", 0, Float.MAX_VALUE, 0.02f, RedefinitionMode.UPDATE_TO_NEW_DEFAULT);
        registerAttribute(LUCK, "minecraft:luck", -1024, 1024, 0, RedefinitionMode.UPDATE_TO_NEW_DEFAULT);
        registerAttribute(FOLLOW_RANGE, "minecraft:follow_range", 0, 2048, 16, RedefinitionMode.UPDATE_TO_NEW_DEFAULT, false);

        registerAttribute(ATTACK_DAMAGE, "minecraft:attack_damage", 0, 1, 1, RedefinitionMode.UPDATE_TO_NEW_DEFAULT, false);
        registerAttribute(PLAYER_HUNGER, "minecraft:player.hunger", 0, 20, 20, RedefinitionMode.KEEP_CURRENT);
        registerAttribute(PLAYER_EXHAUSTION, "minecraft:player.exhaustion", 0, 20, 0, RedefinitionMode.KEEP_CURRENT);
        registerAttribute(PLAYER_SATURATION, "minecraft:player.saturation", 0, 20, 5, RedefinitionMode.KEEP_CURRENT);
        registerAttribute(PLAYER_LEVEL, "minecraft:player.level", 0, 24791, 0, RedefinitionMode.KEEP_CURRENT);
        registerAttribute(PLAYER_EXPERIENCE, "minecraft:player.experience", 0, 1, 0, RedefinitionMode.KEEP_CURRENT);

        registerAttribute(HORSE_JUMP_STRENGTH, "minecraft:horse.jump_strength", 0, Float.MAX_VALUE, 0.7f, RedefinitionMode.UPDATE_TO_NEW_DEFAULT);
        registerAttribute(ZOMBIE_SPAWN_REINFORCEMENTS, "minecraft:zombie.spawn_reinforcements", 0, 2, 0, RedefinitionMode.UPDATE_TO_NEW_DEFAULT, false);
    }

    private static Attribute registerAttribute(int id, String name, float minValue, float maxValue, float defaultValue, RedefinitionMode redefinitionMode) {
        return registerAttribute(id, name, minValue, maxValue, defaultValue, redefinitionMode, true);
    }

    private static Attribute registerAttribute(int id, String name, float minValue, float maxValue, float defaultValue, RedefinitionMode redefinitionMode, boolean syncable) {
        if (minValue > maxValue || defaultValue > maxValue || defaultValue < minValue) {
            throw new IllegalArgumentException("Invalid ranges: min value: " + minValue + ", max value: " + maxValue + ", defaultValue: " + defaultValue);
        }

        Attribute attribute = new Attribute(id, name, minValue, maxValue, defaultValue, redefinitionMode, syncable);
        ATTRIBUTE_BY_NAME.put(name, attribute);
        ATTRIBUTE_BY_ID[id] = attribute;
        return attribute;
    }

    @Nullable
    public static Attribute getAttribute(int id) {
        if (id < 0 || id >= COUNT) {
            return null;
        }
        return ATTRIBUTE_BY_ID[id].copy();
    }

    @Nullable
    public static Attribute getAttributeUnsafe(int id) {
        if (id < 0 || id >= COUNT) {
            return null;
        }
        return ATTRIBUTE_BY_ID[id];
    }

    @Nullable
    public static Attribute getAttribute(String name) {
        Attribute attribute = ATTRIBUTE_BY_NAME.get(name);
        if (attribute == null) {
            return null;
        }
        return attribute.copy();
    }

    @Nullable
    public static Attribute getAttributeUnsafe(String name) {
        return ATTRIBUTE_BY_NAME.get(name);
    }

    private Attributes() {
        throw new IllegalStateException();
    }
}
