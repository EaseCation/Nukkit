package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabBambooMosaic extends BlockDoubleSlabWood {
    public static final int TYPE_MASK = 0;
    public static final int TOP_SLOT_BIT = 0b1;

    public BlockDoubleSlabBambooMosaic() {
        this(0);
    }

    public BlockDoubleSlabBambooMosaic(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BAMBOO_MOSAIC_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Bamboo Mosaic Slab";
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
        return BAMBOO_MOSAIC_SLAB;
    }
}
