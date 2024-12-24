package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockGrateCopperWeatheredWaxed extends BlockGrateCopperWaxed {
    public BlockGrateCopperWeatheredWaxed() {
    }

    @Override
    public int getId() {
        return WAXED_WEATHERED_COPPER_GRATE;
    }

    @Override
    public String getName() {
        return "Waxed Weathered Copper Grate";
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
    public int getIncrementAgeBlockId() {
        return WAXED_OXIDIZED_COPPER_GRATE;
    }

    @Override
    public int getDecrementAgeBlockId() {
        return WAXED_EXPOSED_COPPER_GRATE;
    }

    @Override
    public int getDewaxedBlockId() {
        return WEATHERED_COPPER_GRATE;
    }
}
