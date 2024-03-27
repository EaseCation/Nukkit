package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockStairsStone extends BlockStairs {

    public BlockStairsStone() {
        this(0);
    }

    public BlockStairsStone(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return NORMAL_STONE_STAIRS;
    }

    @Override
    public String getName() {
        return "Stone Stairs";
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
        return BlockColor.STONE_BLOCK_COLOR;
    }
}
