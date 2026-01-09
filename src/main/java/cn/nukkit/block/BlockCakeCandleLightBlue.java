package cn.nukkit.block;

public class BlockCakeCandleLightBlue extends BlockCakeCandle {
    BlockCakeCandleLightBlue() {

    }

    @Override
    public int getId() {
        return LIGHT_BLUE_CANDLE_CAKE;
    }

    @Override
    public String getName() {
        return "Light Blue Candle Cake";
    }

    @Override
    protected int getCandleBlockId() {
        return LIGHT_BLUE_CANDLE;
    }
}
