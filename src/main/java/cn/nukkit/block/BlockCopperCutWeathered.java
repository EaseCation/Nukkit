package cn.nukkit.block;

public class BlockCopperCutWeathered extends BlockCopperWeathered {
    BlockCopperCutWeathered() {

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
    public int getWaxedBlockId() {
        return WAXED_WEATHERED_CUT_COPPER;
    }

    @Override
    public int getIncrementAgeBlockId() {
        return OXIDIZED_CUT_COPPER;
    }

    @Override
    public int getDecrementAgeBlockId() {
        return EXPOSED_CUT_COPPER;
    }
}
