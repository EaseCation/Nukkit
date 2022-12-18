package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabCopperCutWeatheredWaxed extends BlockDoubleSlabCopperCutWaxed {
    public BlockDoubleSlabCopperCutWeatheredWaxed() {
        this(0);
    }

    public BlockDoubleSlabCopperCutWeatheredWaxed(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WAXED_WEATHERED_DOUBLE_CUT_COPPER_SLAB;
    }

    @Override
    public String getName() {
        return "Double Waxed Weathered Cut Copper Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WARPED_STEM_BLOCK_COLOR;
    }

    @Override
    public int getCopperAge() {
        return 2;
    }

    @Override
    protected int getSlabBlockId() {
        return WAXED_WEATHERED_CUT_COPPER_SLAB;
    }

    @Override
    protected int getIncrementAgeBlockId() {
        return WAXED_OXIDIZED_DOUBLE_CUT_COPPER_SLAB;
    }

    @Override
    protected int getDecrementAgeBlockId() {
        return WAXED_EXPOSED_DOUBLE_CUT_COPPER_SLAB;
    }

    @Override
    protected int getDewaxedBlockId() {
        return WEATHERED_DOUBLE_CUT_COPPER_SLAB;
    }
}
