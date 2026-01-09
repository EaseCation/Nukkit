package cn.nukkit.block;

public class BlockCakeCandleGray extends BlockCakeCandle {
    BlockCakeCandleGray() {

    }

    @Override
    public int getId() {
        return GRAY_CANDLE_CAKE;
    }

    @Override
    public String getName() {
        return "Gray Candle Cake";
    }

    @Override
    protected int getCandleBlockId() {
        return GRAY_CANDLE;
    }
}
