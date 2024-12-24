package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockTrapdoorCopperWeatheredWaxed extends BlockTrapdoorCopperWaxed {
    public BlockTrapdoorCopperWeatheredWaxed() {
    }

    @Override
    public int getId() {
        return WAXED_WEATHERED_COPPER_TRAPDOOR;
    }

    @Override
    public String getName() {
        return "Waxed Weathered Copper Trapdoor";
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
    public int getIncrementAgeBlockId() {
        return WAXED_OXIDIZED_COPPER_TRAPDOOR;
    }

    @Override
    public int getDecrementAgeBlockId() {
        return WAXED_EXPOSED_COPPER_TRAPDOOR;
    }

    @Override
    public int getDewaxedBlockId() {
        return WEATHERED_COPPER_TRAPDOOR;
    }
}
