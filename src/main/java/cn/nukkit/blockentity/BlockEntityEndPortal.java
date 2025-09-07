package cn.nukkit.blockentity;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class BlockEntityEndPortal extends BlockEntity {

    public BlockEntityEndPortal(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getTypeId() {
        return BlockEntityType.END_PORTAL;
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == BlockID.END_PORTAL;
    }
}
