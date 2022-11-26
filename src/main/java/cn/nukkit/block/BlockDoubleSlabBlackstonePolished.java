package cn.nukkit.block;

public class BlockDoubleSlabBlackstonePolished extends BlockDoubleSlabBlackstone {
    public BlockDoubleSlabBlackstonePolished() {
        this(0);
    }

    public BlockDoubleSlabBlackstonePolished(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return POLISHED_BLACKSTONE_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Polished Blackstone Slab";
    }

    @Override
    protected int getSlabBlockId() {
        return POLISHED_BLACKSTONE_SLAB;
    }
}
