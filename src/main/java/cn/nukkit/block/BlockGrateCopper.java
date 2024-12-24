package cn.nukkit.block;

public class BlockGrateCopper extends BlockCopper {
    public BlockGrateCopper() {
    }

    @Override
    public int getId() {
        return COPPER_GRATE;
    }

    @Override
    public String getName() {
        return "Copper Grate";
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public int getWaxedBlockId() {
        return WAXED_COPPER_GRATE;
    }

    @Override
    public int getIncrementAgeBlockId() {
        return EXPOSED_COPPER_GRATE;
    }
}
