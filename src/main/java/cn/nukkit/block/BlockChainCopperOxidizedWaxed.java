package cn.nukkit.block;

public class BlockChainCopperOxidizedWaxed extends BlockChainCopperWaxed {
    BlockChainCopperOxidizedWaxed() {

    }

    @Override
    public int getId() {
        return WAXED_OXIDIZED_COPPER_CHAIN;
    }

    @Override
    public String getName() {
        return "Waxed Oxidized Copper Chain";
    }

    @Override
    public int getCopperAge() {
        return 3;
    }

    @Override
    public int getIncrementAgeBlockId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getDecrementAgeBlockId() {
        return WAXED_WEATHERED_COPPER_CHAIN;
    }

    @Override
    public int getDewaxedBlockId() {
        return OXIDIZED_COPPER_CHAIN;
    }
}
