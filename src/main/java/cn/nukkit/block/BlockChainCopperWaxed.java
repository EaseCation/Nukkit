package cn.nukkit.block;

public class BlockChainCopperWaxed extends BlockChainCopper {
    public BlockChainCopperWaxed() {
        this(0);
    }

    public BlockChainCopperWaxed(int meta) {
        super(meta);

    }

    @Override
    public int getId() {
        return WAXED_COPPER_CHAIN;
    }

    @Override
    public String getName() {
        return "Waxed Copper Chain";
    }

    @Override
    public boolean isWaxed() {
        return true;
    }

    @Override
    public final int getWaxedBlockId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getIncrementAgeBlockId() {
        return WAXED_EXPOSED_COPPER_CHAIN;
    }

    @Override
    public int getDewaxedBlockId() {
        return COPPER_CHAIN;
    }
}
