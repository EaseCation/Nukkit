package cn.nukkit.block;

public class BlockSandstoneSmooth extends BlockSandstone {
    BlockSandstoneSmooth() {

    }

    @Override
    public int getId() {
        return SMOOTH_SANDSTONE;
    }

    @Override
    public String getName() {
        return "Smooth Sandstone";
    }

    @Override
    public float getHardness() {
        return 2;
    }

    @Override
    public float getResistance() {
        return 30;
    }

    @Override
    public float getFurnaceXpMultiplier() {
        return 0.1f;
    }

    @Override
    public String getDescriptionId() {
        return "tile.sandstone.smooth.name";
    }
}
