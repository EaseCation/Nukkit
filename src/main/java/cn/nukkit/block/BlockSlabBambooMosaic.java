package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabBambooMosaic extends BlockSlabWood {
    public static final int TYPE_MASK = 0;
    public static final int TOP_SLOT_BIT = 0b1;

    public BlockSlabBambooMosaic() {
        this(0);
    }

    public BlockSlabBambooMosaic(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BAMBOO_MOSAIC_SLAB;
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Bamboo Mosaic Slab" : "Bamboo Mosaic Slab";
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
        return BAMBOO_MOSAIC_DOUBLE_SLAB;
    }
}
