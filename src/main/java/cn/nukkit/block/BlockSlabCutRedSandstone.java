package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabCutRedSandstone extends BlockSlabStone {
    BlockSlabCutRedSandstone() {

    }

    @Override
    public int getId() {
        return CUT_RED_SANDSTONE_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Cut Red Sandstone Slab" : "Cut Red Sandstone Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return CUT_RED_SANDSTONE_DOUBLE_SLAB;
    }
}
