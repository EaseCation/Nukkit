package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockNetherWartBlockWarped extends BlockNetherWartBlock {
    BlockNetherWartBlockWarped() {

    }

    @Override
    public String getName() {
        return "Warped Wart Block";
    }

    @Override
    public int getId() {
        return WARPED_WART_BLOCK;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WARPED_WART_BLOCK_BLOCK_COLOR;
    }
}
