package cn.nukkit.block;

public class BlockDoubleSlabBlackstoneBrickPolished extends BlockDoubleSlabBlackstone {
    public BlockDoubleSlabBlackstoneBrickPolished() {
        this(0);
    }

    public BlockDoubleSlabBlackstoneBrickPolished(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return POLISHED_BLACKSTONE_BRICK_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Polished Blackstone Brick Slab";
    }

    @Override
    protected int getSlabBlockId() {
        return POLISHED_BLACKSTONE_BRICK_SLAB;
    }
}
