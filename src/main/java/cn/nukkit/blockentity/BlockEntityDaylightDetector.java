package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class BlockEntityDaylightDetector extends BlockEntity {

    private long currentTick = 0;

    public BlockEntityDaylightDetector(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == Block.DAYLIGHT_DETECTOR || blockId == Block.DAYLIGHT_DETECTOR_INVERTED;
    }

    @Override
    protected void initBlockEntity() {
        super.initBlockEntity();

//        this.scheduleUpdate();
    }

    @Override
    public boolean onUpdate() {
        if (++currentTick % 20 != 0) {
            return true;
        }

        //TODO: redstone
        return true;
    }
}