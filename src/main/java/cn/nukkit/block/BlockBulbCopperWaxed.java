package cn.nukkit.block;

public class BlockBulbCopperWaxed extends BlockBulbCopper {
    public BlockBulbCopperWaxed() {
        this(0);
    }

    public BlockBulbCopperWaxed(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WAXED_COPPER_BULB;
    }

    @Override
    public String getName() {
        return "Waxed Copper Bulb";
    }

    @Override
    public boolean isWaxed() {
        return true;
    }

    @Override
    public final int getWaxedBlockId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getIncrementAgeBlockId() {
        return WAXED_EXPOSED_COPPER_BULB;
    }

    @Override
    public int getDewaxedBlockId() {
        return COPPER_BULB;
    }
}
