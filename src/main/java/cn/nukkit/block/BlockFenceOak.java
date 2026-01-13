package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockFenceOak extends BlockFence {
    BlockFenceOak() {

    }

    @Override
    public int getId() {
        return OAK_FENCE;
    }

    @Override
    public String getName() {
        return "Oak Fence";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

    @Override
    public String getDescriptionId() {
        return "tile.fence.name";
    }
}
