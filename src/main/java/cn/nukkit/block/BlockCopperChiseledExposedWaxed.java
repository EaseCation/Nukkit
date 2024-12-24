package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCopperChiseledExposedWaxed extends BlockCopperChiseledWaxed {
    public BlockCopperChiseledExposedWaxed() {
    }

    @Override
    public int getId() {
        return WAXED_EXPOSED_CHISELED_COPPER;
    }

    @Override
    public String getName() {
        return "Waxed Exposed Chiseled Copper";
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
        return WAXED_WEATHERED_CHISELED_COPPER;
    }

    @Override
    public int getDecrementAgeBlockId() {
        return WAXED_CHISELED_COPPER;
    }

    @Override
    public int getDewaxedBlockId() {
        return EXPOSED_CHISELED_COPPER;
    }
}
