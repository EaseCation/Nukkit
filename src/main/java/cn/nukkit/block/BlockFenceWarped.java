package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockFenceWarped extends BlockFence {
    BlockFenceWarped() {

    }

    @Override
    public int getId() {
        return WARPED_FENCE;
    }

    @Override
    public String getName() {
        return "Warped Fence";
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
        return BlockColor.CYAN_BLOCK_COLOR;
    }

    @Override
    public int getFuelTime() {
        return 0;
    }
}
