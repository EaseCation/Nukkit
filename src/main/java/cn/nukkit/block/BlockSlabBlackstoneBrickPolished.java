package cn.nukkit.block;

public class BlockSlabBlackstoneBrickPolished extends BlockSlabBlackstone {
    public BlockSlabBlackstoneBrickPolished() {
        this(0);
    }

    public BlockSlabBlackstoneBrickPolished(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return POLISHED_BLACKSTONE_BRICK_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Polished Blackstone Brick Slab" : "Polished Blackstone Brick Slab";
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return POLISHED_BLACKSTONE_BRICK_DOUBLE_SLAB;
    }
}
