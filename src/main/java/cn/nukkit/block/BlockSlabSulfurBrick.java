package cn.nukkit.block;

public class BlockSlabSulfurBrick extends BlockSlabSulfur {
    BlockSlabSulfurBrick() {
    }

    @Override
    public int getId() {
        return SULFUR_BRICK_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Sulfur Brick Slab" : "Sulfur Brick Slab";
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return SULFUR_BRICK_DOUBLE_SLAB;
    }
}
