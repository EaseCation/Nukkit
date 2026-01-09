package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/11/23 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockFenceGateBirch extends BlockFenceGate {
    BlockFenceGateBirch() {

    }

    @Override
    public int getId() {
        return BIRCH_FENCE_GATE;
    }

    @Override
    public String getName() {
        return "Birch Fence Gate";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SAND_BLOCK_COLOR;
    }
}
