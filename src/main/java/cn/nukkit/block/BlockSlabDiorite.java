package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabDiorite extends BlockSlabStone {
    BlockSlabDiorite() {

    }

    @Override
    public int getId() {
        return DIORITE_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Diorite Slab" : "Diorite Slab";
    }

    @Override
    public float getHardness() {
        return 1.5f;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.QUARTZ_BLOCK_COLOR;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return DIORITE_DOUBLE_SLAB;
    }
}
