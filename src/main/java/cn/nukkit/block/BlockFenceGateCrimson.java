package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockFenceGateCrimson extends BlockFenceGate {
    public BlockFenceGateCrimson() {
        this(0);
    }

    public BlockFenceGateCrimson(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CRIMSON_FENCE_GATE;
    }

    @Override
    public String getName() {
        return "Crimson Fence Gate";
    }

    @Override
    public int getBurnChance() {
        return 0;
    }

    @Override
    public int getBurnAbility() {
        return 0;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.NETHER_BLOCK_COLOR;
    }
}
