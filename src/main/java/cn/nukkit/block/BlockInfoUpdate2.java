package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockInfoUpdate2 extends BlockSolid {

    BlockInfoUpdate2() {

    }

    @Override
    public int getId() {
        return INFO_UPDATE2;
    }

    @Override
    public String getName() {
        return "ate!upd";
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
