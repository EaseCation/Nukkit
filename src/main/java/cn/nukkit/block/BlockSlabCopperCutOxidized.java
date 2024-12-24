package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabCopperCutOxidized extends BlockSlabCopperCut {
    public BlockSlabCopperCutOxidized() {
        this(0);
    }

    public BlockSlabCopperCutOxidized(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return OXIDIZED_CUT_COPPER_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Oxidized Cut Copper Slab" : "Oxidized Cut Copper Slab";
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
    protected int getDoubleSlabBlockId() {
        return OXIDIZED_DOUBLE_CUT_COPPER_SLAB;
    }

    @Override
    public int getWaxedBlockId() {
        return WAXED_OXIDIZED_CUT_COPPER_SLAB;
    }

    @Override
    public int getIncrementAgeBlockId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getDecrementAgeBlockId() {
        return WEATHERED_CUT_COPPER_SLAB;
    }
}
