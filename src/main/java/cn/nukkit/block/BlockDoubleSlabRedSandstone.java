package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

/**
 * Created by CreeperFace on 26. 11. 2016.
 */
public class BlockDoubleSlabRedSandstone extends BlockDoubleSlabStone {

    public static final int TYPE_RED_SANDSTONE = 0;
    public static final int TYPE_PURPUR = 1;
    public static final int TYPE_PRISMARINE_ROUGH = 2;
    public static final int TYPE_PRISMARINE_DARK = 3;
    public static final int TYPE_PRISMARINE_BRICK = 4;
    public static final int TYPE_MOSSY_COBBLESTONE = 5;
    public static final int TYPE_SMOOTH_SANDSTONE = 6;
    public static final int TYPE_RED_NETHER_BRICK = 7;

    private static final String[] NAMES = new String[]{
            "Double Red Sandstone Slab",
            "Double Purpur Slab",
            "Double Prismarine Slab",
            "Double Dark Prismarine Slab",
            "Double Prismarine Brick Slab",
            "Double Mossy Cobblestone Slab",
            "Double Smooth Sandstone Slab",
            "Double Red Nether Brick Slab",
    };

    public BlockDoubleSlabRedSandstone() {
        this(0);
    }

    public BlockDoubleSlabRedSandstone(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return DOUBLE_STONE_SLAB2;
    }

    @Override
    public String getName() {
        return NAMES[this.getSlabType()];
    }

    @Override
    public BlockColor getColor() {
        switch (this.getSlabType()) {
            default:
            case TYPE_RED_SANDSTONE:
                return BlockColor.ORANGE_BLOCK_COLOR;
            case TYPE_PURPUR:
                return BlockColor.PURPLE_BLOCK_COLOR;
            case TYPE_PRISMARINE_ROUGH:
                return BlockColor.CYAN_BLOCK_COLOR;
            case TYPE_PRISMARINE_DARK:
            case TYPE_PRISMARINE_BRICK:
                return BlockColor.DIAMOND_BLOCK_COLOR;
            case TYPE_MOSSY_COBBLESTONE:
                return BlockColor.STONE_BLOCK_COLOR;
            case TYPE_SMOOTH_SANDSTONE:
                return BlockColor.SAND_BLOCK_COLOR;
            case TYPE_RED_NETHER_BRICK:
                return BlockColor.NETHER_BLOCK_COLOR;
        }
    }

    @Override
    protected int getSlabBlockId() {
        return STONE_SLAB2;
    }
}