package cn.nukkit.block;

public class BlockChainCopperOxidized extends BlockChainCopper {
    public BlockChainCopperOxidized() {
        this(0);
    }

    public BlockChainCopperOxidized(int meta) {
        super(meta);

    }

    @Override
    public int getId() {
        return OXIDIZED_COPPER_CHAIN;
    }

    @Override
    public String getName() {
        return "Oxidized Copper Chain";
    }

    @Override
    public int getCopperAge() {
        return 3;
    }

    @Override
    public int getWaxedBlockId() {
        return WAXED_OXIDIZED_COPPER_CHAIN;
    }

    @Override
    public int getIncrementAgeBlockId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getDecrementAgeBlockId() {
        return WEATHERED_COPPER_CHAIN;
    }
}
