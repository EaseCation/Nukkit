package cn.nukkit.block;

public class BlockCakeCandlePurple extends BlockCakeCandle {
    BlockCakeCandlePurple() {

    }

    @Override
    public int getId() {
        return PURPLE_CANDLE_CAKE;
    }

    @Override
    public String getName() {
        return "Purple Candle Cake";
    }

    @Override
    protected int getCandleBlockId() {
        return PURPLE_CANDLE;
    }
}
