package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockUnknownBlock extends BlockSolid {
    public BlockUnknownBlock() {
    }

    @Override
    public int getId() {
        return UNKNOWN;
    }

    @Override
    public String getName() {
        return "Unknown";
    }

    @Override
    public double getHardness() {
        return 0;
    }

    @Override
    public double getResistance() {
        return 0;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIRT_BLOCK_COLOR;
    }
}
