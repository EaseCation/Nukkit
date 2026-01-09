package cn.nukkit.block;

public class BlockTrapdoorCopperWaxed extends BlockTrapdoorCopper {
    BlockTrapdoorCopperWaxed() {

    }

    @Override
    public int getId() {
        return WAXED_COPPER_TRAPDOOR;
    }

    @Override
    public String getName() {
        return "Waxed Copper Trapdoor";
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
        return WAXED_EXPOSED_COPPER_TRAPDOOR;
    }

    @Override
    public int getDewaxedBlockId() {
        return COPPER_TRAPDOOR;
    }
}
