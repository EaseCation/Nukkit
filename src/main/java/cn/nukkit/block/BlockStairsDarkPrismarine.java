package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

public class BlockStairsDarkPrismarine extends BlockStairs {

    public BlockStairsDarkPrismarine() {
        this(0);
    }

    public BlockStairsDarkPrismarine(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return DARK_PRISMARINE_STAIRS;
    }

    @Override
    public String getName() {
        return "Dark Prismarine Stairs";
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
