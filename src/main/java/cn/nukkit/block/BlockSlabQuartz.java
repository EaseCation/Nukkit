package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabQuartz extends BlockSlabStone {
    BlockSlabQuartz() {

    }

    @Override
    public int getId() {
        return QUARTZ_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Quartz Slab" : "Quartz Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.QUARTZ_BLOCK_COLOR;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return QUARTZ_DOUBLE_SLAB;
    }

    @Override
    public String getDescriptionId() {
        return "tile.stone_slab.quartz.name";
    }
}
