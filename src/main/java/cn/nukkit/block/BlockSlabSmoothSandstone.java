package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabSmoothSandstone extends BlockSlabStone {
    BlockSlabSmoothSandstone() {

    }

    @Override
    public int getId() {
        return SMOOTH_SANDSTONE_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Smooth Sandstone Slab" : "Smooth Sandstone Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SAND_BLOCK_COLOR;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return SMOOTH_SANDSTONE_DOUBLE_SLAB;
    }

    @Override
    public String getDescriptionId() {
        return "tile.stone_slab2.sandstone.smooth.name";
    }
}
