package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class BlockEntityHangingSign extends BlockEntitySign {
    public BlockEntityHangingSign(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == Block.OAK_HANGING_SIGN
                || blockId == Block.SPRUCE_HANGING_SIGN
                || blockId == Block.BIRCH_HANGING_SIGN
                || blockId == Block.JUNGLE_HANGING_SIGN
                || blockId == Block.ACACIA_HANGING_SIGN
                || blockId == Block.DARK_OAK_HANGING_SIGN
                || blockId == Block.CRIMSON_HANGING_SIGN
                || blockId == Block.WARPED_HANGING_SIGN
                || blockId == Block.MANGROVE_HANGING_SIGN
                || blockId == Block.BAMBOO_HANGING_SIGN
                || blockId == Block.CHERRY_HANGING_SIGN
                || blockId == Block.PALE_OAK_HANGING_SIGN
                ;
    }

    @Override
    protected String getBlockEntityId() {
        return HANGING_SIGN;
    }
}
