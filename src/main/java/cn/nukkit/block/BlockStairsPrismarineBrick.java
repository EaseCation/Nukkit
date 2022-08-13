package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

public class BlockStairsPrismarineBrick extends BlockStairs {

    public BlockStairsPrismarineBrick() {
        this(0);
    }

    public BlockStairsPrismarineBrick(int meta) {
        super(meta);
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
    public double getHardness() {
        return 1.5;
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
        return BlockColor.DIAMOND_BLOCK_COLOR;
    }
}
