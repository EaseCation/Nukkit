package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCandleBlack extends BlockCandle {
    BlockCandleBlack() {

    }

    @Override
    public int getId() {
        return BLACK_CANDLE;
    }

    @Override
    public String getName() {
        return "Black Candle";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BLACK_BLOCK_COLOR;
    }
}
