package cn.nukkit.block;

public class BlockCakeCandleCyan extends BlockCakeCandle {
    public BlockCakeCandleCyan() {
        this(0);
    }

    public BlockCakeCandleCyan(int meta) {
        super(meta);
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
