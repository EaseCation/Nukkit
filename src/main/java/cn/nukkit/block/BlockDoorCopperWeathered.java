package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockDoorCopperWeathered extends BlockDoorCopper {
    BlockDoorCopperWeathered() {

    }

    @Override
    public int getId() {
        return WEATHERED_COPPER_DOOR;
    }

    @Override
    public String getName() {
        return "Weathered Copper Door";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WARPED_STEM_BLOCK_COLOR;
    }

    @Override
    public int getCopperAge() {
        return 2;
    }

    @Override
    public int getWaxedBlockId() {
        return WAXED_WEATHERED_COPPER_DOOR;
    }

    @Override
    public int getIncrementAgeBlockId() {
        return OXIDIZED_COPPER_DOOR;
    }

    @Override
    public int getDecrementAgeBlockId() {
        return EXPOSED_COPPER_DOOR;
    }
}
