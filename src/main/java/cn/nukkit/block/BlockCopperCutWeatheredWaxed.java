package cn.nukkit.block;

public class BlockCopperCutWeatheredWaxed extends BlockCopperWeatheredWaxed {
    BlockCopperCutWeatheredWaxed() {

    }

    @Override
    public int getId() {
        return WAXED_WEATHERED_CUT_COPPER;
    }

    @Override
    public String getName() {
        return "Waxed Weathered Cut Copper";
    }

    @Override
    public int getIncrementAgeBlockId() {
        return WAXED_OXIDIZED_CUT_COPPER;
    }

    @Override
    public int getDecrementAgeBlockId() {
        return WAXED_EXPOSED_CUT_COPPER;
    }

    @Override
    public int getDewaxedBlockId() {
        return WEATHERED_CUT_COPPER;
    }
}
