package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabWarped extends BlockSlabWood {
    public static final int TYPE_MASK = 0;
    public static final int TOP_SLOT_BIT = 0b1;

    public BlockSlabWarped() {
        this(0);
    }

    public BlockSlabWarped(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WARPED_SLAB;
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Warped Slab" : "Warped Slab";
    }

    @Override
    public int getBurnChance() {
        return 0;
    }

    @Override
    public int getBurnAbility() {
        return 0;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CYAN_BLOCK_COLOR;
    }

    @Override
    public int getFuelTime() {
        return 0;
    }

    @Override
    public int getSlabType() {
        return 0;
    }

    @Override
    protected int getTopSlotBit() {
        return TOP_SLOT_BIT;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return WARPED_DOUBLE_SLAB;
    }
}
