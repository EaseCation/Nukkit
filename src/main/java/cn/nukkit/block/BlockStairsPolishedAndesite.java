package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

public class BlockStairsPolishedAndesite extends BlockStairs {

    public BlockStairsPolishedAndesite() {
        this(0);
    }

    public BlockStairsPolishedAndesite(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return POLISHED_ANDESITE_STAIRS;
    }

    @Override
    public String getName() {
        return "Polished Andesite Stairs";
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
