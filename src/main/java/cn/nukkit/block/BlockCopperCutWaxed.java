package cn.nukkit.block;

public class BlockCopperCutWaxed extends BlockCopperWaxed {
    BlockCopperCutWaxed() {

    }

    @Override
    public int getId() {
        return WAXED_CUT_COPPER;
    }

    @Override
    public String getName() {
        return "Waxed Cut Copper";
    }

    @Override
    public int getIncrementAgeBlockId() {
        return WAXED_EXPOSED_CUT_COPPER;
    }

    @Override
    public int getDewaxedBlockId() {
        return CUT_COPPER;
    }
}
