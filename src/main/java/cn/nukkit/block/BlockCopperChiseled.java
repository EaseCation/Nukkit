package cn.nukkit.block;

public class BlockCopperChiseled extends BlockCopper {
    BlockCopperChiseled() {

    }

    @Override
    public int getId() {
        return CHISELED_COPPER;
    }

    @Override
    public String getName() {
        return "Chiseled Copper";
    }

    @Override
    public int getWaxedBlockId() {
        return WAXED_CHISELED_COPPER;
    }

    @Override
    public int getIncrementAgeBlockId() {
        return EXPOSED_CHISELED_COPPER;
    }
}
