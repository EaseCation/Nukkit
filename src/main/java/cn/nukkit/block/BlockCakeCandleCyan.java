package cn.nukkit.block;

public class BlockCakeCandleCyan extends BlockCakeCandle {
    BlockCakeCandleCyan() {

    }

    @Override
    public int getId() {
        return CYAN_CANDLE_CAKE;
    }

    @Override
    public String getName() {
        return "Cyan Candle Cake";
    }

    @Override
    protected int getCandleBlockId() {
        return CYAN_CANDLE;
    }
}
