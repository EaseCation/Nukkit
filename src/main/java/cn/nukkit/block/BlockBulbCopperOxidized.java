package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockBulbCopperOxidized extends BlockBulbCopper {
    BlockBulbCopperOxidized() {

    }

    @Override
    public int getId() {
        return OXIDIZED_COPPER_BULB;
    }

    @Override
    public String getName() {
        return "Oxidized Copper Bulb";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WARPED_NYLIUM_BLOCK_COLOR;
    }

    @Override
    public int getCopperAge() {
        return 3;
    }

    @Override
    public int getWaxedBlockId() {
        return WAXED_OXIDIZED_COPPER_BULB;
    }

    @Override
    public int getIncrementAgeBlockId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getDecrementAgeBlockId() {
        return WEATHERED_COPPER_BULB;
    }
}
