package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

import static cn.nukkit.GameVersion.*;
import static cn.nukkit.SharedConstants.*;

/**
 * Created by CreeperFace on 26. 11. 2016.
 */
public class BlockSlabRedSandstone extends BlockSlabStone {

    public static final int TYPE_RED_SANDSTONE = 0;
    public static final int TYPE_PURPUR = 1;
    public static final int TYPE_PRISMARINE_ROUGH = 2;
    public static final int TYPE_PRISMARINE_DARK = 3;
    public static final int TYPE_PRISMARINE_BRICK = 4;
    public static final int TYPE_MOSSY_COBBLESTONE = 5;
    public static final int TYPE_SMOOTH_SANDSTONE = 6;
    public static final int TYPE_RED_NETHER_BRICK = 7;

    private static final String[] NAMES = new String[]{
            "Red Sandstone Slab",
            "Purpur Slab",
            "Prismarine Slab",
            "Dark Prismarine Slab",
            "Prismarine Brick Slab",
            "Mossy Cobblestone Slab",
            "Smooth Sandstone Slab",
            "Red Nether Brick Slab",
    };

    public BlockSlabRedSandstone() {
        this(0);
    }

    public BlockSlabRedSandstone(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STONE_SLAB2;
    }

    @Override
    public boolean isStackedByData() {
        return !V1_21_20.isAvailable();
    }

    @Override
    public String getName() {
        return (isTopSlot() ? "Upper " : "") + NAMES[this.getSlabType()];
    }

    @Override
    public float getHardness() {
        if (ENABLE_BLOCK_DESTROY_SPEED_COMPATIBILITY || V1_21_20.isAvailable()) {
            switch (getSlabType()) {
                case TYPE_PRISMARINE_ROUGH:
                case TYPE_PRISMARINE_DARK:
                case TYPE_PRISMARINE_BRICK:
                    return 1.5f;
            }
        }
        return super.getHardness();
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
    protected int getDoubleSlabBlockId() {
        return DOUBLE_STONE_SLAB2;
    }
}