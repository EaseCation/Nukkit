package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.nbt.tag.CompoundTag;

import javax.annotation.Nullable;

/**
 * Created by CreeperFace on 11.4.2017.
 */
public class BlockEntityMovingBlock extends BlockEntitySpawnable {

    protected Block block;
    protected Block blockExtra;

    protected BlockVector3 piston;

    public BlockEntityMovingBlock(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        if (namedTag.contains("movingBlock")) {
            CompoundTag blockData = namedTag.getCompound("movingBlock");
            this.block = Block.get(blockData.getInt("id"), blockData.getInt("meta"));
        } else {
            this.close();
        }

        if (this.namedTag.contains("movingBlockExtra")) {
            CompoundTag blockState = this.namedTag.getCompound("movingBlockExtra");
            this.blockExtra = Block.get(blockState.getInt("id"), blockState.getInt("meta"));
        } else {
            this.namedTag.put("movingBlockExtra", new CompoundTag()
                    .putString("name", "minecraft:air")
                    .putShort("val", 0)
                    .putInt("id", BlockID.AIR) // only for Nukkit purpose
                    .putInt("meta", 0)); // only for Nukkit purpose
            this.blockExtra = Block.get(BlockID.AIR);
        }

        if (namedTag.contains("pistonPosX") && namedTag.contains("pistonPosY") && namedTag.contains("pistonPosZ")) {
            this.piston = new BlockVector3(namedTag.getInt("pistonPosX"), namedTag.getInt("pistonPosY"), namedTag.getInt("pistonPosZ"));
        } else {
            this.piston = new BlockVector3(0, -1, 0);
        }

        super.initBlockEntity();
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

    public Block getMovingBlockExtra() {
        return this.blockExtra;
    }

    public void moveCollidedEntities(BlockEntityPistonArm piston, BlockFace moveDirection) {
        AxisAlignedBB bb = block.getBoundingBox();

        if (bb == null) {
            return;
        }

        bb = bb.getOffsetBoundingBox(
                this.x + (piston.progress * moveDirection.getXOffset()) - moveDirection.getXOffset(),
                this.y + (piston.progress * moveDirection.getYOffset()) - moveDirection.getYOffset(),
                this.z + (piston.progress * moveDirection.getZOffset()) - moveDirection.getZOffset()
        );

        Entity[] entities = this.level.getCollidingEntities(bb);

        for (Entity entity : entities) {
            piston.moveEntity(entity, moveDirection);
        }
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == BlockID.MOVING_BLOCK;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag movingBlock = this.namedTag.getCompound("movingBlock");
        CompoundTag movingBlockExtra = this.namedTag.getCompound("movingBlockExtra");

        return getDefaultCompound(this, MOVING_BLOCK)
                .putCompound("movingBlock", new CompoundTag()
                        .putString("name", movingBlock.getString("name"))
                        .putShort("val", movingBlock.getShort("val")))
                .putCompound("movingBlockExtra", new CompoundTag()
                        .putString("name", movingBlockExtra.getString("name"))
                        .putShort("val", movingBlockExtra.getShort("val")))
                .putInt("pistonPosX", this.piston.getX())
                .putInt("pistonPosY", this.piston.getY())
                .putInt("pistonPosZ", this.piston.getZ());
    }
}
