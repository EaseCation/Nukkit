package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCandleCyan extends BlockCandle {
    public BlockCandleCyan() {
        this(0);
    }

    public BlockCandleCyan(int meta) {
        super(meta);
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
