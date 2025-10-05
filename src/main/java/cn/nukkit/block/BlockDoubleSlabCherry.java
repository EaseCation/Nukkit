package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabCherry extends BlockDoubleSlabWood {
    public static final int TYPE_MASK = 0;
    public static final int TOP_SLOT_BIT = 0b1;

    public BlockDoubleSlabCherry() {
        this(0);
    }

    public BlockDoubleSlabCherry(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CHERRY_DOUBLE_SLAB;
    }

    @Override
    public boolean isStackedByData() {
        return false;
    }

    @Override
    public String getName() {
        return "Double Cherry Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WHITE_TERRACOTA_BLOCK_COLOR;
    }

    @Override
    public int getSlabType() {
        return 0;
    }

    @Override
    protected int getSlabBlockId() {
        return CHERRY_SLAB;
    }
}
