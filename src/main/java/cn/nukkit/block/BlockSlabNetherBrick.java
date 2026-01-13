package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabNetherBrick extends BlockSlabStone {
    BlockSlabNetherBrick() {

    }

    @Override
    public int getId() {
        return NETHER_BRICK_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Nether Brick Slab" : "Nether Brick Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.NETHER_BLOCK_COLOR;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return NETHER_BRICK_DOUBLE_SLAB;
    }

    @Override
    public String getDescriptionId() {
        return "tile.stone_slab.nether_brick.name";
    }
}
