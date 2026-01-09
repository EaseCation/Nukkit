package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabCopperCutOxidizedWaxed extends BlockSlabCopperCutWaxed {
    BlockSlabCopperCutOxidizedWaxed() {

    }

    @Override
    public int getId() {
        return WAXED_OXIDIZED_CUT_COPPER_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Waxed Oxidized Cut Copper Slab" : "Waxed Oxidized Cut Copper Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WARPED_NYLIUM_BLOCK_COLOR;
    }

    @Override
    public int getCopperAge() {
        return 3;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return WAXED_OXIDIZED_DOUBLE_CUT_COPPER_SLAB;
    }

    @Override
    public int getIncrementAgeBlockId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getDecrementAgeBlockId() {
        return WAXED_WEATHERED_CUT_COPPER_SLAB;
    }

    @Override
    public int getDewaxedBlockId() {
        return OXIDIZED_CUT_COPPER_SLAB;
    }
}
