package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockHyphaeStrippedCrimson extends BlockHyphaeStripped {
    public BlockHyphaeStrippedCrimson() {
        this(0);
    }

    public BlockHyphaeStrippedCrimson(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STRIPPED_CRIMSON_HYPHAE;
    }

    @Override
    public String getName() {
        return "Stripped Crimson Hyphae";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CRIMSON_HYPHAE_BLOCK_COLOR;
    }
}
