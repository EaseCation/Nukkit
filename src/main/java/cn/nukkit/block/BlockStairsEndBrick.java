package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

public class BlockStairsEndBrick extends BlockStairs {

    public BlockStairsEndBrick() {
        this(0);
    }

    public BlockStairsEndBrick(int meta) {
        super(meta);
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
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 30;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
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