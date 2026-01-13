package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabSandstone extends BlockSlabStone {
    BlockSlabSandstone() {

    }

    @Override
    public int getId() {
        return SANDSTONE_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Sandstone Slab" : "Sandstone Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SAND_BLOCK_COLOR;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return SANDSTONE_DOUBLE_SLAB;
    }

    @Override
    public String getDescriptionId() {
        return "tile.stone_slab.sand.name";
    }
}
