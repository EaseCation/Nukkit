package cn.nukkit.block;

import cn.nukkit.item.ItemTool;

public abstract class BlockFungusStemStripped extends BlockRotatedPillar {
    protected BlockFungusStemStripped(int meta) {
        super(meta);
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 10;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }
}
