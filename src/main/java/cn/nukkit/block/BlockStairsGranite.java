package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

public class BlockStairsGranite extends BlockStairs {

    public BlockStairsGranite() {
        this(0);
    }

    public BlockStairsGranite(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return GRANITE_STAIRS;
    }

    @Override
    public String getName() {
        return "Granite Stairs";
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
        return BlockColor.DIRT_BLOCK_COLOR;
    }
}
