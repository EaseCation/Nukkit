package cn.nukkit.block;

public class BlockCopperCutWeathered extends BlockCopperWeathered {
    public BlockCopperCutWeathered() {
    }

    @Override
    public int getId() {
        return WEATHERED_CUT_COPPER;
    }

    @Override
    public String getName() {
        return "Weathered Cut Copper";
    }

    @Override
    protected int getWaxedBlockId() {
        return WAXED_WEATHERED_CUT_COPPER;
    }

    @Override
    protected int getIncrementAgeBlockId() {
        return OXIDIZED_CUT_COPPER;
    }

    @Override
    protected int getDecrementAgeBlockId() {
        return EXPOSED_CUT_COPPER;
    }
}
