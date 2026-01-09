package cn.nukkit.block;

public class BlockCakeCandleBlack extends BlockCakeCandle {
    BlockCakeCandleBlack() {

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
