package cn.nukkit.block;

public class BlockCakeCandleRed extends BlockCakeCandle {
    public BlockCakeCandleRed() {
        this(0);
    }

    public BlockCakeCandleRed(int meta) {
        super(meta);
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
