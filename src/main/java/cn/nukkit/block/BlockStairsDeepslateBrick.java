package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

public class BlockStairsDeepslateBrick extends BlockStairs {
    public BlockStairsDeepslateBrick() {
        this(0);
    }

    public BlockStairsDeepslateBrick(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return DEEPSLATE_BRICK_STAIRS;
    }

    @Override
    public String getName() {
        return "Deepslate Brick Stairs";
    }

    @Override
    public double getHardness() {
        return 3.5;
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
        return BlockColor.DEEPSLATE_BLOCK_COLOR;
    }
}
