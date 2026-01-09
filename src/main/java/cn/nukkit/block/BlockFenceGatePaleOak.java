package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockFenceGatePaleOak extends BlockFenceGate {
    BlockFenceGatePaleOak() {

    }

    @Override
    public int getId() {
        return PALE_OAK_FENCE_GATE;
    }

    @Override
    public String getName() {
        return "Pale Oak Fence Gate";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.QUARTZ_BLOCK_COLOR;
    }
}
