package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabMangrove extends BlockSlabWood {
    public static final int TYPE_MASK = 0;
    public static final int TOP_SLOT_BIT = 0b1;

    public BlockSlabMangrove() {
        this(0);
    }

    public BlockSlabMangrove(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return MANGROVE_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Mangrove Slab" : "Mangrove Slab";
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
    protected int getTopSlotBit() {
        return TOP_SLOT_BIT;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return MANGROVE_DOUBLE_SLAB;
    }
}
