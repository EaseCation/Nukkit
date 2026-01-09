package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabJungle extends BlockSlabWood {
    BlockSlabJungle() {

    }

    @Override
    public int getId() {
        return JUNGLE_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Jungle Slab" : "Jungle Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIRT_BLOCK_COLOR;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return JUNGLE_DOUBLE_SLAB;
    }
}
