package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockFenceCrimson extends BlockFence {
    public BlockFenceCrimson() {
        super(0);
    }

    @Override
    public int getId() {
        return CRIMSON_FENCE;
    }

    @Override
    public String getName() {
        return "Crimson Fence";
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

    @Override
    public int getFullId() {
        return getId() << BLOCK_META_BITS;
    }

    @Override
    public void setDamage(int meta) {
    }
}
