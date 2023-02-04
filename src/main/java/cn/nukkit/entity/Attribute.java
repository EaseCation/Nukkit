package cn.nukkit.entity;

import cn.nukkit.utils.ServerException;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.ToString;

import javax.annotation.Nullable;

/**
 * Attribute
 *
 * @author Box, MagicDroidX(code), PeratX @ Nukkit Project
 * @since Nukkit 1.0 | Nukkit API 1.0.0
 */
@ToString
public class Attribute implements Cloneable {

    // mob generic attributes (see Mob::_registerMobAttributes())
    public static final int HEALTH = 0;
    public static final int ABSORPTION = 1;
    public static final int KNOCKBACK_RESISTANCE = 2;
    public static final int MOVEMENT = 3;
    public static final int UNDERWATER_MOVEMENT = 4;
    public static final int LAVA_MOVEMENT = 5;
    public static final int LUCK = 6;
    public static final int FOLLOW_RANGE = 7;
    // player attributes (see Player::_registerPlayerAttributes())
    public static final int ATTACK_DAMAGE = 8;
    public static final int PLAYER_HUNGER = 9;
    public static final int PLAYER_EXHAUSTION = 10;
    public static final int PLAYER_SATURATION = 11;
    public static final int PLAYER_LEVEL = 12;
    public static final int PLAYER_EXPERIENCE = 13;
    // mob special attributes
    public static final int HORSE_JUMP_STRENGTH = 14; // see SharedAttributes::JUMP_STRENGTH
    public static final int ZOMBIE_SPAWN_REINFORCEMENTS = 15; // see Zombie::SPAWN_REINFORCEMENTS_CHANCE

    public static final int COUNT = 16;

    public static final String DEPRECATED_FALL_DAMAGE = "minecraft:fall_damage"; // see SharedAttributes::checkIsDeprecated()

    private static final Attribute[] ATTRIBUTES = new Attribute[COUNT];
    private static final Object2ObjectMap<String, Attribute> ATTRIBUTES_BY_NAME = new Object2ObjectOpenHashMap<>(COUNT);

    private final int id;
    private final String name;
    private float minValue;
    private float maxValue;
    private float defaultValue;
    private float currentValue;
    private final boolean shouldSend; //TODO: EntityType

    private Attribute(int id, String name, float minValue, float maxValue, float defaultValue, boolean shouldSend) {
        this.id = id;
        this.name = name;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.defaultValue = defaultValue;
        this.shouldSend = shouldSend;
        this.currentValue = this.defaultValue;
    }

    public static void init() {
        addAttribute(HEALTH, "minecraft:health", 0, 20, 20);
        addAttribute(ABSORPTION, "minecraft:absorption", 0, 16, 0);
        addAttribute(KNOCKBACK_RESISTANCE, "minecraft:knockback_resistance", 0, 1, 0);
        addAttribute(MOVEMENT, "minecraft:movement", 0, Float.MAX_VALUE, 0.1f);
        addAttribute(UNDERWATER_MOVEMENT, "minecraft:underwater_movement", 0, Float.MAX_VALUE, 0.02f);
        addAttribute(LAVA_MOVEMENT, "minecraft:lava_movement", 0, Float.MAX_VALUE, 0.02f);
        addAttribute(LUCK, "minecraft:luck", -1024, 1024, 0);
        addAttribute(FOLLOW_RANGE, "minecraft:follow_range", 0, 2048, 16, false);

        addAttribute(ATTACK_DAMAGE, "minecraft:attack_damage", 0, 1, 1, false);
        addAttribute(PLAYER_HUNGER, "minecraft:player.hunger", 0, 20, 20);
        addAttribute(PLAYER_EXHAUSTION, "minecraft:player.exhaustion", 0, 20, 0, false);
        addAttribute(PLAYER_SATURATION, "minecraft:player.saturation", 0, 20, 5);
        addAttribute(PLAYER_LEVEL, "minecraft:player.level", 0, 24791, 0);
        addAttribute(PLAYER_EXPERIENCE, "minecraft:player.experience", 0, 1, 0);

        addAttribute(HORSE_JUMP_STRENGTH, "minecraft:horse.jump_strength", 0, Float.MAX_VALUE, 0.7f);
        addAttribute(ZOMBIE_SPAWN_REINFORCEMENTS, "minecraft:zombie.spawn_reinforcements", 0, 2, 0);
    }

    private static Attribute addAttribute(int id, String name, float minValue, float maxValue, float defaultValue) {
        return addAttribute(id, name, minValue, maxValue, defaultValue, true);
    }

    private static Attribute addAttribute(int id, String name, float minValue, float maxValue, float defaultValue, boolean shouldSend) {
        if (minValue > maxValue || defaultValue > maxValue || defaultValue < minValue) {
            throw new IllegalArgumentException("Invalid ranges: min value: " + minValue + ", max value: " + maxValue + ", defaultValue: " + defaultValue);
        }

        Attribute attribute = new Attribute(id, name, minValue, maxValue, defaultValue, shouldSend);
        ATTRIBUTES_BY_NAME.put(name, attribute);
        ATTRIBUTES[id] = attribute;
        return attribute;
    }

    public static Attribute getAttribute(int id) {
        if (id < 0 || id >= COUNT) {
            throw new ServerException("Attribute id: " + id + " not found");
        }
        return ATTRIBUTES[id].clone();
    }

    /**
     * @param name name
     * @return null|Attribute
     */
    @Nullable
    public static Attribute getAttributeByName(String name) {
        Attribute attribute = ATTRIBUTES_BY_NAME.get(name);
        if (attribute == null) {
            return null;
        }
        return attribute.clone();
    }

    public float getMinValue() {
        return this.minValue;
    }

    public Attribute setMinValue(float minValue) {
        if (minValue > this.getMaxValue()) {
            throw new IllegalArgumentException("Value " + minValue + " is bigger than the maxValue!");
        }
        this.minValue = minValue;
        return this;
    }

    public float getMaxValue() {
        return this.maxValue;
    }

    public Attribute setMaxValue(float maxValue) {
        if (maxValue < this.getMinValue()) {
            throw new IllegalArgumentException("Value " + maxValue + " is bigger than the minValue!");
        }
        this.maxValue = maxValue;
        return this;
    }

    public float getDefaultValue() {
        return this.defaultValue;
    }

    public Attribute setDefaultValue(float defaultValue) {
        if (defaultValue > this.getMaxValue() || defaultValue < this.getMinValue()) {
            throw new IllegalArgumentException("Value " + defaultValue + " exceeds the range!");
        }
        this.defaultValue = defaultValue;
        return this;
    }

    public float getValue() {
        return this.currentValue;
    }

    public Attribute setValue(float value) {
        return setValue(value, false);
    }

    public Attribute setValue(float value, boolean fit) {
        if (value > this.getMaxValue() || value < this.getMinValue()) {
            if (!fit) {
                throw new IllegalArgumentException("Value " + value + " exceeds the range!");
            }
            value = Math.min(Math.max(value, this.getMinValue()), this.getMaxValue());
        }
        this.currentValue = value;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public int getId() {
        return this.id;
    }

    public boolean isSyncable() {
        return this.shouldSend;
    }

    @Override
    public Attribute clone() {
        try {
            return (Attribute) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
