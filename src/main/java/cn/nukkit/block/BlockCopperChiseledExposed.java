package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCopperChiseledExposed extends BlockCopperChiseled {
    BlockCopperChiseledExposed() {

    }

    @Override
    public int getId() {
        return EXPOSED_CHISELED_COPPER;
    }

    @Override
    public String getName() {
        return "Exposed Chiseled Copper";
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
        return WAXED_EXPOSED_CHISELED_COPPER;
    }

    @Override
    public int getIncrementAgeBlockId() {
        return WEATHERED_CHISELED_COPPER;
    }

    @Override
    public int getDecrementAgeBlockId() {
        return CHISELED_COPPER;
    }
}
