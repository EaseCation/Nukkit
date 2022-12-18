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
    protected int getWaxedBlockId() {
        return WAXED_CUT_COPPER;
    }

    @Override
    protected int getIncrementAgeBlockId() {
        return EXPOSED_CUT_COPPER;
    }
}
