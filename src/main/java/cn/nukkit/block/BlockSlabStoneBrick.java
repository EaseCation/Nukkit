package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabStoneBrick extends BlockSlabStone {
    BlockSlabStoneBrick() {

    }

    @Override
    public int getId() {
        return STONE_BRICK_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Stone Brick Slab" : "Stone Brick Slab";
    }

    @Override
    public float getHardness() {
        return 1.5f;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.STONE_BLOCK_COLOR;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return STONE_BRICK_DOUBLE_SLAB;
    }
}
