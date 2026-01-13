package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabMossyStoneBrick extends BlockSlabStone {
    BlockSlabMossyStoneBrick() {

    }

    @Override
    public int getId() {
        return MOSSY_STONE_BRICK_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Mossy Stone Brick Slab" : "Mossy Stone Brick Slab";
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
        return MOSSY_STONE_BRICK_DOUBLE_SLAB;
    }

    @Override
    public String getDescriptionId() {
        return "tile.stone_slab4.mossy_stone_brick.name";
    }
}
