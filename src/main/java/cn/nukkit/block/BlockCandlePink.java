package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCandlePink extends BlockCandle {
    public BlockCandlePink() {
        this(0);
    }

    public BlockCandlePink(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return PINK_CANDLE;
    }

    @Override
    public String getName() {
        return "Pink Candle";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PINK_BLOCK_COLOR;
    }
}
