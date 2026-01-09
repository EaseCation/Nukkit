package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/11/23 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockFenceGateJungle extends BlockFenceGate {
    BlockFenceGateJungle() {

    }

    @Override
    public int getId() {
        return JUNGLE_FENCE_GATE;
    }

    @Override
    public String getName() {
        return "Jungle Fence Gate";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIRT_BLOCK_COLOR;
    }
}
