package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabPaleOak extends BlockSlabWood {
    BlockSlabPaleOak() {

    }

    @Override
    public int getId() {
        return PALE_OAK_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Pale Oak Slab" : "Pale Oak Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.QUARTZ_BLOCK_COLOR;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return PALE_OAK_DOUBLE_SLAB;
    }
}
