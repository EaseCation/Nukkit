package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockGrateCopperOxidized extends BlockGrateCopper {
    BlockGrateCopperOxidized() {

    }

    @Override
    public int getId() {
        return OXIDIZED_COPPER_GRATE;
    }

    @Override
    public String getName() {
        return "Oxidized Copper Grate";
    }

    @Override
    public int onUpdate(int type) {
        return 0;
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
        return WAXED_OXIDIZED_COPPER_GRATE;
    }

    @Override
    public int getIncrementAgeBlockId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getDecrementAgeBlockId() {
        return WEATHERED_COPPER_GRATE;
    }
}
