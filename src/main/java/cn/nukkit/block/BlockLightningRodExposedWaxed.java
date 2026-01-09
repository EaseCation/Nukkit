package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockLightningRodExposedWaxed extends BlockLightningRodWaxed {
    BlockLightningRodExposedWaxed() {

    }

    @Override
    public int getId() {
        return WAXED_EXPOSED_LIGHTNING_ROD;
    }

    @Override
    public String getName() {
        return "Waxed Exposed Lightning Rod";
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
    public int getIncrementAgeBlockId() {
        return WAXED_WEATHERED_LIGHTNING_ROD;
    }

    @Override
    public int getDecrementAgeBlockId() {
        return WAXED_LIGHTNING_ROD;
    }

    @Override
    public int getDewaxedBlockId() {
        return EXPOSED_LIGHTNING_ROD;
    }
}
