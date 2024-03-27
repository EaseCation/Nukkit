package cn.nukkit.block;

public abstract class BlockFroglight extends BlockRotatedPillar {
    protected BlockFroglight(int meta) {
        super(meta);
    }

    @Override
    public float getHardness() {
        return 0.3f;
    }

    @Override
    public float getResistance() {
        return 1.5f;
    }

    @Override
    public int getLightLevel() {
        return 15;
    }
}
