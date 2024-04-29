package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabStone3 extends BlockSlabStone {

    public static final int TYPE_END_STONE_BRICK = 0;
    public static final int TYPE_SMOOTH_RED_SANDSTONE = 1;
    public static final int TYPE_POLISHED_ANDESITE = 2;
    public static final int TYPE_ANDESITE = 3;
    public static final int TYPE_DIORITE = 4;
    public static final int TYPE_POLISHED_DIORITE = 5;
    public static final int TYPE_GRANITE = 6;
    public static final int TYPE_POLISHED_GRANITE = 7;

    private static final String[] NAMES = new String[]{
            "End Stone Brick Slab",
            "Smooth Red Sandstone Slab",
            "Polished Andesite Slab",
            "Andesite Slab",
            "Diorite Slab",
            "Polished Diorite Slab",
            "Granite Slab",
            "Polished Granite Slab",
    };

    public BlockSlabStone3() {
        this(0);
    }

    public BlockSlabStone3(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STONE_SLAB3;
    }

    @Override
    public String getName() {
        return (isTopSlot() ? "Upper " : "") + NAMES[getSlabType()];
    }

    @Override
    public BlockColor getColor() {
        switch (this.getSlabType()) {
            default:
            case TYPE_END_STONE_BRICK:
                return BlockColor.SAND_BLOCK_COLOR;
            case TYPE_SMOOTH_RED_SANDSTONE:
                return BlockColor.ORANGE_BLOCK_COLOR;
            case TYPE_POLISHED_ANDESITE:
            case TYPE_ANDESITE:
                return BlockColor.STONE_BLOCK_COLOR;
            case TYPE_DIORITE:
            case TYPE_POLISHED_DIORITE:
                return BlockColor.QUARTZ_BLOCK_COLOR;
            case TYPE_GRANITE:
            case TYPE_POLISHED_GRANITE:
                return BlockColor.DIRT_BLOCK_COLOR;
        }
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return DOUBLE_STONE_SLAB3;
    }
}
