package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockLogStrippedSpruce extends BlockLogStripped {

    BlockLogStrippedSpruce() {

    }

    @Override
    public int getId() {
        return STRIPPED_SPRUCE_LOG;
    }

    @Override
    public String getName() {
        return "Stripped Spruce Log";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PODZOL_BLOCK_COLOR;
    }
}
