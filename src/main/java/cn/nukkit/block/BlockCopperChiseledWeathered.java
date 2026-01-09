package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCopperChiseledWeathered extends BlockCopperChiseled {
    BlockCopperChiseledWeathered() {

    }

    @Override
    public int getId() {
        return WEATHERED_CHISELED_COPPER;
    }

    @Override
    public String getName() {
        return "Weathered Chiseled Copper";
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
        return WAXED_WEATHERED_CHISELED_COPPER;
    }

    @Override
    public int getIncrementAgeBlockId() {
        return OXIDIZED_CHISELED_COPPER;
    }

    @Override
    public int getDecrementAgeBlockId() {
        return EXPOSED_CHISELED_COPPER;
    }
}
