package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockGrateCopperOxidizedWaxed extends BlockGrateCopperWaxed {
    public BlockGrateCopperOxidizedWaxed() {
    }

    @Override
    public int getId() {
        return WAXED_OXIDIZED_COPPER_GRATE;
    }

    @Override
    public String getName() {
        return "Waxed Oxidized Copper Grate";
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
    public int getIncrementAgeBlockId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getDecrementAgeBlockId() {
        return WAXED_WEATHERED_COPPER_GRATE;
    }

    @Override
    public int getDewaxedBlockId() {
        return OXIDIZED_COPPER_GRATE;
    }
}
