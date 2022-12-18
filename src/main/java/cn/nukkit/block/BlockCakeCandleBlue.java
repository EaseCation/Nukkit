package cn.nukkit.block;

public class BlockCakeCandleBlue extends BlockCakeCandle {
    public BlockCakeCandleBlue() {
        this(0);
    }

    public BlockCakeCandleBlue(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BLUE_CANDLE_CAKE;
    }

    @Override
    public String getName() {
        return "Blue Candle Cake";
    }

    @Override
    protected int getCandleBlockId() {
        return BLUE_CANDLE;
    }
}
