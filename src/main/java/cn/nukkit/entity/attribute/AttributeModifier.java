package cn.nukkit.entity.attribute;

import cn.nukkit.entity.attribute.Attribute.Operand;
import lombok.ToString;

import java.util.UUID;

@ToString
public class AttributeModifier {
    private final UUID id;
    private final String name;
    private final float amount;
    private final Operation operation;
    private final Operand operand;
    private final boolean serializable;

    public AttributeModifier(String id, String name, float amount, Operation operation, Operand operand) {
        this(id, name, amount, operation, operand, true);
    }

    public AttributeModifier(String id, String name, float amount, Operation operation, Operand operand, boolean serializable) {
        this(UUID.fromString(id), name, amount, operation, operand, serializable);
    }

    public AttributeModifier(UUID id, String name, float amount, Operation operation, Operand operand) {
        this(id, name, amount, operation, operand, true);
    }

    public AttributeModifier(UUID id, String name, float amount, Operation operation, Operand operand, boolean serializable) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.operation = operation;
        this.operand = operand;
        this.serializable = serializable;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public float getAmount() {
        return amount;
    }

    public Operation getOperation() {
        return operation;
    }

    public Operand getOperand() {
        return operand;
    }

    public boolean isSerializable() {
        return serializable;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AttributeModifier)) {
            return false;
        }
        AttributeModifier o = (AttributeModifier) obj;
        return operand == o.operand && operation == o.operation && id.equals(o.id) && name.equals(o.name);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * Affects how the modifier value is applied to the target attribute.
     * These operations are listed in the order that they are applied in.
     */
    public enum Operation {
        /**
         * Adds the modifier value to the attribute's base value.
         */
        ADDITION,
        /**
         * Multiplies the value by (1 + x), where x is the sum of all MULTIPLY_BASE modifiers' amounts.
         * Multiple modifiers of this type have additive effects on each other.
         */
        MULTIPLY_BASE,
        /**
         * Each modifier of this type multiplies the value by (1 + x), where x is the current modifier's value.
         * Multiple modifiers of this type have multiplicative effects on each other.
         */
        MULTIPLY_TOTAL,
        /**
         * Limits the result value. If the result value is greater than the limit, it is set to the limit.
         */
        CAP,
    }
}
