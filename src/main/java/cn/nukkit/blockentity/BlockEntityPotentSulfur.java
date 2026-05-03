package cn.nukkit.blockentity;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class BlockEntityPotentSulfur extends BlockEntity {
    public BlockEntityPotentSulfur(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getTypeId() {
        return BlockEntityType.POTENT_SULFUR_BLOCK;
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == BlockID.POTENT_SULFUR;
    }
}
