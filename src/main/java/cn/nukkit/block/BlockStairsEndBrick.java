package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

import static cn.nukkit.GameVersion.*;

public class BlockStairsEndBrick extends BlockStairs {

    BlockStairsEndBrick() {

    }

    @Override
    public int getId() {
        return END_BRICK_STAIRS;
    }

    @Override
    public String getName() {
        return "End Stone Brick Stairs";
    }

    @Override
    public float getHardness() {
        if (V1_20_30.isAvailable()) {
            return 3;
        }
        return 2;
    }

    @Override
    public float getResistance() {
        return 45;
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
        return BlockColor.SAND_BLOCK_COLOR;
    }
}
