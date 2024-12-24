package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

//TODO
public class BlockEntityTrialSpawner extends BlockEntitySpawnable {
    public BlockEntityTrialSpawner(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {

        super.initBlockEntity();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == Block.TRIAL_SPAWNER;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag nbt = getDefaultCompound(this, TRIAL_SPAWNER);

        return nbt;
    }
}
