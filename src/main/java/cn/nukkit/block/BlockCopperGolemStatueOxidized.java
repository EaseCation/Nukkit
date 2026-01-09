package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCopperGolemStatueOxidized extends BlockCopperGolemStatue {
    BlockCopperGolemStatueOxidized() {

    }

    @Override
    public int getId() {
        return OXIDIZED_COPPER_GOLEM_STATUE;
    }

    @Override
    public String getName() {
        return "Oxidized Copper Golem Statue";
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
        return WAXED_OXIDIZED_COPPER_GOLEM_STATUE;
    }

    @Override
    public int getIncrementAgeBlockId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getDecrementAgeBlockId() {
        return WEATHERED_COPPER_GOLEM_STATUE;
    }
}
