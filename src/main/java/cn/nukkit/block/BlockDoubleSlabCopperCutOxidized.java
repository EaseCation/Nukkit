package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabCopperCutOxidized extends BlockDoubleSlabCopperCut {
    public BlockDoubleSlabCopperCutOxidized() {
        this(0);
    }

    public BlockDoubleSlabCopperCutOxidized(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return OXIDIZED_DOUBLE_CUT_COPPER_SLAB;
    }

    @Override
    public String getName() {
        return "Double Oxidized Cut Copper Slab";
    }

    @Override
    public int onUpdate(int type) {
        return 0;
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
    protected int getSlabBlockId() {
        return OXIDIZED_CUT_COPPER_SLAB;
    }

    @Override
    public int getWaxedBlockId() {
        return WAXED_OXIDIZED_DOUBLE_CUT_COPPER_SLAB;
    }

    @Override
    public int getIncrementAgeBlockId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getDecrementAgeBlockId() {
        return WEATHERED_DOUBLE_CUT_COPPER_SLAB;
    }
}
