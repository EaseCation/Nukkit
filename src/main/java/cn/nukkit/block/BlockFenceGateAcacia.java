package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/11/23 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockFenceGateAcacia extends BlockFenceGate {
    BlockFenceGateAcacia() {

    }

    @Override
    public int getId() {
        return ACACIA_FENCE_GATE;
    }

    @Override
    public String getName() {
        return "Acacia Fence Gate";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }
}
