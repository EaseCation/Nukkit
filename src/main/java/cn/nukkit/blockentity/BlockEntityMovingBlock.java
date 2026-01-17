package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockSerializer;
import cn.nukkit.block.Blocks;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by CreeperFace on 11.4.2017.
 */
public class BlockEntityMovingBlock extends BlockEntitySpawnable {

    protected Block block;
    @Nullable
    protected Block blockExtra;

    protected BlockVector3 piston;

    private boolean expanding;

    public BlockEntityMovingBlock(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getTypeId() {
        return BlockEntityType.MOVING_BLOCK;
    }

    @Override
    protected void initBlockEntity() {
        CompoundTag movingBlock = this.namedTag.getCompound("movingBlock", null);
        if (movingBlock != null) {
            this.block = NBTIO.getBlockHelper(movingBlock);
        } else {
//            this.close();
            this.block = Blocks.air();
        }

        CompoundTag movingBlockExtra = this.namedTag.getCompound("movingBlockExtra", null);
        if (movingBlockExtra != null) {
            this.blockExtra = NBTIO.getBlockHelper(movingBlockExtra);
        } else {
            this.blockExtra = null;
        }

        if (namedTag.contains("pistonPosX") && namedTag.contains("pistonPosY") && namedTag.contains("pistonPosZ")) {
            this.piston = new BlockVector3(namedTag.getInt("pistonPosX"), namedTag.getInt("pistonPosY"), namedTag.getInt("pistonPosZ"));
        } else {
            this.piston = asBlockVector3();
        }

        expanding = namedTag.getBoolean("expanding");

        super.initBlockEntity();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        if (block != null) {
            namedTag.putCompound("movingBlock", BlockSerializer.serialize(block));
        } else {
            namedTag.remove("movingBlock");
        }

        if (blockExtra != null) {
            namedTag.putCompound("movingBlockExtra", BlockSerializer.serialize(blockExtra));
        } else {
            namedTag.remove("movingBlockExtra");
        }

        namedTag.putBoolean("expanding", expanding);
    }

    @Nullable
    public CompoundTag getBlockEntity() {
        if (this.namedTag.contains("movingEntity")) {
            return this.namedTag.getCompound("movingEntity");
        }

        return null;
    }

    public Block getMovingBlock() {
        return this.block;
    }

    @Nullable
    public Block getMovingBlockExtra() {
        return this.blockExtra;
    }

    public void moveCollidedEntities(BlockEntityPistonArm piston, BlockFace moveDirection) {
        AxisAlignedBB[] bbs = block.getCollisionShape();
        if (bbs == null) {
            return;
        }

        Set<Entity> entities = new HashSet<>();

        for (AxisAlignedBB bb : bbs) {
            bb = bb.getOffsetBoundingBox(
                    this.x + (piston.progress * moveDirection.getXOffset()) - moveDirection.getXOffset(),
                    this.y + (piston.progress * moveDirection.getYOffset()) - moveDirection.getYOffset(),
                    this.z + (piston.progress * moveDirection.getZOffset()) - moveDirection.getZOffset()
            );

            Collections.addAll(entities, this.level.getCollidingEntities(bb));
        }

        for (Entity entity : entities) {
            piston.moveEntity(entity, moveDirection);
        }
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == BlockID.MOVING_BLOCK;
    }

    @Override
    public CompoundTag getSpawnCompound(boolean chunkData) {
        CompoundTag tag = getDefaultCompound(this, MOVING_BLOCK)
                .putInt("pistonPosX", this.piston.getX())
                .putInt("pistonPosY", this.piston.getY())
                .putInt("pistonPosZ", this.piston.getZ())
                .putBoolean("expanding", expanding);

        if (block != null) {
            tag.putCompound("movingBlock", BlockSerializer.serialize(block));
        }
        if (blockExtra != null) {
            tag.putCompound("movingBlockExtra", BlockSerializer.serialize(blockExtra));
        }
/*
        CompoundTag blockEntity = namedTag.getCompound("movingEntity", null);
        if (blockEntity != null) {
            tag.putCompound("movingEntity", blockEntity.getSpawnCompound());
        }
*/
        return tag;
    }
}
