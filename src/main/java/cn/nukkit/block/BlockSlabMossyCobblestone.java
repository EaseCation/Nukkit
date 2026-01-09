package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabMossyCobblestone extends BlockSlabStone {
    BlockSlabMossyCobblestone() {

    }

    @Override
    public int getId() {
        return MOSSY_COBBLESTONE_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Mossy Cobblestone Slab" : "Mossy Cobblestone Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.STONE_BLOCK_COLOR;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return MOSSY_COBBLESTONE_DOUBLE_SLAB;
    }
}
