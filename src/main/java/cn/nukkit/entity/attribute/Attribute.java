package cn.nukkit.entity.attribute;

import cn.nukkit.entity.attribute.AttributeModifier.Operation;
import cn.nukkit.math.Mth;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import lombok.ToString;

import javax.annotation.Nullable;
import java.util.*;
import java.util.Map.Entry;

/**
 * Attribute
 *
 * @author Box, MagicDroidX(code), PeratX @ Nukkit Project
 * @since Nukkit 1.0 | Nukkit API 1.0.0
 */
@ToString
public class Attribute implements AttributeID {

    private final int id;
    private final String name;
    private float defaultMinValue;
    private float minValue;
    private float defaultMaxValue;
    private float maxValue;
    private float defaultValue;
    private float currentValue;

    private final EnumMap<Operation, Set<AttributeModifier>> modifiersByOperation;
    private final Object2ObjectArrayMap<UUID, AttributeModifier> modifiersById;

    private final float[] calculatedValues;
    private boolean dirty = true;

    private final RedefinitionMode redefinitionMode;
    private final boolean shouldSend; //TODO: EntityType

    Attribute(int id, String name, float minValue, float maxValue, float defaultValue, RedefinitionMode redefinitionMode, boolean shouldSend) {
        this.id = id;
        this.name = name;
        this.defaultMinValue = minValue;
        this.minValue = minValue;
        this.defaultMaxValue = maxValue;
        this.maxValue = maxValue;
        this.defaultValue = defaultValue;
        this.redefinitionMode = redefinitionMode;
        this.shouldSend = shouldSend;
        this.currentValue = this.defaultValue;
        this.modifiersByOperation = new EnumMap<>(Operation.class);
        this.modifiersById = new Object2ObjectArrayMap<>();
        this.calculatedValues = new float[3];
    }

    private Attribute(int id, String name, float defaultMinValue, float minValue, float defaultMaxValue, float maxValue, float defaultValue, float currentValue, RedefinitionMode redefinitionMode, boolean shouldSend,
                      EnumMap<Operation, Set<AttributeModifier>> modifiersByOperation, Object2ObjectArrayMap<UUID, AttributeModifier> modifiersById, float[] calculatedValues, boolean dirty) {
        this.id = id;
        this.name = name;
        this.defaultMinValue = defaultMinValue;
        this.minValue = minValue;
        this.defaultMaxValue = defaultMaxValue;
        this.maxValue = maxValue;
        this.defaultValue = defaultValue;
        this.currentValue = currentValue;
        this.redefinitionMode = redefinitionMode;
        this.shouldSend = shouldSend;
        this.modifiersByOperation = modifiersByOperation;
        this.modifiersById = modifiersById;
        this.calculatedValues = calculatedValues;
        this.dirty = dirty;
    }

    public static void init() {
        Attributes.registerVanillaAttributes();
        AttributeModifiers.initializeVanillaAttributeModifiers();
    }

    public static Attribute getAttribute(int id) {
        return Attributes.getAttribute(id);
    }

    /**
     * @param name name
     * @return null|Attribute
     */
    @Nullable
    public static Attribute getAttributeByName(String name) {
        return Attributes.getAttribute(name);
    }

    public float getDefaultMinValue() {
        return this.defaultMinValue;
    }

