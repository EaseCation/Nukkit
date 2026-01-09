package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockFungusStemCrimson extends BlockFungusStem {
    BlockFungusStemCrimson() {

    }

    @Override
    public int getId() {
        return CRIMSON_STEM;
    }

    @Override
    public String getName() {
        return "Crimson Stem";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CRIMSON_STEM_BLOCK_COLOR;
    }

    @Override
    protected Block getStrippedBlock() {
        return get(STRIPPED_CRIMSON_STEM, getDamage());
    }
}
