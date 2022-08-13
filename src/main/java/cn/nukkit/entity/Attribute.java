package cn.nukkit.entity;

import cn.nukkit.utils.ServerException;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.ToString;

import java.util.Objects;

/**
 * Attribute
 *
 * @author Box, MagicDroidX(code), PeratX @ Nukkit Project
 * @since Nukkit 1.0 | Nukkit API 1.0.0
 */
@ToString
public class Attribute implements Cloneable {

    public static final int ABSORPTION = 0;
    public static final int PLAYER_SATURATION = 1;
    public static final int PLAYER_EXHAUSTION = 2;
    public static final int KNOCKBACK_RESISTANCE = 3;
    public static final int HEALTH = 4;
    public static final int MOVEMENT = 5;
    public static final int FOLLOW_RANGE = 6;
    public static final int PLAYER_HUNGER = 7;
    public static final int ATTACK_DAMAGE = 8;
    public static final int PLAYER_LEVEL = 9;
    public static final int PLAYER_EXPERIENCE = 10;
    public static final int LUCK = 11;
    public static final int UNDERWATER_MOVEMENT = 12;
    public static final int LAVA_MOVEMENT = 13;
    public static final int HORSE_JUMP_STRENGTH = 14;

    protected static Int2ObjectMap<Attribute> attributes = new Int2ObjectOpenHashMap<>();

    protected float minValue;
    protected float maxValue;
    protected float defaultValue;
    protected float currentValue;
    protected String name;
    protected boolean shouldSend; //TODO: EntityType
    private final int id;

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
        addAttribute(ABSORPTION, "minecraft:absorption", 0.00f, 16, 0.00f);
        addAttribute(PLAYER_SATURATION, "minecraft:player.saturation", 0.00f, 20.00f, 5.00f);
        addAttribute(PLAYER_EXHAUSTION, "minecraft:player.exhaustion", 0.00f, 20.00f, 0);
        addAttribute(KNOCKBACK_RESISTANCE, "minecraft:knockback_resistance", 0.00f, 1.00f, 0.00f);
        addAttribute(HEALTH, "minecraft:health", 0.00f, 20.00f, 20.00f);
        addAttribute(MOVEMENT, "minecraft:movement", 0.00f, 3.4028235E38f, 0.10f);
        addAttribute(FOLLOW_RANGE, "minecraft:follow_range", 0.00f, 2048.00f, 16.00f);
        addAttribute(PLAYER_HUNGER, "minecraft:player.hunger", 0.00f, 20.00f, 20.00f);
        addAttribute(ATTACK_DAMAGE, "minecraft:attack_damage", 0.00f, 1, 1.00f);
        addAttribute(PLAYER_LEVEL, "minecraft:player.level", 0.00f, 24791.00f, 0.00f);
        addAttribute(PLAYER_EXPERIENCE, "minecraft:player.experience", 0.00f, 1.00f, 0.00f);
        addAttribute(LUCK, "minecraft:luck", -1024, 1024, 0);
        addAttribute(UNDERWATER_MOVEMENT, "minecraft:underwater_movement", 0, 3.4028235E38f, 0.02f);
        addAttribute(LAVA_MOVEMENT, "minecraft:lava_movement", 0, 3.4028235E38f, 0.02f);
        addAttribute(HORSE_JUMP_STRENGTH, "minecraft:horse.jump_strength", 0.0f, 3.4028235E38f, 0.7f);
    }

    public static Attribute addAttribute(int id, String name, float minValue, float maxValue, float defaultValue) {
        return addAttribute(id, name, minValue, maxValue, defaultValue, true);
    }

    public static Attribute addAttribute(int id, String name, float minValue, float maxValue, float defaultValue, boolean shouldSend) {
        if (minValue > maxValue || defaultValue > maxValue || defaultValue < minValue) {
            throw new IllegalArgumentException("Invalid ranges: min value: " + minValue + ", max value: " + maxValue + ", defaultValue: " + defaultValue);
        }

        return attributes.put(id, new Attribute(id, name, minValue, maxValue, defaultValue, shouldSend));
    }

    public static Attribute getAttribute(int id) {
        if (attributes.containsKey(id)) {
            return attributes.get(id).clone();
        }
        throw new ServerException("Attribute id: " + id + " not found");
    }

    /**
     * @param name name
     * @return null|Attribute
     */
    public static Attribute getAttributeByName(String name) {
        for (Attribute a : attributes.values()) {
            if (Objects.equals(a.getName(), name)) {
                return a.clone();
            }
        }
        return null;
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