package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockFenceJungle extends BlockFence {
    public BlockFenceJungle() {
    }

    @Override
    public int getId() {
        return JUNGLE_FENCE;
    }

    @Override
    public String getName() {
        return "Jungle Fence";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIRT_BLOCK_COLOR;
    }
}
