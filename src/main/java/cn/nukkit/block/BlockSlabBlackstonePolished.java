package cn.nukkit.block;

public class BlockSlabBlackstonePolished extends BlockSlabBlackstone {
    public BlockSlabBlackstonePolished() {
        this(0);
    }

    public BlockSlabBlackstonePolished(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return POLISHED_BLACKSTONE_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Polished Blackstone Slab" : "Polished Blackstone Slab";
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return POLISHED_BLACKSTONE_DOUBLE_SLAB;
    }
}
