package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabWarped extends BlockSlabWood {
    BlockSlabWarped() {

    }

    @Override
    public int getId() {
        return WARPED_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Warped Slab" : "Warped Slab";
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

    @Override
    protected int getDoubleSlabBlockId() {
        return WARPED_DOUBLE_SLAB;
    }
}
