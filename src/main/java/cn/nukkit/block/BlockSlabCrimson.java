package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabCrimson extends BlockSlabWood {
    BlockSlabCrimson() {

    }

    @Override
    public int getId() {
        return CRIMSON_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Crimson Slab" : "Crimson Slab";
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
    public int getFuelTime() {
        return 0;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return CRIMSON_DOUBLE_SLAB;
    }
}
