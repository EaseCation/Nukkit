package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabOak extends BlockSlabWood {
    BlockSlabOak() {

    }

    @Override
    public int getId() {
        return OAK_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Oak Slab" : "Oak Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return OAK_DOUBLE_SLAB;
    }
}
