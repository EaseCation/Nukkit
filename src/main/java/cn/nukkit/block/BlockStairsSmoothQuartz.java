package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

import static cn.nukkit.GameVersion.*;

public class BlockStairsSmoothQuartz extends BlockStairs {

    BlockStairsSmoothQuartz() {

    }

    @Override
    public int getId() {
        return SMOOTH_QUARTZ_STAIRS;
    }

    @Override
    public String getName() {
        return "Smooth Quartz Stairs";
    }

    @Override
    public float getHardness() {
        if (V1_20_30.isAvailable()) {
            return 2;
        }
        return 0.8f;
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
        return BlockColor.QUARTZ_BLOCK_COLOR;
    }
}
