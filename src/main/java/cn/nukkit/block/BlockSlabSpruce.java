package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabSpruce extends BlockSlabWood {
    BlockSlabSpruce() {

    }

    @Override
    public int getId() {
        return SPRUCE_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Spruce Slab" : "Spruce Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PODZOL_BLOCK_COLOR;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return SPRUCE_DOUBLE_SLAB;
    }
}
