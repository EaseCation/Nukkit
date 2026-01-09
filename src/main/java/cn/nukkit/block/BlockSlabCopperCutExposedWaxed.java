package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabCopperCutExposedWaxed extends BlockSlabCopperCutWaxed {
    BlockSlabCopperCutExposedWaxed() {

    }

    @Override
    public int getId() {
        return WAXED_EXPOSED_CUT_COPPER_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Waxed Exposed Cut Copper Slab" : "Waxed Exposed Cut Copper Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.LIGHT_GRAY_TERRACOTA_BLOCK_COLOR;
    }

    @Override
    public int getCopperAge() {
        return 1;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return WAXED_EXPOSED_DOUBLE_CUT_COPPER_SLAB;
    }

    @Override
    public int getIncrementAgeBlockId() {
        return WAXED_WEATHERED_CUT_COPPER_SLAB;
    }

    @Override
    public int getDecrementAgeBlockId() {
        return WAXED_CUT_COPPER_SLAB;
    }

    @Override
    public int getDewaxedBlockId() {
        return EXPOSED_CUT_COPPER_SLAB;
    }
}
