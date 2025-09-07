package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class BlockEntityLodestone extends BlockEntitySpawnable {
    public static final int INVALID_POSITION_TRACKING_ID = 0;

    private int trackingHandle;

    public BlockEntityLodestone(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getTypeId() {
        return BlockEntityType.LODESTONE;
    }

    @Override
    protected void initBlockEntity() {
        if (namedTag.contains("trackingHandle")) {
            trackingHandle = namedTag.getInt("trackingHandle");
        } else {
            trackingHandle = INVALID_POSITION_TRACKING_ID;
        }

        super.initBlockEntity();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        if (trackingHandle != INVALID_POSITION_TRACKING_ID) {
            namedTag.putInt("trackingHandle", trackingHandle);
        }
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == Block.LODESTONE;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return getDefaultCompound(this, LODESTONE);
    }

    public boolean hasTrackingHandle() {
        return trackingHandle != INVALID_POSITION_TRACKING_ID;
    }

    public int getTrackingHandle() {
        return trackingHandle;
    }

    public void setTrackingHandle(int id) {
        this.trackingHandle = id;
    }
}
