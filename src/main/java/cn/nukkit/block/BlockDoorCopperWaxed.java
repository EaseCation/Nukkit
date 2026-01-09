package cn.nukkit.block;

public class BlockDoorCopperWaxed extends BlockDoorCopper {
    BlockDoorCopperWaxed() {

    }

    @Override
    public int getId() {
        return WAXED_COPPER_DOOR;
    }

    @Override
    public String getName() {
        return "Waxed Copper Door";
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
        return WAXED_EXPOSED_COPPER_DOOR;
    }

    @Override
    public int getDewaxedBlockId() {
        return COPPER_DOOR;
    }
}
