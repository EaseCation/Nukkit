package cn.nukkit.entity.property;

import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import lombok.ToString;

import javax.annotation.Nullable;

@ToString
public abstract class EntityProperty {
    private final String name;
    private int index = -1;

    protected EntityProperty(String name) {
        this.name = name;
    }

    public final String getName() {
        return name;
    }

    public abstract EntityPropertyType getType();

    public abstract @Nullable Number load(Tag tag);

    public abstract void save(CompoundTag nbt, @Nullable Number value);

    public abstract CompoundTag toNBT();

    public abstract Object getDefaultData();

    public abstract Number getMinimumNumber();

    public abstract Number getMaximumNumber();

    public abstract Number getDefaultNumber();

    public int getMinimumIntValue() {
        throw new UnsupportedOperationException();
    }

    public int getMaximumIntValue() {
        throw new UnsupportedOperationException();
    }

    public int getDefaultIntValue() {
        throw new UnsupportedOperationException();
    }

    public float getMinimumFloatValue() {
        throw new UnsupportedOperationException();
    }

    public float getMaximumFloatValue() {
        throw new UnsupportedOperationException();
    }

    public float getDefaultFloatValue() {
        throw new UnsupportedOperationException();
    }

    public final int getIndex() {
        return index;
    }

    public final void setIndex(int index) {
        this.index = index;
    }
}
