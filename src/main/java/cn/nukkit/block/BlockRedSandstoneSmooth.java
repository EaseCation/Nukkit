package cn.nukkit.block;

public class BlockRedSandstoneSmooth extends BlockRedSandstone {
    BlockRedSandstoneSmooth() {

    }

    @Override
    public int getId() {
        return SMOOTH_RED_SANDSTONE;
    }

    @Override
    public String getName() {
        return "Smooth Red Sandstone";
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
        return "tile.red_sandstone.smooth.name";
    }
}
