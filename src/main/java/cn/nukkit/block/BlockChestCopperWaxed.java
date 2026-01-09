package cn.nukkit.block;

public class BlockChestCopperWaxed extends BlockChestCopper {
    BlockChestCopperWaxed() {

    }

    @Override
    public int getId() {
        return WAXED_COPPER_CHEST;
    }

    @Override
    public String getName() {
        return "Waxed Copper Chest";
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
        return WAXED_EXPOSED_COPPER_CHEST;
    }

    @Override
    public int getDewaxedBlockId() {
        return COPPER_CHEST;
    }
}
