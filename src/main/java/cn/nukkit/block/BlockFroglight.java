package cn.nukkit.block;

public abstract class BlockFroglight extends BlockRotatedPillar {
    protected BlockFroglight(int meta) {
        super(meta);
    }

    @Override
    public double getHardness() {
        return 0.3;
    }

    @Override
    public double getResistance() {
        return 1.5;
    }

    @Override
    public int getLightLevel() {
        return 15;
    }
}
