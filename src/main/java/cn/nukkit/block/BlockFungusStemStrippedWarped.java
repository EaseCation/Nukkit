package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockFungusStemStrippedWarped extends BlockFungusStemStripped {
    public BlockFungusStemStrippedWarped() {
        this(0);
    }

    public BlockFungusStemStrippedWarped(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STRIPPED_WARPED_STEM;
    }

    @Override
    public String getName() {
        return "Stripped Warped Stem";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WARPED_STEM_BLOCK_COLOR;
    }
}
