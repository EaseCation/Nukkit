package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockSlabStone4 extends BlockSlabStone {

    public static final int TYPE_MOSSY_STONE_BRICK = 0;
    public static final int TYPE_SMOOTH_QUARTZ = 1;
    public static final int TYPE_STONE = 2;
    public static final int TYPE_CUT_SANDSTONE = 3;
    public static final int TYPE_CUT_RED_SANDSTONE = 4;

    private static final String[] NAMES = new String[]{
            "Mossy Stone Brick Slab",
            "Smooth Quartz Slab",
            "Stone Slab",
            "Cut Sandstone Slab",
            "Cut Red Sandstone Slab",
            "Slab",
            "Slab",
            "Slab",
    };

    public BlockSlabStone4() {
        this(0);
    }

    public BlockSlabStone4(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STONE_SLAB4;
    }

    @Override
    public String getName() {
        return (isTopSlot() ? "Upper " : "") + NAMES[getSlabType()];
    }

    @Override
    public BlockColor getColor() {
        switch (this.getSlabType()) {
            default:
            case TYPE_MOSSY_STONE_BRICK:
            case TYPE_STONE:
                return BlockColor.STONE_BLOCK_COLOR;
            case TYPE_SMOOTH_QUARTZ:
                return BlockColor.QUARTZ_BLOCK_COLOR;
            case TYPE_CUT_SANDSTONE:
                return BlockColor.SAND_BLOCK_COLOR;
            case TYPE_CUT_RED_SANDSTONE:
                return BlockColor.ORANGE_BLOCK_COLOR;
        }
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return DOUBLE_STONE_SLAB4;
    }
}
