package cn.nukkit.blockentity;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class BlockEntitySporeBlossom extends BlockEntity {
    public BlockEntitySporeBlossom(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getTypeId() {
        return BlockEntityType.SPORE_BLOSSOM;
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == BlockID.SPORE_BLOSSOM;
    }
}
