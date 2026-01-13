package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabSmoothQuartz extends BlockSlabStone {
    BlockSlabSmoothQuartz() {

    }

    @Override
    public int getId() {
        return SMOOTH_QUARTZ_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Smooth Quartz Slab" : "Smooth Quartz Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.QUARTZ_BLOCK_COLOR;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return SMOOTH_QUARTZ_DOUBLE_SLAB;
    }

    @Override
    public String getDescriptionId() {
        return "tile.stone_slab4.smooth_quartz.name";
    }
}
