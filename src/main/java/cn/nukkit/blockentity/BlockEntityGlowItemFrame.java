package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class BlockEntityGlowItemFrame extends BlockEntityItemFrame {

    public BlockEntityGlowItemFrame(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getTypeId() {
        return BlockEntityType.GLOW_ITEM_FRAME;
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == Block.BLOCK_GLOW_FRAME;
    }

    @Override
    protected String getBlockEntityId() {
        return GLOW_ITEM_FRAME;
    }
}
