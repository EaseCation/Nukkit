package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockGrateCopperExposedWaxed extends BlockGrateCopperWaxed {
    BlockGrateCopperExposedWaxed() {

    }

    @Override
    public int getId() {
        return WAXED_EXPOSED_COPPER_GRATE;
    }

    @Override
    public String getName() {
        return "Waxed Exposed Copper Grate";
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
        return WAXED_WEATHERED_COPPER_GRATE;
    }

    @Override
    public int getDecrementAgeBlockId() {
        return WAXED_COPPER_GRATE;
    }

    @Override
    public int getDewaxedBlockId() {
        return EXPOSED_COPPER_GRATE;
    }
}
