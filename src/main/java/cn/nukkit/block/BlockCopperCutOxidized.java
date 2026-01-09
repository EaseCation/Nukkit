package cn.nukkit.block;

public class BlockCopperCutOxidized extends BlockCopperOxidized {
    BlockCopperCutOxidized() {

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
    public int getWaxedBlockId() {
        return WAXED_OXIDIZED_CUT_COPPER;
    }

    @Override
    public int getDecrementAgeBlockId() {
        return WEATHERED_CUT_COPPER;
    }
}
