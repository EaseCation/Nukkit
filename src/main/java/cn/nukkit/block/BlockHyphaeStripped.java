package cn.nukkit.block;

import cn.nukkit.item.ItemTool;

public abstract class BlockHyphaeStripped extends BlockRotatedPillar {
    protected BlockHyphaeStripped(int meta) {
        super(meta);
    }

    @Override
    public double getHardness() {
        return 0.3;
    }

    @Override
    public double getResistance() {
        return 1.5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }
}
