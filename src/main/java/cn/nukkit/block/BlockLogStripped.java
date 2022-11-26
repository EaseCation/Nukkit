package cn.nukkit.block;

import cn.nukkit.item.ItemTool;

public abstract class BlockLogStripped extends BlockRotatedPillar {
    protected BlockLogStripped(int meta) {
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
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int getBurnAbility() {
        return 5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public boolean isLog() {
        return true;
    }
}
