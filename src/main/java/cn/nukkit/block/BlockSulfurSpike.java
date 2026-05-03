package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSulfurSpike extends BlockDripstonePointed {
    BlockSulfurSpike() {
    }

    @Override
    public int getId() {
        return SULFUR_SPIKE;
    }

    @Override
    public String getName() {
        return "Sulfur Spike";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.YELLOW_BLOCK_COLOR;
    }
}
