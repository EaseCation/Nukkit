package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCandleWhite extends BlockCandle {
    public BlockCandleWhite() {
        this(0);
    }

    public BlockCandleWhite(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WHITE_CANDLE;
    }

    @Override
    public String getName() {
        return "White Candle";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOL_BLOCK_COLOR;
    }
}
