package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabCopperCutWeathered extends BlockDoubleSlabCopperCut {
    public BlockDoubleSlabCopperCutWeathered() {
        this(0);
    }

    public BlockDoubleSlabCopperCutWeathered(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WEATHERED_DOUBLE_CUT_COPPER_SLAB;
    }

    @Override
    public String getName() {
        return "Double Weathered Cut Copper Slab";
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
        return WEATHERED_CUT_COPPER_SLAB;
    }

    @Override
    public int getWaxedBlockId() {
        return WAXED_WEATHERED_DOUBLE_CUT_COPPER_SLAB;
    }

    @Override
    public int getIncrementAgeBlockId() {
        return OXIDIZED_DOUBLE_CUT_COPPER_SLAB;
    }

    @Override
    public int getDecrementAgeBlockId() {
        return EXPOSED_DOUBLE_CUT_COPPER_SLAB;
    }
}
