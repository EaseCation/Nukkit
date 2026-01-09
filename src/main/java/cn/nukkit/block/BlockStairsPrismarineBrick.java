package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockStairsPrismarineBrick extends BlockStairs {

    BlockStairsPrismarineBrick() {

    }

    @Override
    public int getId() {
        return PRISMARINE_BRICKS_STAIRS;
    }

    @Override
    public String getName() {
        return "Prismarine Brick Stairs";
    }

    @Override
    public float getHardness() {
        return 1.5f;
    }

    @Override
    public float getResistance() {
        return 30;
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIAMOND_BLOCK_COLOR;
    }
}
