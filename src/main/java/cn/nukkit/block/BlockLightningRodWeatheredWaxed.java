package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockLightningRodWeatheredWaxed extends BlockLightningRodWaxed {
    BlockLightningRodWeatheredWaxed() {

    }

    @Override
    public int getId() {
        return WAXED_WEATHERED_LIGHTNING_ROD;
    }

    @Override
    public String getName() {
        return "Waxed Weathered Lightning Rod";
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
        return WAXED_OXIDIZED_LIGHTNING_ROD;
    }

    @Override
    public int getDecrementAgeBlockId() {
        return WAXED_EXPOSED_LIGHTNING_ROD;
    }

    @Override
    public int getDewaxedBlockId() {
        return WEATHERED_LIGHTNING_ROD;
    }
}
