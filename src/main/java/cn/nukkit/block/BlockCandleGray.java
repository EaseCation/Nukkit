package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCandleGray extends BlockCandle {
    BlockCandleGray() {

    }

    @Override
    public int getId() {
        return GRAY_CANDLE;
    }

    @Override
    public String getName() {
        return "Gray Candle";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GRAY_BLOCK_COLOR;
    }
}
