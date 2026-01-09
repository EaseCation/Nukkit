package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabBambooMosaic extends BlockSlabWood {
    BlockSlabBambooMosaic() {

    }

    @Override
    public int getId() {
        return BAMBOO_MOSAIC_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Bamboo Mosaic Slab" : "Bamboo Mosaic Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.YELLOW_BLOCK_COLOR;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return BAMBOO_MOSAIC_DOUBLE_SLAB;
    }
}
