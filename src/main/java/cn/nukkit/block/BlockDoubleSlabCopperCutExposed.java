package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabCopperCutExposed extends BlockDoubleSlabCopperCut {
    public BlockDoubleSlabCopperCutExposed() {
        this(0);
    }

    public BlockDoubleSlabCopperCutExposed(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return EXPOSED_DOUBLE_CUT_COPPER_SLAB;
    }

    @Override
    public String getName() {
        return "Double Exposed Cut Copper Slab";
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
        return EXPOSED_CUT_COPPER_SLAB;
    }

    @Override
    protected int getWaxedBlockId() {
        return WAXED_EXPOSED_DOUBLE_CUT_COPPER_SLAB;
    }

    @Override
    protected int getIncrementAgeBlockId() {
        return WEATHERED_DOUBLE_CUT_COPPER_SLAB;
    }

    @Override
    protected int getDecrementAgeBlockId() {
        return DOUBLE_CUT_COPPER_SLAB;
    }
}
