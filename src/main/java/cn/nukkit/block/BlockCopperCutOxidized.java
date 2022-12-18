package cn.nukkit.block;

public class BlockCopperCutOxidized extends BlockCopperOxidized {
    public BlockCopperCutOxidized() {
    }

    @Override
    public int getId() {
        return OXIDIZED_CUT_COPPER;
    }

    @Override
    public String getName() {
        return "Oxidized Cut Copper";
    }

    @Override
    protected int getWaxedBlockId() {
        return WAXED_OXIDIZED_CUT_COPPER;
    }

    @Override
    protected int getDecrementAgeBlockId() {
        return WEATHERED_CUT_COPPER;
    }
}
