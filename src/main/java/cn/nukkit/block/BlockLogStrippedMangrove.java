package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockLogStrippedMangrove extends BlockLogStripped {
    BlockLogStrippedMangrove() {

    }

    @Override
    public int getId() {
        return STRIPPED_MANGROVE_LOG;
    }

    @Override
    public String getName() {
        return "Stripped Mangrove Log";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.RED_BLOCK_COLOR;
    }
}
