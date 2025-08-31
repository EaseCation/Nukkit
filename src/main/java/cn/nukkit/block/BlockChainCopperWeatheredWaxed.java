package cn.nukkit.block;

public class BlockChainCopperWeatheredWaxed extends BlockChainCopperWaxed {
    public BlockChainCopperWeatheredWaxed() {
        this(0);
    }

    public BlockChainCopperWeatheredWaxed(int meta) {
        super(meta);

    }

    @Override
    public int getId() {
        return WAXED_WEATHERED_COPPER_CHAIN;
    }

    @Override
    public String getName() {
        return "Waxed Weathered Copper Chain";
    }

    @Override
    public int getCopperAge() {
        return 2;
    }

    @Override
    public int getIncrementAgeBlockId() {
        return WAXED_OXIDIZED_COPPER_CHAIN;
    }

    @Override
    public int getDecrementAgeBlockId() {
        return WAXED_EXPOSED_COPPER_CHAIN;
    }

    @Override
    public int getDewaxedBlockId() {
        return WEATHERED_COPPER_CHAIN;
    }
}
