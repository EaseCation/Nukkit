package cn.nukkit.block;

public class BlockLanternCopperWaxed extends BlockLanternCopper {
    BlockLanternCopperWaxed() {

    }

    @Override
    public int getId() {
        return WAXED_COPPER_LANTERN;
    }

    @Override
    public String getName() {
        return "Waxed Copper Lantern";
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
        return WAXED_EXPOSED_COPPER_LANTERN;
    }

    @Override
    public int getDewaxedBlockId() {
        return COPPER_LANTERN;
    }
}
