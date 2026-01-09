package cn.nukkit.block;

public class BlockChainCopperExposedWaxed extends BlockChainCopperWaxed {
    BlockChainCopperExposedWaxed() {

    }

    @Override
    public int getId() {
        return WAXED_EXPOSED_COPPER_CHAIN;
    }

    @Override
    public String getName() {
        return "Waxed Exposed Copper Chain";
    }

    @Override
    public int getCopperAge() {
        return 1;
    }

    @Override
    public int getIncrementAgeBlockId() {
        return WAXED_WEATHERED_COPPER_CHAIN;
    }

    @Override
    public int getDecrementAgeBlockId() {
        return WAXED_COPPER_CHAIN;
    }

    @Override
    public int getDewaxedBlockId() {
        return EXPOSED_COPPER_CHAIN;
    }
}
