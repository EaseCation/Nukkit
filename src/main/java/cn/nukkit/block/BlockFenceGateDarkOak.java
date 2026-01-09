package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/11/23 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockFenceGateDarkOak extends BlockFenceGate {
    BlockFenceGateDarkOak() {

    }

    @Override
    public int getId() {
        return DARK_OAK_FENCE_GATE;
    }

    @Override
    public String getName() {
        return "Dark Oak Fence Gate";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BROWN_BLOCK_COLOR;
    }
}
