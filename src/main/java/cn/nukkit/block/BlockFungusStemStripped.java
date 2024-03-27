package cn.nukkit.block;

public abstract class BlockFungusStemStripped extends BlockRotatedPillar {
    protected BlockFungusStemStripped(int meta) {
        super(meta);
    }

    @Override
    public float getHardness() {
        return 2;
    }

    @Override
    public float getResistance() {
        return 10;
    }

    @Override
    public int getToolType() {
        return BlockToolType.AXE;
    }
}
