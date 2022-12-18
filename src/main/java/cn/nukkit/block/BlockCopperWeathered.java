package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCopperWeathered extends BlockCopper {
    public BlockCopperWeathered() {
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
    protected int getWaxedBlockId() {
        return WAXED_WEATHERED_COPPER;
    }

    @Override
    protected int getIncrementAgeBlockId() {
        return OXIDIZED_COPPER;
    }

    @Override
    protected int getDecrementAgeBlockId() {
        return EXPOSED_COPPER;
    }
}
