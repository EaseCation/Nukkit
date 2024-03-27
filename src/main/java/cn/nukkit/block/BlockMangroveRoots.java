package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockMangroveRoots extends BlockTransparent {
    public BlockMangroveRoots() {
    }

    @Override
    public int getId() {
        return MANGROVE_ROOTS;
    }

    @Override
    public String getName() {
        return "Mangrove Roots";
    }

    @Override
    public float getHardness() {
        return 0.7f;
    }

    @Override
    public float getResistance() {
        return 3.5f;
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
        return BlockToolType.AXE;
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PODZOL_BLOCK_COLOR;
    }
}
