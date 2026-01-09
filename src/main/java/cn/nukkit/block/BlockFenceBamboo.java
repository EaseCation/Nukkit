package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockFenceBamboo extends BlockFence {
    BlockFenceBamboo() {

    }

    @Override
    public int getId() {
        return BAMBOO_FENCE;
    }

    @Override
    public String getName() {
        return "Bamboo Fence";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.YELLOW_BLOCK_COLOR;
    }
}
