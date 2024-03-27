package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/11/25 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockStairsBrick extends BlockStairs {
    public BlockStairsBrick() {
        this(0);
    }

    public BlockStairsBrick(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BRICK_STAIRS;
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
    public String getName() {
        return "Brick Stairs";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.RED_BLOCK_COLOR;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
