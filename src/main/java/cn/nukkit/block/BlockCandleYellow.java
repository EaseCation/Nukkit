package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCandleYellow extends BlockCandle {
    public BlockCandleYellow() {
        this(0);
    }

    public BlockCandleYellow(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return YELLOW_CANDLE;
    }

    @Override
    public String getName() {
        return "Yellow Candle";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.YELLOW_BLOCK_COLOR;
    }
}
