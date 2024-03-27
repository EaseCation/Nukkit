package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/11/25 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockStairsNetherBrick extends BlockStairs {
    public BlockStairsNetherBrick() {
        this(0);
    }

    public BlockStairsNetherBrick(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return NETHER_BRICK_STAIRS;
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
        return "Nether Bricks Stairs";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.NETHER_BLOCK_COLOR;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
