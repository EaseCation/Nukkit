package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabBamboo extends BlockDoubleSlabWood {
    public static final int TYPE_MASK = 0;
    public static final int TOP_SLOT_BIT = 0b1;

    public BlockDoubleSlabBamboo() {
        this(0);
    }

    public BlockDoubleSlabBamboo(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BAMBOO_DOUBLE_SLAB;
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public String getName() {
        return "Double Bamboo Slab";
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
    protected int getSlabBlockId() {
        return BAMBOO_SLAB;
    }
}
