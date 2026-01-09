package cn.nukkit.block;

public class BlockCakeCandleRed extends BlockCakeCandle {
    BlockCakeCandleRed() {

    }

    @Override
    public int getId() {
        return RED_CANDLE_CAKE;
    }

    @Override
    public String getName() {
        return "Red Candle Cake";
    }

    @Override
    protected int getCandleBlockId() {
        return RED_CANDLE;
    }
}
