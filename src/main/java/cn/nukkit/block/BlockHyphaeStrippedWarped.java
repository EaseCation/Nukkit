package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockHyphaeStrippedWarped extends BlockHyphaeStripped {
    BlockHyphaeStrippedWarped() {

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
