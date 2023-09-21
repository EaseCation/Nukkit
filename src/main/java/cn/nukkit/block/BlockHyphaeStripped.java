package cn.nukkit.block;

import cn.nukkit.item.ItemTool;

import static cn.nukkit.GameVersion.*;

public abstract class BlockHyphaeStripped extends BlockRotatedPillar {
    protected BlockHyphaeStripped(int meta) {
        super(meta);
    }

    @Override
    public double getHardness() {
        if (V1_20_30.isAvailable()) {
            return 2;
        }
        return 0.3;
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
