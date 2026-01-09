package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockStairsRedNetherBrick extends BlockStairs {

    BlockStairsRedNetherBrick() {

    }

    @Override
    public int getId() {
        return RED_NETHER_BRICK_STAIRS;
    }

    @Override
    public String getName() {
        return "Red Nether Brick Stairs";
    }

    @Override
    public float getHardness() {
        return 2;
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
        return BlockColor.NETHER_BLOCK_COLOR;
    }
}
