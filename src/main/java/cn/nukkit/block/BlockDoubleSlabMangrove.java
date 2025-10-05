package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabMangrove extends BlockDoubleSlabWood {
    public static final int TYPE_MASK = 0;
    public static final int TOP_SLOT_BIT = 0b1;

    public BlockDoubleSlabMangrove() {
        this(0);
    }

    public BlockDoubleSlabMangrove(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return MANGROVE_DOUBLE_SLAB;
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public String getName() {
        return "Double Mangrove Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.RED_BLOCK_COLOR;
    }

    @Override
    public int getSlabType() {
        return 0;
    }

    @Override
    protected int getSlabBlockId() {
        return MANGROVE_SLAB;
    }
}
