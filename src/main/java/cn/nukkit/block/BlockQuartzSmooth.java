package cn.nukkit.block;

public class BlockQuartzSmooth extends BlockQuartz {
    BlockQuartzSmooth() {

    }

    @Override
    public int getId() {
        return SMOOTH_QUARTZ;
    }

    @Override
    public String getName() {
        return "Smooth Quartz Block";
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
        return "tile.quartz_block.smooth.name";
    }
}
