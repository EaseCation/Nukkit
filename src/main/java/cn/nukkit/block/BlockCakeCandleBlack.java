package cn.nukkit.block;

public class BlockCakeCandleBlack extends BlockCakeCandle {
    public BlockCakeCandleBlack() {
        this(0);
    }

    public BlockCakeCandleBlack(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BLACK_CANDLE_CAKE;
    }

    @Override
    public String getName() {
        return "Black Candle Cake";
    }

    @Override
    protected int getCandleBlockId() {
        return BLACK_CANDLE;
    }
}
