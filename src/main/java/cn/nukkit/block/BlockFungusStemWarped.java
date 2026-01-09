package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockFungusStemWarped extends BlockFungusStem {
    BlockFungusStemWarped() {

    }

    @Override
    public int getId() {
        return WARPED_STEM;
    }

    @Override
    public String getName() {
        return "Warped Stem";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WARPED_STEM_BLOCK_COLOR;
    }

    @Override
    protected Block getStrippedBlock() {
        return get(STRIPPED_WARPED_STEM, getDamage());
    }
}
