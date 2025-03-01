package cn.nukkit.entity.property;

import cn.nukkit.nbt.tag.ByteTag;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import lombok.ToString;

@ToString(callSuper = true)
public class BooleanEntityProperty extends EntityProperty {
    private final boolean defaultValue;

    public BooleanEntityProperty(String name) {
        this(name, false);
    }

    public BooleanEntityProperty(String name, boolean defaultValue) {
        super(name);
        this.defaultValue = defaultValue;
    }

    @Override
    public EntityPropertyType getType() {
        return EntityPropertyType.BOOL;
    }

    @Override
    public Number load(Tag tag) {
        if (!(tag instanceof ByteTag byteTag)) {
            return null;
        }
        return byteTag.data == 0 ? 0 : 1;
    }

    @Override
    public void save(CompoundTag nbt, Number value) {
        nbt.putBoolean(getName(), value != null ? value.intValue() != 0 : getDefaultValue());
    }

    @Override
    public CompoundTag toNBT() {
        return new CompoundTag()
                .putString("name", getName())
                .putInt("type", getType().ordinal());
    }

    @Override
    public Object getDefaultData() {
        return getDefaultValue();
    }

    @Override
    public Number getMinimumNumber() {
        return 0;
    }

    @Override
    public Number getMaximumNumber() {
        return 1;
    }

    @Override
    public Number getDefaultNumber() {
        return getDefaultIntValue();
    }

    @Override
    public int getMinimumIntValue() {
        return 0;
    }

    @Override
    public int getMaximumIntValue() {
        return 1;
    }

    @Override
    public int getDefaultIntValue() {
        return defaultValue ? 1 : 0;
    }

    public boolean getDefaultValue() {
        return defaultValue;
    }
}
