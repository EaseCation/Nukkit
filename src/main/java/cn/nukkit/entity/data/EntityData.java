package cn.nukkit.entity.data;

import cn.nukkit.item.Item;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.Objects;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class EntityData<T> {
    private int id;

    protected EntityData(int id) {
        this.id = id;
    }

    public abstract int getType();

    public abstract T getData();

    public abstract void setData(T data);

    public int getId() {
        return id;
    }

    public EntityData setId(int id) {
        this.id = id;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof EntityData)) {
            return false;
        }
        EntityData<?> data = (EntityData<?>) obj;
        return data.getType() == this.getType() && data.getId() == this.getId() && this.equalsData(data);
    }

    protected boolean equalsData(EntityData<?> data) {
        return Objects.equals(data.getData(), this.getData());
    }

    public int getDataAsByte() {
        throw new ClassCastException(getClass().getName() + " != cn.nukkit.entity.data.ByteEntityData");
    }

    public int getDataAsShort() {
        throw new ClassCastException(getClass().getName() + " != cn.nukkit.entity.data.ShortEntityData");
    }

    public int getDataAsInt() {
        throw new ClassCastException(getClass().getName() + " != cn.nukkit.entity.data.IntEntityData");
    }

    public long getDataAsLong() {
        throw new ClassCastException(getClass().getName() + " != cn.nukkit.entity.data.LongEntityData");
    }

    public float getDataAsFloat() {
        throw new ClassCastException(getClass().getName() + " != cn.nukkit.entity.data.FloatEntityData");
    }

    public String getDataAsString() {
        throw new ClassCastException(getClass().getName() + " != cn.nukkit.entity.data.StringEntityData");
    }

    public BlockVector3 getDataAsBlockPos() {
        throw new ClassCastException(getClass().getName() + " != cn.nukkit.entity.data.IntPositionEntityData");
    }

    public Vector3f getDataAsVec3() {
        throw new ClassCastException(getClass().getName() + " != cn.nukkit.entity.data.Vector3fEntityData");
    }

    public Item getDataAsItemStack() {
        throw new ClassCastException(getClass().getName() + " != cn.nukkit.entity.data.SlotEntityData");
    }

    public CompoundTag getDataAsNbt() {
        throw new ClassCastException(getClass().getName() + " != cn.nukkit.entity.data.NBTEntityData");
    }
}
