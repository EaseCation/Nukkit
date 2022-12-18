package cn.nukkit.block;

public class BlockCakeCandleGreen extends BlockCakeCandle {
    public BlockCakeCandleGreen() {
        this(0);
    }

    public BlockCakeCandleGreen(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return GREEN_CANDLE_CAKE;
    }

    @Override
    public String getName() {
        return "Green Candle Cake";
    }

    @Override
    protected int getCandleBlockId() {
        return GREEN_CANDLE;
    }
}
