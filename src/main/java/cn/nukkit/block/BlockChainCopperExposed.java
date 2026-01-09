package cn.nukkit.block;

public class BlockChainCopperExposed extends BlockChainCopper {
    BlockChainCopperExposed() {

    }

    @Override
    public int getId() {
        return EXPOSED_COPPER_CHAIN;
    }

    @Override
    public String getName() {
        return "Exposed Copper Chain";
    }

    @Override
    public int getCopperAge() {
        return 1;
    }

    @Override
    public int getWaxedBlockId() {
        return WAXED_EXPOSED_COPPER_CHAIN;
    }

    @Override
    public int getIncrementAgeBlockId() {
        return WEATHERED_COPPER_CHAIN;
    }

    @Override
    public int getDecrementAgeBlockId() {
        return COPPER_CHAIN;
    }
}
