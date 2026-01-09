package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabTuffBrick extends BlockSlabStone {
    BlockSlabTuffBrick() {

    }

    @Override
    public int getId() {
        return TUFF_BRICK_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Tuff Brick Slab" : "Tuff Brick Slab";
    }

    @Override
    public float getHardness() {
        return 1.5f;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GRAY_TERRACOTA_BLOCK_COLOR;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return TUFF_BRICK_DOUBLE_SLAB;
    }
}
