package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCandleBlue extends BlockCandle {
    public BlockCandleBlue() {
        this(0);
    }

    public BlockCandleBlue(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BLUE_CANDLE;
    }

    @Override
    public String getName() {
        return "Blue Candle";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BLUE_BLOCK_COLOR;
    }
}
