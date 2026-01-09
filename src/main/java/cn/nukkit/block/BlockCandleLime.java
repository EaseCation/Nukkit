package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCandleLime extends BlockCandle {
    BlockCandleLime() {

    }

    @Override
    public int getId() {
        return LIME_CANDLE;
    }

    @Override
    public String getName() {
        return "Lime Candle";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.LIME_BLOCK_COLOR;
    }
}
