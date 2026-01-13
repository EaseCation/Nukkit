package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabCutSandstone extends BlockSlabStone {
    BlockSlabCutSandstone() {

    }

    @Override
    public int getId() {
        return CUT_SANDSTONE_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Cut Sandstone Slab" : "Cut Sandstone Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SAND_BLOCK_COLOR;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return CUT_SANDSTONE_DOUBLE_SLAB;
    }

    @Override
    public String getDescriptionId() {
        return "tile.stone_slab4.cut_sandstone.name";
    }
}
