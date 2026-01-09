package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockUnknownBlock extends BlockSolid {
    BlockUnknownBlock() {

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
    public float getHardness() {
        return 0;
    }

    @Override
    public float getResistance() {
        return 0;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIRT_BLOCK_COLOR;
    }
}
