package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCandleLightBlue extends BlockCandle {
    BlockCandleLightBlue() {

    }

    @Override
    public int getId() {
        return LIGHT_BLUE_CANDLE;
    }

    @Override
    public String getName() {
        return "Light Blue Candle";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.LIGHT_BLUE_BLOCK_COLOR;
    }
}
