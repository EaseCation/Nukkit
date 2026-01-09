package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockFungusStemStrippedCrimson extends BlockFungusStemStripped {
    BlockFungusStemStrippedCrimson() {

    }

    @Override
    public int getId() {
        return STRIPPED_CRIMSON_STEM;
    }

    @Override
    public String getName() {
        return "Stripped Crimson Stem";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CRIMSON_STEM_BLOCK_COLOR;
    }
}
