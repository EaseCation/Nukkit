package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabCopperCutWeatheredWaxed extends BlockSlabCopperCutWaxed {
    public BlockSlabCopperCutWeatheredWaxed() {
        this(0);
    }

    public BlockSlabCopperCutWeatheredWaxed(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WAXED_WEATHERED_CUT_COPPER_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Waxed Weathered Cut Copper Slab" : "Waxed Weathered Cut Copper Slab";
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
    protected int getDoubleSlabBlockId() {
        return WAXED_WEATHERED_DOUBLE_CUT_COPPER_SLAB;
    }

    @Override
    protected int getIncrementAgeBlockId() {
        return WAXED_OXIDIZED_CUT_COPPER_SLAB;
    }

    @Override
    protected int getDecrementAgeBlockId() {
        return WAXED_EXPOSED_CUT_COPPER_SLAB;
    }

    @Override
    protected int getDewaxedBlockId() {
        return WEATHERED_CUT_COPPER_SLAB;
    }
}