    public Attribute setDefaultMinValue(float defaultMinValue) {
        if (defaultMinValue > this.getDefaultMaxValue()) {
            throw new IllegalArgumentException("Value " + defaultMinValue + " is bigger than the maxValue!");
        }
        this.defaultMinValue = defaultMinValue;
        return this;
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

    public float getDefaultMaxValue() {
        return this.defaultMaxValue;
    }

    public Attribute setDefaultMaxValue(float defaultMaxValue) {
        if (defaultMaxValue < this.getDefaultMinValue()) {
            throw new IllegalArgumentException("Value " + defaultMaxValue + " is bigger than the minValue!");
        }
        this.defaultMaxValue = defaultMaxValue;
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
        if (defaultValue > this.getDefaultMaxValue() || defaultValue < this.getDefaultMinValue()) {
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

    public RedefinitionMode getRedefinitionMode() {
        return this.redefinitionMode;
    }

    public boolean isSyncable() {
        return this.shouldSend;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty() {
        dirty = true;
    }

    public Attribute copy() {
        EnumMap<Operation, Set<AttributeModifier>> modifiersByOperation = new EnumMap<>(Operation.class);
        for (Entry<Operation, Set<AttributeModifier>> entry : this.modifiersByOperation.entrySet()) {
            Set<AttributeModifier> modifiers = entry.getValue();
            if (!modifiers.isEmpty()) {
                modifiersByOperation.put(entry.getKey(), new HashSet<>(modifiers));
            }
        }
        return new Attribute(id, name, defaultMinValue, minValue, defaultMaxValue, maxValue, defaultValue, currentValue, redefinitionMode, shouldSend,
                modifiersByOperation, modifiersById.clone(), calculatedValues.clone(), dirty);
    }

    @Nullable
    public AttributeModifier getModifier(UUID modifierId) {
        return modifiersById.get(modifierId);
    }

    public void addModifier(AttributeModifier modifier) {
        if (!modifiersByOperation.computeIfAbsent(modifier.getOperation(), op -> new HashSet<>()).add(modifier)) {
            return;
        }
        modifiersById.put(modifier.getId(), modifier);

        setDirty();
    }

    public void replaceModifier(AttributeModifier modifier) {
        AttributeModifier old = modifiersById.put(modifier.getId(), modifier);
        if (old != null) {
            if (modifier.getAmount() == old.getAmount() && modifier.equals(old)) {
                return;
            }

            Set<AttributeModifier> modifiers = modifiersByOperation.get(old.getOperation());
            if (modifiers != null) {
                modifiers.remove(old);
            }
        }

        modifiersByOperation.computeIfAbsent(modifier.getOperation(), op -> new HashSet<>()).add(modifier);

        setDirty();
    }

    public boolean removeModifier(UUID modifierId) {
        AttributeModifier modifier = modifiersById.remove(modifierId);
        if (modifier == null) {
            return false;
        }

        Set<AttributeModifier> modifiers =  modifiersByOperation.get(modifier.getOperation());
        if (modifiers != null) {
            modifiers.removeIf(mod -> modifierId.equals(mod.getId()));
        }

        setDirty();
        return true;
    }

    public Map<UUID, AttributeModifier> getModifiers() {
        return modifiersById;
    }

    public Set<AttributeModifier> getModifiers(Operation operation) {
        return modifiersByOperation.getOrDefault(operation, Collections.emptySet());
    }

    public float getModifiedValue() {
        if (dirty) {
            float value = this.calculateValue();
            calculatedValues[Operand.CURRENT.ordinal()] = value;
            dirty = false;
            return value;
        }

        return calculatedValues[Operand.CURRENT.ordinal()];
    }

    private float calculateValue() {
        calculatedValues[Operand.MIN.ordinal()] = minValue;
        calculatedValues[Operand.MAX.ordinal()] = maxValue;
        calculatedValues[Operand.CURRENT.ordinal()] = currentValue;

        for (AttributeModifier modifier : getModifiers(Operation.ADDITION)) {
            calculatedValues[modifier.getOperand().ordinal()] += modifier.getAmount();
        }

        float[] baseValues = calculatedValues.clone();
        for (AttributeModifier modifier : getModifiers(Operation.MULTIPLY_BASE)) {
            int operand = modifier.getOperand().ordinal();
            calculatedValues[operand] += baseValues[operand] * modifier.getAmount();
        }

        for (AttributeModifier modifier : getModifiers(Operation.MULTIPLY_TOTAL)) {
            calculatedValues[modifier.getOperand().ordinal()] *= 1 + modifier.getAmount();
        }

        return sanitizeValue(calculatedValues[Operand.CURRENT.ordinal()]);
    }

    private float sanitizeValue(float value) {
        float modifiedMax = calculatedValues[Operand.MAX.ordinal()];

        for (AttributeModifier modifier : getModifiers(Operation.CAP)) {
            float cap = modifier.getAmount();
            if (modifiedMax > cap) {
                modifiedMax = cap;
            }
        }

        return Mth.clamp(value, calculatedValues[Operand.MIN.ordinal()], modifiedMax);
    }

    /**
     * Affects which parameter of the target attribute is modified.
     */
    public enum Operand {
        MIN,
        MAX,
        CURRENT,
    }

    public enum RedefinitionMode {
        KEEP_CURRENT,
        UPDATE_TO_NEW_DEFAULT,
    }
}
