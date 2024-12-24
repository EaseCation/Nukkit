package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabPaleOak extends BlockDoubleSlabWood {
    public static final int TYPE_MASK = 0;
    public static final int TOP_SLOT_BIT = 0b1;

    public BlockDoubleSlabPaleOak() {
        this(0);
    }

    public BlockDoubleSlabPaleOak(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return PALE_OAK_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Pale Oak Slab";
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
    protected int getSlabBlockId() {
        return PALE_OAK_SLAB;
    }
}
