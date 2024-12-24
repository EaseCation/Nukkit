package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockFenceGatePaleOak extends BlockFenceGate {
    public BlockFenceGatePaleOak() {
        this(0);
    }

    public BlockFenceGatePaleOak(int meta) {
        super(meta);
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
