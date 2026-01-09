package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockLightningRodExposed extends BlockLightningRod {
    BlockLightningRodExposed() {

    }

    @Override
    public int getId() {
        return EXPOSED_LIGHTNING_ROD;
    }

    @Override
    public String getName() {
        return "Exposed Lightning Rod";
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
    public int getWaxedBlockId() {
        return WAXED_EXPOSED_LIGHTNING_ROD;
    }

    @Override
    public int getIncrementAgeBlockId() {
        return WEATHERED_LIGHTNING_ROD;
    }

    @Override
    public int getDecrementAgeBlockId() {
        return LIGHTNING_ROD;
    }
}
