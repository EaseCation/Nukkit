package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockStairsSmoothRedSandstone extends BlockStairs {

    BlockStairsSmoothRedSandstone() {

    }

    @Override
    public int getId() {
        return SMOOTH_RED_SANDSTONE_STAIRS;
    }

    @Override
    public String getName() {
        return "Smooth Red Sandstone Stairs";
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
        return BlockColor.ORANGE_BLOCK_COLOR;
    }
}
