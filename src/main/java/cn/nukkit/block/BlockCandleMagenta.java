package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCandleMagenta extends BlockCandle {
    public BlockCandleMagenta() {
        this(0);
    }

    public BlockCandleMagenta(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return MAGENTA_CANDLE;
    }

    @Override
    public String getName() {
        return "Magenta Candle";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.MAGENTA_BLOCK_COLOR;
    }
}
