package cn.nukkit.block;

public class BlockSlabBlackstoneBrickPolished extends BlockSlabBlackstone {
    BlockSlabBlackstoneBrickPolished() {

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
