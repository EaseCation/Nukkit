package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockTrapdoorCopperWeathered extends BlockTrapdoorCopper {
    public BlockTrapdoorCopperWeathered() {
    }

    @Override
    public int getId() {
        return WEATHERED_COPPER_TRAPDOOR;
    }

    @Override
    public String getName() {
        return "Weathered Copper Trapdoor";
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
        return WAXED_WEATHERED_COPPER_TRAPDOOR;
    }

    @Override
    public int getIncrementAgeBlockId() {
        return OXIDIZED_COPPER_TRAPDOOR;
    }

    @Override
    public int getDecrementAgeBlockId() {
        return EXPOSED_COPPER_TRAPDOOR;
    }
}
