package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockHyphaeStrippedWarped extends BlockHyphaeStripped {
    public BlockHyphaeStrippedWarped() {
        this(0);
    }

    public BlockHyphaeStrippedWarped(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STRIPPED_WARPED_HYPHAE;
    }

    @Override
    public String getName() {
        return "Stripped Warped Hyphae";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WARPED_HYPHAE_BLOCK_COLOR;
    }
}
