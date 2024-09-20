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
        if (!namedTag.contains("DoingAnimation")) {
            namedTag.putBoolean("DoingAnimation", false);
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
        return blockId == Block.BLOCK_SKULL
                //TODO: 1.21.40+ flatten mv -- 09/07/2024
                ||  blockId == Block.WITHER_SKELETON_SKULL
                ||  blockId == Block.ZOMBIE_HEAD
                ||  blockId == Block.PLAYER_HEAD
                ||  blockId == Block.CREEPER_HEAD
                ||  blockId == Block.DRAGON_HEAD
                ||  blockId == Block.PIGLIN_HEAD
                ;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return getDefaultCompound(this, SKULL)
                .putByte("SkullType", this.namedTag.getByte("SkullType"))
                .putFloat("Rotation", namedTag.getFloat("Rotation"))
                .putBoolean("MouthMoving", namedTag.getBoolean("MouthMoving"))
                .putInt("MouthTickCount", namedTag.getInt("MouthTickCount"))
                .putBoolean("DoingAnimation", namedTag.getBoolean("DoingAnimation"));
    }

}