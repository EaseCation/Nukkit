package cn.nukkit.block;

public class BlockCopperCutExposed extends BlockCopperExposed {
    public BlockCopperCutExposed() {
    }

    @Override
    public int getId() {
        return EXPOSED_CUT_COPPER;
    }

    @Override
    public String getName() {
        return "Exposed Cut Copper";
    }

    @Override
    protected int getWaxedBlockId() {
        return WAXED_EXPOSED_CUT_COPPER;
    }

    @Override
    protected int getIncrementAgeBlockId() {
        return WEATHERED_CUT_COPPER;
    }

    @Override
    protected int getDecrementAgeBlockId() {
        return CUT_COPPER;
    }
}
