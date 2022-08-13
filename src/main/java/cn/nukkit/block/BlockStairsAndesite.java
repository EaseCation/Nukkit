package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

public class BlockStairsAndesite extends BlockStairs {

    public BlockStairsAndesite() {
        this(0);
    }

    public BlockStairsAndesite(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return ANDESITE_STAIRS;
    }

    @Override
    public String getName() {
        return "Andesite Stairs";
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
        return BlockColor.STONE_BLOCK_COLOR;
    }
}
