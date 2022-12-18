package cn.nukkit.block;

public class BlockCakeCandlePink extends BlockCakeCandle {
    public BlockCakeCandlePink() {
        this(0);
    }

    public BlockCakeCandlePink(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return PINK_CANDLE_CAKE;
    }

    @Override
    public String getName() {
        return "Pink Candle Cake";
    }

    @Override
    protected int getCandleBlockId() {
        return PINK_CANDLE;
    }
}
