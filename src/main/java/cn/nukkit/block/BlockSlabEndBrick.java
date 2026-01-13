package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabEndBrick extends BlockSlabStone {
    BlockSlabEndBrick() {

    }

    @Override
    public int getId() {
        return END_STONE_BRICK_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper End Stone Brick Slab" : "End Stone Brick Slab";
    }

    @Override
    public float getHardness() {
        return 3;
    }

    @Override
    public float getResistance() {
        return 45;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SAND_BLOCK_COLOR;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return END_STONE_BRICK_DOUBLE_SLAB;
    }

    @Override
    public String getDescriptionId() {
        return "tile.stone_slab3.end_brick.name";
    }
}
