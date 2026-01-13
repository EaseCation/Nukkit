package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabPrismarineBrick extends BlockSlabStone {
    BlockSlabPrismarineBrick() {

    }

    @Override
    public int getId() {
        return PRISMARINE_BRICK_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Prismarine Brick Slab" : "Prismarine Brick Slab";
    }

    @Override
    public float getHardness() {
        return 1.5f;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIAMOND_BLOCK_COLOR;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return PRISMARINE_BRICK_DOUBLE_SLAB;
    }

    @Override
    public String getDescriptionId() {
        return "tile.stone_slab2.prismarine.bricks.name";
    }
}
