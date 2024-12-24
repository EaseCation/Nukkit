package cn.nukkit.block;

public class BlockCopperCut extends BlockCopper {
    public BlockCopperCut() {
    }

    @Override
    public int getId() {
        return CUT_COPPER;
    }

    @Override
    public String getName() {
        return "Cut Copper";
    }

    @Override
    public int getWaxedBlockId() {
        return WAXED_CUT_COPPER;
    }

    @Override
    public int getIncrementAgeBlockId() {
        return EXPOSED_CUT_COPPER;
    }
}
