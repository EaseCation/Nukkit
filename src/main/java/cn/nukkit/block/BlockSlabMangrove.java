package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabMangrove extends BlockSlabWood {
    BlockSlabMangrove() {

    }

    @Override
    public int getId() {
        return MANGROVE_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Mangrove Slab" : "Mangrove Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.RED_BLOCK_COLOR;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return MANGROVE_DOUBLE_SLAB;
    }
}
