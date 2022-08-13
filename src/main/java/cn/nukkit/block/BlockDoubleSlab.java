package cn.nukkit.block;

public abstract class BlockDoubleSlab extends BlockSolidMeta {

    public static final int TYPE_MASK = 0b111;
    public static final int TOP_SLOT_BIT = 0b1000;

    protected BlockDoubleSlab() {
        this(0);
    }

    protected BlockDoubleSlab(int meta) {
        super(meta);
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public boolean isSlab() {
        return true;
    }
}
