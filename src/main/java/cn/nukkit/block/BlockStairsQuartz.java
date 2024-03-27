package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/11/25 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockStairsQuartz extends BlockStairs {
    public BlockStairsQuartz() {
        this(0);
    }

    public BlockStairsQuartz(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return QUARTZ_STAIRS;
    }

    @Override
    public float getHardness() {
        return 0.8f;
    }

    @Override
    public float getResistance() {
        return 4;
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }

    @Override
    public String getName() {
        return "Quartz Stairs";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.QUARTZ_BLOCK_COLOR;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
