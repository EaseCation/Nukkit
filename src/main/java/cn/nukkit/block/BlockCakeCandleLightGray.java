package cn.nukkit.block;

public class BlockCakeCandleLightGray extends BlockCakeCandle {
    BlockCakeCandleLightGray() {

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
