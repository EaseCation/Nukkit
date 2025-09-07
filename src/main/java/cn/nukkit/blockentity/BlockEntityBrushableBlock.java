package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockBrushable;
import cn.nukkit.block.Blocks;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

import javax.annotation.Nullable;

public class BlockEntityBrushableBlock extends BlockEntitySpawnable {
    private String type;

    private int brushCount;
    @Nullable
    private BlockFace brushDirection;

    @Nullable
    private Item item;

    private int resetTick;
    private int cooldownTick;

    public BlockEntityBrushableBlock(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getTypeId() {
        return BlockEntityType.BRUSHABLE_BLOCK;
    }

    @Override
    protected void initBlockEntity() {
        if (namedTag.contains("type")) {
            type = namedTag.getString("type");
        } else {
            type = Blocks.getBlockFullNameById(getBlock().getId());
            namedTag.putString("type", type);
        }

        brushCount = namedTag.getInt("brush_count");

        int brushDirection = namedTag.getByte("brush_direction", 6);
        if (brushDirection >= 0 && brushDirection < 6) {
            this.brushDirection = BlockFace.fromIndex(brushDirection);
        } else {
            this.brushDirection = null;
        }

        super.initBlockEntity();

        if (brushCount > 0) {
            scheduleUpdate();
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        namedTag.putString("type", type);

        namedTag.putInt("brush_count", brushCount);
        namedTag.putByte("brush_direction", brushDirection == null ? 6 : brushDirection.getIndex());

        if (hasItem()) {
            namedTag.putCompound("item", NBTIO.putItemHelper(item));
        } else {
            namedTag.remove("item");
        }
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == Block.SUSPICIOUS_SAND || blockId == Block.SUSPICIOUS_GRAVEL;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag nbt = getDefaultCompound(this, BRUSHABLE_BLOCK)
                .putString("type", type)
                .putInt("brush_count", brushCount)
                .putByte("brush_direction", brushDirection == null ? 6 : brushDirection.getIndex());

        if (hasItem()) {
            nbt.putCompound("item", NBTIO.putItemHelper(item));
        }

        return nbt;
    }

    @Override
    public boolean onUpdate() {
        if (isClosed()) {
            return false;
        }

        int currentTick = server.getTick();
        int tickDiff = currentTick - lastUpdate;
        if (tickDiff <= 0) {
            return true;
        }
        lastUpdate = currentTick;

        if (brushCount > 0 && currentTick >= resetTick) {
            int oldProgress = getProgress();
            brushCount = Math.max(0, brushCount - 2);
            int newProgress = getProgress();
            if (newProgress != oldProgress && getBlock() instanceof BlockBrushable block) {
                block.setBrushedProgress(newProgress);
                level.setBlock(block, block, true);
            }

            resetTick = currentTick + 4;
        }

        if (brushCount > 0) {
            return true;
        }

        brushDirection = null;
        resetTick = 0;
        cooldownTick = 0;
        return false;
    }

    public boolean brush(BlockBrushable block, BlockFace face) {
        brushDirection = face;

        int time = server.getTick();
        resetTick = time + 40;

        if (time < cooldownTick) {
            return false;
        }
        cooldownTick = time + 10;

        Vector3 center = blockCenter();
        int blockFullId = block.getFullId();
        level.addLevelSoundEvent(center, LevelSoundEventPacket.SOUND_BRUSH, blockFullId);

        int oldProgress = block.getBrushedProgress();
        if (++brushCount >= 10) {
            if (item != null) {
                level.dropItem(block.getSideVec(face).blockCenter(), item);
                item = null;
            }

            level.addLevelSoundEvent(center, LevelSoundEventPacket.SOUND_BRUSH_COMPLETED, blockFullId);
            level.addLevelEvent(block, LevelEventPacket.EVENT_PARTICLE_DESTROY_BLOCK_NO_SOUND, blockFullId);

            level.setBlock(block, Block.get(block.getBrushedBlockId()), true);
            return true;
        }

        int newProgress = getProgress();
        if (newProgress != oldProgress) {
            block.setBrushedProgress(newProgress);
            level.setBlock(block, block, true);
        }

        scheduleUpdate();
        return false;
    }

    private int getProgress() {
        return brushCount == 0 ? 0 :
                brushCount < 3 ? 1 :
                brushCount < 6 ? 2 : 3;
    }

    public boolean hasItem() {
        return item != null && !item.isNull();
    }

    @Nullable
    public Item getItem() {
        return item;
    }

    public void setItem(@Nullable Item item) {
        this.item = item;
    }
}
