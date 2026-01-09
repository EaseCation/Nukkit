package cn.nukkit.block;

public class BlockCakeCandleYellow extends BlockCakeCandle {
    BlockCakeCandleYellow() {

    }

    @Override
    public int getId() {
        return YELLOW_CANDLE_CAKE;
    }

    @Override
    public String getName() {
        return "Yellow Candle Cake";
    }

    @Override
    protected int getCandleBlockId() {
        return YELLOW_CANDLE;
    }
}
