package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockBricksPrismarine extends BlockPrismarine {
    BlockBricksPrismarine() {

    }

    @Override
    public int getId() {
        return PRISMARINE_BRICKS;
    }

    @Override
    public String getName() {
        return "Prismarine Bricks";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIAMOND_BLOCK_COLOR;
    }
}
