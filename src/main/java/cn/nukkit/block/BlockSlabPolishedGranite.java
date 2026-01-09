package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabPolishedGranite extends BlockSlabStone {
    BlockSlabPolishedGranite() {

    }

    @Override
    public int getId() {
        return POLISHED_GRANITE_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Polished Granite Slab" : "Polished Granite Slab";
    }

    @Override
    public float getHardness() {
        return 1.5f;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIRT_BLOCK_COLOR;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return POLISHED_GRANITE_DOUBLE_SLAB;
    }
}
