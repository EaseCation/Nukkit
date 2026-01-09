package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockDiorite extends BlockStoneAbstract {
    BlockDiorite() {

    }

    @Override
    public int getId() {
        return DIORITE;
    }

    @Override
    public String getName() {
        return "Diorite";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.QUARTZ_BLOCK_COLOR;
    }
}
