package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockInfoUpdate extends BlockSolid {

    public BlockInfoUpdate() {
    }

    @Override
    public int getId() {
        return INFO_UPDATE;
    }

    @Override
    public String getName() {
        return "update!";
    }

    @Override
    public float getHardness() {
        return 1;
    }

    @Override
    public float getResistance() {
        return 5;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIRT_BLOCK_COLOR;
    }
}
