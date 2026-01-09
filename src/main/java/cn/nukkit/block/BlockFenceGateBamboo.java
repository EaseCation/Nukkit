package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockFenceGateBamboo extends BlockFenceGate {
    BlockFenceGateBamboo() {

    }

    @Override
    public int getId() {
        return BAMBOO_FENCE_GATE;
    }

    @Override
    public String getName() {
        return "Bamboo Fence Gate";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.YELLOW_BLOCK_COLOR;
    }
}
