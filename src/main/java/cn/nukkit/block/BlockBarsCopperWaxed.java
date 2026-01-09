package cn.nukkit.block;

public class BlockBarsCopperWaxed extends BlockBarsCopper {
    BlockBarsCopperWaxed() {

    }

    @Override
    public int getId() {
        return WAXED_COPPER_BARS;
    }

    @Override
    public String getName() {
        return "Waxed Copper Bars";
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
        return WAXED_EXPOSED_COPPER_BARS;
    }

    @Override
    public int getDewaxedBlockId() {
        return COPPER_BARS;
    }
}
