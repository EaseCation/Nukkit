package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCandleRed extends BlockCandle {
    BlockCandleRed() {

    }

    @Override
    public int getId() {
        return RED_CANDLE;
    }

    @Override
    public String getName() {
        return "Red Candle";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.RED_BLOCK_COLOR;
    }
}
