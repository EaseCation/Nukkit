package cn.nukkit.block;

public class BlockCopperCutExposedWaxed extends BlockCopperExposedWaxed {
    public BlockCopperCutExposedWaxed() {
    }

    @Override
    public int getId() {
        return WAXED_EXPOSED_CUT_COPPER;
    }

    @Override
    public String getName() {
        return "Waxed Exposed Cut Copper";
    }

    @Override
    protected int getIncrementAgeBlockId() {
        return WAXED_WEATHERED_CUT_COPPER;
    }

    @Override
    protected int getDewaxedBlockId() {
        return EXPOSED_CUT_COPPER;
    }

    @Override
    protected int getDecrementAgeBlockId() {
        return WAXED_CUT_COPPER;
    }
}
