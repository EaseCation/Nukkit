package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Mth;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * Created by Snake1999 on 2016/2/3.
 * Package cn.nukkit.blockentity in project Nukkit.
 */
public class BlockEntitySkull extends BlockEntitySpawnable {
    public BlockEntitySkull(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        if (!namedTag.contains("SkullType")) {
            namedTag.putByte("SkullType", 0);
        }
        if (!namedTag.contains("Rotation")) {
            if (namedTag.contains("Rot")) {
                namedTag.putFloat("Rotation", Mth.wrapDegrees(namedTag.getByte("Rot") * 360 / 16));
                namedTag.remove("Rot");
            } else {
                namedTag.putFloat("Rotation", 0);
            }
        }
        if (!namedTag.contains("MouthMoving")) {
            namedTag.putBoolean("MouthMoving", false);
        }
        if (!namedTag.contains("MouthTickCount")) {
            namedTag.putInt("MouthTickCount", 0);
        }

        super.initBlockEntity();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.remove("Creator");
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == Block.BLOCK_SKULL;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return getDefaultCompound(this, SKULL)
                .putByte("SkullType", this.namedTag.getByte("SkullType"))
                .putFloat("Rotation", namedTag.getFloat("Rotation"))
                .putBoolean("MouthMoving", namedTag.getBoolean("MouthMoving"))
                .putInt("MouthTickCount", namedTag.getInt("MouthTickCount"));
    }

}