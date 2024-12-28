package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockFenceDarkOak extends BlockFence {
    public BlockFenceDarkOak() {
    }

    @Override
    public int getId() {
        return DARK_OAK_FENCE;
    }

    @Override
    public String getName() {
        return "Dark Oak Fence";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BROWN_BLOCK_COLOR;
    }
}
