package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabStone3 extends BlockDoubleSlabStone {

    public static final int TYPE_END_STONE_BRICK = 0;
    public static final int TYPE_SMOOTH_RED_SANDSTONE = 1;
    public static final int TYPE_POLISHED_ANDESITE = 2;
    public static final int TYPE_ANDESITE = 3;
    public static final int TYPE_DIORITE = 4;
    public static final int TYPE_POLISHED_DIORITE = 5;
    public static final int TYPE_GRANITE = 6;
    public static final int TYPE_POLISHED_GRANITE = 7;

    private static final String[] NAMES = new String[]{
            "Double End Stone Brick Slab",
            "Double Smooth Red Sandstone Slab",
            "Double Polished Andesite Slab",
            "Double Andesite Slab",
            "Double Diorite Slab",
            "Double Polished Diorite Slab",
            "Double Granite Slab",
            "Double Polished Granite Slab",
    };

    public BlockDoubleSlabStone3() {
        this(0);
    }

    public BlockDoubleSlabStone3(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return DOUBLE_STONE_SLAB3;
    }

    @Override
    public String getName() {
        return NAMES[getSlabType()];
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
    protected int getSlabBlockId() {
        return STONE_SLAB3;
    }
}
