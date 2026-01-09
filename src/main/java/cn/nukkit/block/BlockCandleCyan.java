package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCandleCyan extends BlockCandle {
    BlockCandleCyan() {

    }

    @Override
    public int getId() {
        return CYAN_CANDLE;
    }

    @Override
    public String getName() {
        return "Cyan Candle";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CYAN_BLOCK_COLOR;
    }
}
