package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabSmoothRedSandstone extends BlockSlabStone {
    BlockSlabSmoothRedSandstone() {

    }

    @Override
    public int getId() {
        return SMOOTH_RED_SANDSTONE_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Smooth Red Sandstone Slab" : "Smooth Red Sandstone Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return SMOOTH_RED_SANDSTONE_DOUBLE_SLAB;
    }

    @Override
    public String getDescriptionId() {
        return "tile.stone_slab3.red_sandstone.smooth.name";
    }
}
