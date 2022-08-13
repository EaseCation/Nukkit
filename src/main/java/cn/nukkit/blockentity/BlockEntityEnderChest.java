package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;

public class BlockEntityEnderChest extends BlockEntitySpawnable {

    public BlockEntityEnderChest(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == Block.ENDER_CHEST;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return getDefaultCompound(this, ENDER_CHEST)
                .putList(new ListTag<>("Items"));
    }
}
