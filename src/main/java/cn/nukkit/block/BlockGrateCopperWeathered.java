package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockGrateCopperWeathered extends BlockGrateCopper {
    BlockGrateCopperWeathered() {

    }

    @Override
    public int getId() {
        return WEATHERED_COPPER_GRATE;
    }

    @Override
    public String getName() {
        return "Weathered Copper Grate";
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
    public int getWaxedBlockId() {
        return WAXED_WEATHERED_COPPER_GRATE;
    }

    @Override
    public int getIncrementAgeBlockId() {
        return OXIDIZED_COPPER_GRATE;
    }

    @Override
    public int getDecrementAgeBlockId() {
        return EXPOSED_COPPER_GRATE;
    }
}
