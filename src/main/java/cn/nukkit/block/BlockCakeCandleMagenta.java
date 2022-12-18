package cn.nukkit.block;

public class BlockCakeCandleMagenta extends BlockCakeCandle {
    public BlockCakeCandleMagenta() {
        this(0);
    }

    public BlockCakeCandleMagenta(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return MAGENTA_CANDLE_CAKE;
    }

    @Override
    public String getName() {
        return "Magenta Candle Cake";
    }

    @Override
    protected int getCandleBlockId() {
        return MAGENTA_CANDLE;
    }
}
