package cn.nukkit.block;

public class BlockCakeCandleOrange extends BlockCakeCandle {
    BlockCakeCandleOrange() {

    }

    @Override
    public int getId() {
        return ORANGE_CANDLE_CAKE;
    }

    @Override
    public String getName() {
        return "Orange Candle Cake";
    }

    @Override
    protected int getCandleBlockId() {
        return ORANGE_CANDLE;
    }
}
