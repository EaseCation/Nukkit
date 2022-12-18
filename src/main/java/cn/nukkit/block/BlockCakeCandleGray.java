package cn.nukkit.block;

public class BlockCakeCandleGray extends BlockCakeCandle {
    public BlockCakeCandleGray() {
        this(0);
    }

    public BlockCakeCandleGray(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return GRAY_CANDLE_CAKE;
    }

    @Override
    public String getName() {
        return "Gray Candle Cake";
    }

    @Override
    protected int getCandleBlockId() {
        return GRAY_CANDLE;
    }
}
