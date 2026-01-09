package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockGranite extends BlockStoneAbstract {
    BlockGranite() {

    }

    @Override
    public int getId() {
        return GRANITE;
    }

    @Override
    public String getName() {
        return "Granite";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIRT_BLOCK_COLOR;
    }
}
