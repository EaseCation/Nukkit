package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockStairsWarped extends BlockStairsWood {
    BlockStairsWarped() {

    }

    @Override
    public int getId() {
        return WARPED_STAIRS;
    }

    @Override
    public String getName() {
        return "Warped Stairs";
    }

    @Override
    public int getBurnChance() {
        return 0;
    }

    @Override
    public int getBurnAbility() {
        return 0;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WARPED_STEM_BLOCK_COLOR;
    }

    @Override
    public int getFuelTime() {
        return 0;
    }
}
