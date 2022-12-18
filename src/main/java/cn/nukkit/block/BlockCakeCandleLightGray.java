package cn.nukkit.block;

public class BlockCakeCandleLightGray extends BlockCakeCandle {
    public BlockCakeCandleLightGray() {
        this(0);
    }

    public BlockCakeCandleLightGray(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return LIGHT_GRAY_CANDLE_CAKE;
    }

    @Override
    public String getName() {
        return "Light Gray Candle Cake";
    }

    @Override
    protected int getCandleBlockId() {
        return LIGHT_GRAY_CANDLE;
    }
}
