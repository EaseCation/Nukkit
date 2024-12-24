package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabCopperCutExposedWaxed extends BlockDoubleSlabCopperCutWaxed {
    public BlockDoubleSlabCopperCutExposedWaxed() {
        this(0);
    }

    public BlockDoubleSlabCopperCutExposedWaxed(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WAXED_EXPOSED_DOUBLE_CUT_COPPER_SLAB;
    }

    @Override
    public String getName() {
        return "Double Waxed Exposed Cut Copper Slab";
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
    protected int getSlabBlockId() {
        return WAXED_EXPOSED_CUT_COPPER_SLAB;
    }

    @Override
    public int getIncrementAgeBlockId() {
        return WAXED_WEATHERED_DOUBLE_CUT_COPPER_SLAB;
    }

    @Override
    public int getDecrementAgeBlockId() {
        return WAXED_DOUBLE_CUT_COPPER_SLAB;
    }

    @Override
    public int getDewaxedBlockId() {
        return EXPOSED_DOUBLE_CUT_COPPER_SLAB;
    }
}
