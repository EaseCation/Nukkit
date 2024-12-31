package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockFenceGateBamboo extends BlockFenceGate {
    public BlockFenceGateBamboo() {
        this(0);
    }

    public BlockFenceGateBamboo(int meta) {
        super(meta);
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