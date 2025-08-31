package cn.nukkit.entity;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class EntityCreature extends EntityLiving {
    public EntityCreature(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        if (namedTag.contains("IsBaby")) {
            setDataFlag(DATA_FLAG_BABY, namedTag.getBoolean("IsBaby"));
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        namedTag.putBoolean("IsBaby", getDataFlag(DATA_FLAG_BABY));
    }
}
