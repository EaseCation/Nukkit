package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabDeepslateBrick extends BlockSlabStone {
    BlockSlabDeepslateBrick() {

    }

    @Override
    public int getId() {
        return DEEPSLATE_BRICK_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Deepslate Brick Slab" : "Deepslate Brick Slab";
    }

    @Override
    public float getHardness() {
        return 3.5f;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DEEPSLATE_BLOCK_COLOR;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return DEEPSLATE_BRICK_DOUBLE_SLAB;
    }
}
