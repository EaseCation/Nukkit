package cn.nukkit.entity.property;

import cn.nukkit.math.Mth;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.nbt.tag.Tag;
import lombok.ToString;

@ToString(callSuper = true)
public class IntEntityProperty extends EntityProperty {
    private final int minimumValue;
    private final int maximumValue;
    private final int defaultValue;

    public IntEntityProperty(String name, int minimumValue, int maximumValue) {
        this(name, minimumValue, maximumValue, minimumValue);
    }

    public IntEntityProperty(String name, int minimumValue, int maximumValue, int defaultValue) {
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
        return EntityPropertyType.INT;
    }

    @Override
    public Number load(Tag tag) {
        if (!(tag instanceof IntTag intTag)) {
            return null;
        }
        return Mth.clamp(intTag.data, getMinimumIntValue(), getMaximumIntValue());
    }

    @Override
    public void save(CompoundTag nbt, Number value) {
        nbt.putInt(getName(), value != null ? value.intValue() : getDefaultIntValue());
    }

    @Override
    public CompoundTag toNBT() {
        return new CompoundTag()
                .putString("name", getName())
                .putInt("min", minimumValue)
                .putInt("max", maximumValue)
                .putInt("type", getType().ordinal());
    }

    @Override
    public Object getDefaultData() {
        return getDefaultValue();
    }

    @Override
    public Number getMinimumNumber() {
        return getMinimumIntValue();
    }

    @Override
    public Number getMaximumNumber() {
        return getMaximumIntValue();
    }

    @Override
    public Number getDefaultNumber() {
        return getDefaultValue();
    }

    @Override
    public int getMinimumIntValue() {
        return minimumValue;
    }

    @Override
    public int getMaximumIntValue() {
        return maximumValue;
    }

    @Override
    public int getDefaultIntValue() {
        return defaultValue;
    }

    public int getDefaultValue() {
        return defaultValue;
    }
}
