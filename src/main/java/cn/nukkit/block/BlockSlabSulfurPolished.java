package cn.nukkit.block;

public class BlockSlabSulfurPolished extends BlockSlabSulfur {
    BlockSlabSulfurPolished() {
    }

    @Override
    public int getId() {
        return POLISHED_SULFUR_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Polished Sulfur Slab" : "Polished Sulfur Slab";
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return POLISHED_SULFUR_DOUBLE_SLAB;
    }
}
