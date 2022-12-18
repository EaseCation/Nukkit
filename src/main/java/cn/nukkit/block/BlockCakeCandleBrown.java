package cn.nukkit.block;

public class BlockCakeCandleBrown extends BlockCakeCandle {
    public BlockCakeCandleBrown() {
        this(0);
    }

    public BlockCakeCandleBrown(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BROWN_CANDLE_CAKE;
    }

    @Override
    public String getName() {
        return "Brown Candle Cake";
    }

    @Override
    protected int getCandleBlockId() {
        return BROWN_CANDLE;
    }
}
