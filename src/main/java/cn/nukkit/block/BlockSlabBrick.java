package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabBrick extends BlockSlabStone {
    BlockSlabBrick() {

    }

    @Override
    public int getId() {
        return BRICK_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Brick Slab" : "Brick Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.RED_BLOCK_COLOR;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return BRICK_DOUBLE_SLAB;
    }
}
