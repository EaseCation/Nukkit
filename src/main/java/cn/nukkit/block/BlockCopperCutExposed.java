package cn.nukkit.block;

public class BlockCopperCutExposed extends BlockCopperExposed {
    BlockCopperCutExposed() {

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
    public int getWaxedBlockId() {
        return WAXED_EXPOSED_CUT_COPPER;
    }

    @Override
    public int getIncrementAgeBlockId() {
        return WEATHERED_CUT_COPPER;
    }

    @Override
    public int getDecrementAgeBlockId() {
        return CUT_COPPER;
    }
}
