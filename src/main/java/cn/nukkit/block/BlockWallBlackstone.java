package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

public class BlockWallBlackstone extends BlockWall {
    public BlockWallBlackstone() {
        this(0);
    }

    public BlockWallBlackstone(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BLACKSTONE_WALL;
    }

    @Override
    public String getName() {
        return "Blackstone Wall";
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
        return BlockColor.BLACK_BLOCK_COLOR;
    }
}
