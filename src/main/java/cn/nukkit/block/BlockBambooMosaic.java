package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockBambooMosaic extends BlockSolid {
    BlockBambooMosaic() {

    }

    @Override
    public int getId() {
        return BAMBOO_MOSAIC;
    }

    @Override
    public String getName() {
        return "Bamboo Mosaic";
    }

    @Override
    public float getHardness() {
        return 2;
    }

    @Override
    public float getResistance() {
        return 15;
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int getBurnAbility() {
        return 20;
    }

    @Override
    public int getToolType() {
        return BlockToolType.AXE;
    }

    @Override
    public int getFuelTime() {
        return 300;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.YELLOW_BLOCK_COLOR;
    }
}
