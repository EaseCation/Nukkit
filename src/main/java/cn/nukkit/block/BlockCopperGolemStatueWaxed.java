package cn.nukkit.block;

public class BlockCopperGolemStatueWaxed extends BlockCopperGolemStatue {
    BlockCopperGolemStatueWaxed() {

    }

    @Override
    public int getId() {
        return WAXED_COPPER_GOLEM_STATUE;
    }

    @Override
    public String getName() {
        return "Waxed Copper Golem Statue";
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
        return WAXED_EXPOSED_COPPER_GOLEM_STATUE;
    }

    @Override
    public int getDewaxedBlockId() {
        return COPPER_GOLEM_STATUE;
    }
}
