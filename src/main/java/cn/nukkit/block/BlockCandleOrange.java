package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCandleOrange extends BlockCandle {
    BlockCandleOrange() {

    }

    @Override
    public int getId() {
        return ORANGE_CANDLE;
    }

    @Override
    public String getName() {
        return "Orange Candle";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }
}
