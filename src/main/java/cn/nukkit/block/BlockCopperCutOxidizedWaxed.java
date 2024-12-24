package cn.nukkit.block;

public class BlockCopperCutOxidizedWaxed extends BlockCopperOxidizedWaxed {
    public BlockCopperCutOxidizedWaxed() {
    }

    @Override
    public int getId() {
        return WAXED_OXIDIZED_CUT_COPPER;
    }

    @Override
    public String getName() {
        return "Waxed Oxidized Cut Copper";
    }

    @Override
    public int getDecrementAgeBlockId() {
        return WAXED_WEATHERED_CUT_COPPER;
    }

    @Override
    public int getDewaxedBlockId() {
        return OXIDIZED_CUT_COPPER;
    }
}
