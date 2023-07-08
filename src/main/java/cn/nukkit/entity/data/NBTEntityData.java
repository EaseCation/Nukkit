package cn.nukkit.entity.data;

import cn.nukkit.entity.Entity;
import cn.nukkit.nbt.tag.CompoundTag;
import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class NBTEntityData extends EntityData<CompoundTag> {
    public CompoundTag tag;

    public NBTEntityData(int id, CompoundTag tag) {
        super(id);
        this.tag = tag;
    }

    @Override
    public CompoundTag getData() {
        return this.tag;
    }

    @Override
    public CompoundTag getDataAsNbt() {
        return getData();
    }

    @Override
    public void setData(CompoundTag tag) {
        if (tag != null) {
            this.tag = tag;
        } else {
            this.tag = new CompoundTag();
        }
    }

    @Override
    public int getType() {
        return Entity.DATA_TYPE_NBT;
    }
}
