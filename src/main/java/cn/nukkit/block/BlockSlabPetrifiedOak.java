package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabPetrifiedOak extends BlockSlabStone {
    BlockSlabPetrifiedOak() {

    }

    @Override
    public int getId() {
        return PETRIFIED_OAK_SLAB;
    }

    @Override
    public String getDescriptionId() {
        return "tile.stone_slab.wood.name";
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Petrified Oak Slab" : "Petrified Oak Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return PETRIFIED_OAK_DOUBLE_SLAB;
    }
}
