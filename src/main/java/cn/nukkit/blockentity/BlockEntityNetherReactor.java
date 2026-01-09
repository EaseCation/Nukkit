package cn.nukkit.blockentity;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class BlockEntityNetherReactor extends BlockEntitySpawnable {

    private boolean initialized;
    private int progress;
    private boolean finished;

    public BlockEntityNetherReactor(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getTypeId() {
        return BlockEntityType.NETHER_REACTOR;
    }

    @Override
    protected void initBlockEntity() {
        if (!namedTag.contains("IsInitialized")) {
            namedTag.putBoolean("IsInitialized", false);
            initialized = false;
        } else {
            initialized = namedTag.getBoolean("IsInitialized");
        }
        if (!namedTag.contains("Progress")) {
            namedTag.putShort("Progress", 0);
            progress = 0;
        } else {
            progress = Math.max(namedTag.getShort("Progress"), 0);
        }
        if (!namedTag.contains("HasFinished")) {
            namedTag.putBoolean("HasFinished", false);
            finished = false;
        } else {
            finished = namedTag.getBoolean("HasFinished");
        }

        super.initBlockEntity();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        namedTag.putBoolean("IsInitialized", initialized);
        namedTag.putShort("Progress", progress);
        namedTag.putBoolean("HasFinished", finished);
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == BlockID.NETHERREACTOR;
    }

    @Override
    public CompoundTag getSpawnCompound(boolean chunkData) {
        return getDefaultCompound(this, NETHER_REACTOR)
                .putBoolean("IsInitialized", initialized)
                .putShort("Progress", progress)
                .putBoolean("HasFinished", finished);
    }
}
