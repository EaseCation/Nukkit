package cn.nukkit.entity.property;

import cn.nukkit.math.Mth;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.Tag;
import lombok.ToString;

@ToString(callSuper = true)
public class FloatEntityProperty extends EntityProperty {
    private final float minimumValue;
    private final float maximumValue;
    private final float defaultValue;

    public FloatEntityProperty(String name, float minimumValue, float maximumValue) {
        this(name, minimumValue, maximumValue, minimumValue);
    }

    public FloatEntityProperty(String name, float minimumValue, float maximumValue, float defaultValue) {
        super(name);
        if (defaultValue < minimumValue || defaultValue > maximumValue) {
            throw new IllegalArgumentException("Invalid default value: " + defaultValue);
        }
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
        this.defaultValue = defaultValue;
    }

    @Override
    public EntityPropertyType getType() {
        return EntityPropertyType.FLOAT;
    }

    @Override
    public Number load(Tag tag) {
        if (!(tag instanceof FloatTag floatTag)) {
            return null;
        }
        return Mth.clamp(floatTag.data, getMinimumFloatValue(), getMaximumFloatValue());
    }

    @Override
    public void save(CompoundTag nbt, Number value) {
        nbt.putFloat(getName(), value != null ? value.floatValue() : getDefaultFloatValue());
    }

    @Override
    public CompoundTag toNBT() {
        return new CompoundTag()
                .putString("name", getName())
                .putFloat("min", minimumValue)
                .putFloat("max", maximumValue)
                .putInt("type", getType().ordinal());
    }

    @Override
    public Object getDefaultData() {
        return getDefaultValue();
    }

    @Override
    public Number getMinimumNumber() {
        return getMinimumFloatValue();
    }

    @Override
    public Number getMaximumNumber() {
        return  getMaximumFloatValue();
    }

    @Override
    public Number getDefaultNumber() {
        return getDefaultValue();
    }

    @Override
    public float getMinimumFloatValue() {
        return minimumValue;
    }

    @Override
    public float getMaximumFloatValue() {
        return maximumValue;
    }

    @Override
    public float getDefaultFloatValue() {
        return defaultValue;
    }

    public float getDefaultValue() {
        return defaultValue;
    }
}
