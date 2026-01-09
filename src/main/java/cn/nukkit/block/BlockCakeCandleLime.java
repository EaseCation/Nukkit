package cn.nukkit.block;

public class BlockCakeCandleLime extends BlockCakeCandle {
    BlockCakeCandleLime() {

    }

    @Override
    public int getId() {
        return LIME_CANDLE_CAKE;
    }

    @Override
    public String getName() {
        return "Lime Candle Cake";
    }

    @Override
    protected int getCandleBlockId() {
        return LIME_CANDLE;
    }
}
