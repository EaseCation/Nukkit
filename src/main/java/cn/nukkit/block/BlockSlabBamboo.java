package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabBamboo extends BlockSlabWood {
    BlockSlabBamboo() {

    }

    @Override
    public int getId() {
        return BAMBOO_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Bamboo Slab" : "Bamboo Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.YELLOW_BLOCK_COLOR;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return BAMBOO_DOUBLE_SLAB;
    }
}
