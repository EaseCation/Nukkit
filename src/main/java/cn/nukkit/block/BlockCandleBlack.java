package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCandleBlack extends BlockCandle {
    public BlockCandleBlack() {
        this(0);
    }

    public BlockCandleBlack(int meta) {
        super(meta);
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
