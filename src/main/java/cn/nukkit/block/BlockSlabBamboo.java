package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabBamboo extends BlockSlabWood {
    public static final int TYPE_MASK = 0;
    public static final int TOP_SLOT_BIT = 0b1;

    public BlockSlabBamboo() {
        this(0);
    }

    public BlockSlabBamboo(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BAMBOO_SLAB;
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Bamboo Slab" : "Bamboo Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.YELLOW_BLOCK_COLOR;
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
        return BAMBOO_DOUBLE_SLAB;
    }
}
