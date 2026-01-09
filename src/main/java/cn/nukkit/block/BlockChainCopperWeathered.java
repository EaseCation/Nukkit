package cn.nukkit.block;

public class BlockChainCopperWeathered extends BlockChainCopper {
    BlockChainCopperWeathered() {

    }

    @Override
    public int getId() {
        return WEATHERED_COPPER_CHAIN;
    }

    @Override
    public String getName() {
        return "Weathered Copper Chain";
    }

    @Override
    public int getCopperAge() {
        return 2;
    }

    @Override
    public int getWaxedBlockId() {
        return WAXED_WEATHERED_COPPER_CHAIN;
    }

    @Override
    public int getIncrementAgeBlockId() {
        return OXIDIZED_COPPER_CHAIN;
    }

    @Override
    public int getDecrementAgeBlockId() {
        return EXPOSED_COPPER_CHAIN;
    }
}
