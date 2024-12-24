package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabCherry extends BlockSlabWood {
    public static final int TYPE_MASK = 0;
    public static final int TOP_SLOT_BIT = 0b1;

    public BlockSlabCherry() {
        this(0);
    }

    public BlockSlabCherry(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CHERRY_SLAB;
    }

    @Override
    public String getName() {
        return isTopSlot() ? "Upper Cherry Slab" : "Cherry Slab";
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
    protected int getTopSlotBit() {
        return TOP_SLOT_BIT;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return CHERRY_DOUBLE_SLAB;
    }
}
