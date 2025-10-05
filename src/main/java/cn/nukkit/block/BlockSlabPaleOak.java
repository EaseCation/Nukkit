package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabPaleOak extends BlockSlabWood {
    public static final int TYPE_MASK = 0;
    public static final int TOP_SLOT_BIT = 0b1;

    public BlockSlabPaleOak() {
        this(0);
    }

    public BlockSlabPaleOak(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return PALE_OAK_SLAB;
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Pale Oak Slab" : "Pale Oak Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.QUARTZ_BLOCK_COLOR;
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
        return PALE_OAK_DOUBLE_SLAB;
    }
}
