package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCandlePurple extends BlockCandle {
    BlockCandlePurple() {

    }

    @Override
    public int getId() {
        return PURPLE_CANDLE;
    }

    @Override
    public String getName() {
        return "Purple Candle";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PURPLE_BLOCK_COLOR;
    }
}
