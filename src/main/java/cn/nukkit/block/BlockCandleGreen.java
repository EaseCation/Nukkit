package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCandleGreen extends BlockCandle {
    public BlockCandleGreen() {
        this(0);
    }

    public BlockCandleGreen(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return GREEN_CANDLE;
    }

    @Override
    public String getName() {
        return "Green Candle";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GREEN_BLOCK_COLOR;
    }
}
