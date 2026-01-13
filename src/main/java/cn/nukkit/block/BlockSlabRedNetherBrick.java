package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabRedNetherBrick extends BlockSlabStone {
    BlockSlabRedNetherBrick() {

    }

    @Override
    public int getId() {
        return RED_NETHER_BRICK_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Red Nether Brick Slab" : "Red Nether Brick Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.NETHER_BLOCK_COLOR;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return RED_NETHER_BRICK_DOUBLE_SLAB;
    }

    @Override
    public String getDescriptionId() {
        return "tile.stone_slab2.red_nether_brick.name";
    }
}
