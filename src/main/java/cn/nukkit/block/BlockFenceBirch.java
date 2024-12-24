package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockFenceBirch extends BlockFence {
    public BlockFenceBirch() {
    }

    @Override
    public int getId() {
        return BIRCH_FENCE;
    }

    @Override
    public String getName() {
        return "Birch Fence";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SAND_BLOCK_COLOR;
    }
}
