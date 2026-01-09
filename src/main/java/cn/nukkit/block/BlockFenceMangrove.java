package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockFenceMangrove extends BlockFence {
    BlockFenceMangrove() {

    }

    @Override
    public int getId() {
        return MANGROVE_FENCE;
    }

    @Override
    public String getName() {
        return "Mangrove Fence";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.RED_BLOCK_COLOR;
    }
}
