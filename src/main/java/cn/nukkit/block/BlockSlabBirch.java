package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabBirch extends BlockSlabWood {
    BlockSlabBirch() {

    }

    @Override
    public int getId() {
        return BIRCH_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Birch Slab" : "Birch Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SAND_BLOCK_COLOR;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return BIRCH_DOUBLE_SLAB;
    }

    @Override
    public String getDescriptionId() {
        return "tile.wooden_slab.birch.name";
    }
}
