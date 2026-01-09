package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabSmoothStone extends BlockSlabStone {
    BlockSlabSmoothStone() {

    }

    @Override
    public int getId() {
        return SMOOTH_STONE_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Smooth Stone Slab" : "Smooth Stone Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.STONE_BLOCK_COLOR;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return SMOOTH_STONE_DOUBLE_SLAB;
    }
}
