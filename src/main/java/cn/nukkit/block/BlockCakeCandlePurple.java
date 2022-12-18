package cn.nukkit.block;

public class BlockCakeCandlePurple extends BlockCakeCandle {
    public BlockCakeCandlePurple() {
        this(0);
    }

    public BlockCakeCandlePurple(int meta) {
        super(meta);
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
