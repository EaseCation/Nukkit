package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabBlackstone extends BlockSlabStone {
    BlockSlabBlackstone() {

    }

    @Override
    public int getId() {
        return BLACKSTONE_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Blackstone Slab" : "Blackstone Slab";
    }

    @Override
    public float getHardness() {
        return 1.5f;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BLACK_BLOCK_COLOR;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return BLACKSTONE_DOUBLE_SLAB;
    }
}
