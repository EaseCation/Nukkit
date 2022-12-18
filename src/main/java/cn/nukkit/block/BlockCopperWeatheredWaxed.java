package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCopperWeatheredWaxed extends BlockCopperWaxed {
    public BlockCopperWeatheredWaxed() {
    }

    @Override
    public int getId() {
        return WAXED_WEATHERED_COPPER;
    }

    @Override
    public String getName() {
        return "Waxed Weathered Copper";
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
    protected int getIncrementAgeBlockId() {
        return WAXED_OXIDIZED_COPPER;
    }

    @Override
    protected int getDecrementAgeBlockId() {
        return WAXED_EXPOSED_COPPER;
    }

    @Override
    protected int getDewaxedBlockId() {
        return WEATHERED_COPPER;
    }
}
