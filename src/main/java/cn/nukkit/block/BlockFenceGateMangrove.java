package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockFenceGateMangrove extends BlockFenceGate {
    BlockFenceGateMangrove() {

    }

    @Override
    public int getId() {
        return MANGROVE_FENCE_GATE;
    }

    @Override
    public String getName() {
        return "Mangrove Fence Gate";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.RED_BLOCK_COLOR;
    }
}
