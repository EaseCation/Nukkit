package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockHoneycomb extends BlockSolid {

    BlockHoneycomb() {

    }

    @Override
    public String getName() {
        return "Honeycomb Block";
    }

    @Override
    public int getId() {
        return HONEYCOMB_BLOCK;
    }

    @Override
    public float getHardness() {
        return 0.6f;
    }

    @Override
    public float getResistance() {
        return 3;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }
}
