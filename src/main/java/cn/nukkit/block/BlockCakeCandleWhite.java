package cn.nukkit.block;

public class BlockCakeCandleWhite extends BlockCakeCandle {
    BlockCakeCandleWhite() {

    }

    @Override
    public int getId() {
        return WHITE_CANDLE_CAKE;
    }

    @Override
    public String getName() {
        return "White Candle Cake";
    }

    @Override
    protected int getCandleBlockId() {
        return WHITE_CANDLE;
    }
}
