package cn.nukkit.block;

import static cn.nukkit.GameVersion.*;

public abstract class BlockHyphaeStripped extends BlockRotatedPillar {
    protected BlockHyphaeStripped(int meta) {
        super(meta);
    }

    @Override
    public float getHardness() {
        if (V1_20_30.isAvailable()) {
            return 2;
        }
        return 0.3f;
    }

    @Override
    public float getResistance() {
        return 10;
    }

    @Override
    public int getToolType() {
        return BlockToolType.AXE;
    }
}
