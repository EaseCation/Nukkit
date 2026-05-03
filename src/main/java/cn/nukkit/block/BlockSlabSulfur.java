package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabSulfur extends BlockSlabStone {
    BlockSlabSulfur() {
    }

    @Override
    public int getId() {
        return SULFUR_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Sulfur Slab" : "Sulfur Slab";
    }

    @Override
    public float getHardness() {
        return 1.5f;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.YELLOW_BLOCK_COLOR;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return SULFUR_DOUBLE_SLAB;
    }
}
