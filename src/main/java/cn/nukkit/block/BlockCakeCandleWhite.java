package cn.nukkit.block;

public class BlockCakeCandleWhite extends BlockCakeCandle {
    public BlockCakeCandleWhite() {
        this(0);
    }

    public BlockCakeCandleWhite(int meta) {
        super(meta);
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
