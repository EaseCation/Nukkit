package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockBulbCopperExposedWaxed extends BlockBulbCopperWaxed {
    public BlockBulbCopperExposedWaxed() {
    }

    @Override
    public int getId() {
        return WAXED_EXPOSED_COPPER_BULB;
    }

    @Override
    public String getName() {
        return "Waxed Exposed Copper Bulb";
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
        return WAXED_WEATHERED_COPPER_BULB;
    }

    @Override
    public int getDecrementAgeBlockId() {
        return WAXED_COPPER_BULB;
    }

    @Override
    public int getDewaxedBlockId() {
        return EXPOSED_COPPER_BULB;
    }
}
