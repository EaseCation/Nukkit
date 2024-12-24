package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockBulbCopperExposed extends BlockBulbCopper {
    public BlockBulbCopperExposed() {
    }

    @Override
    public int getId() {
        return EXPOSED_COPPER_BULB;
    }

    @Override
    public String getName() {
        return "Exposed Copper Bulb";
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
        return WAXED_EXPOSED_COPPER_BULB;
    }

    @Override
    public int getIncrementAgeBlockId() {
        return WEATHERED_COPPER_BULB;
    }

    @Override
    public int getDecrementAgeBlockId() {
        return COPPER_BULB;
    }
}
