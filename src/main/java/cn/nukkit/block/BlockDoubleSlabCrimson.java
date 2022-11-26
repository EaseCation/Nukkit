package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabCrimson extends BlockDoubleSlabWood {
    public static final int TYPE_MASK = 0;
    public static final int TOP_SLOT_BIT = 0b1;

    public BlockDoubleSlabCrimson() {
        this(0);
    }

    public BlockDoubleSlabCrimson(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CRIMSON_DOUBLE_SLAB;
    }

    @Override
    public String getName() {
        return "Double Crimson Slab";
    }

    @Override
    public int getBurnChance() {
        return 0;
    }

    @Override
    public int getBurnAbility() {
        return 0;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.NETHER_BLOCK_COLOR;
    }

    @Override
    public int getSlabType() {
        return 0;
    }

    @Override
    protected int getSlabBlockId() {
        return CRIMSON_SLAB;
    }
}
