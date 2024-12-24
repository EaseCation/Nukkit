package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCopperChiseledWeatheredWaxed extends BlockCopperChiseledWaxed {
    public BlockCopperChiseledWeatheredWaxed() {
    }

    @Override
    public int getId() {
        return WAXED_WEATHERED_CHISELED_COPPER;
    }

    @Override
    public String getName() {
        return "Waxed Weathered Chiseled Copper";
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
        return WAXED_OXIDIZED_CHISELED_COPPER;
    }

    @Override
    public int getDecrementAgeBlockId() {
        return WAXED_EXPOSED_CHISELED_COPPER;
    }

    @Override
    public int getDewaxedBlockId() {
        return WEATHERED_CHISELED_COPPER;
    }
}
