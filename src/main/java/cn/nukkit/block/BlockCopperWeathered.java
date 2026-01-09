package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCopperWeathered extends BlockCopper {
    BlockCopperWeathered() {

    }

    @Override
    public int getId() {
        return WEATHERED_COPPER;
    }

    @Override
    public String getName() {
        return "Weathered Copper";
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
        return WAXED_WEATHERED_COPPER;
    }

    @Override
    public int getIncrementAgeBlockId() {
        return OXIDIZED_COPPER;
    }

    @Override
    public int getDecrementAgeBlockId() {
        return EXPOSED_COPPER;
    }
}
