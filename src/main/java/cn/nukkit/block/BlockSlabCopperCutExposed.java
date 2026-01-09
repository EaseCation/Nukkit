package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabCopperCutExposed extends BlockSlabCopperCut {
    BlockSlabCopperCutExposed() {

    }

    @Override
    public int getId() {
        return EXPOSED_CUT_COPPER_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Exposed Cut Copper Slab" : "Exposed Cut Copper Slab";
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
        return EXPOSED_DOUBLE_CUT_COPPER_SLAB;
    }

    @Override
    public int getWaxedBlockId() {
        return WAXED_EXPOSED_CUT_COPPER_SLAB;
    }

    @Override
    public int getIncrementAgeBlockId() {
        return WEATHERED_CUT_COPPER_SLAB;
    }

    @Override
    public int getDecrementAgeBlockId() {
        return CUT_COPPER_SLAB;
    }
}
