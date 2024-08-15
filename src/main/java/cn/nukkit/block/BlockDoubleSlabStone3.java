package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

import static cn.nukkit.GameVersion.*;
import static cn.nukkit.SharedConstants.*;

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
    public float getHardness() {
        switch (getSlabType()) {
            case TYPE_ANDESITE:
            case TYPE_POLISHED_ANDESITE:
            case TYPE_DIORITE:
            case TYPE_POLISHED_DIORITE:
            case TYPE_GRANITE:
            case TYPE_POLISHED_GRANITE:
                if (ENABLE_BLOCK_DESTROY_SPEED_COMPATIBILITY || V1_21_20.isAvailable()) {
                    return 1.5f;
                }
                break;
            case TYPE_END_STONE_BRICK:
                if (V1_21_20.isAvailable()) {
                    return 3;
                }
                break;
        }
        return super.getHardness();
    }

    @Override
    public float getResistance() {
        if (getSlabType() == TYPE_END_STONE_BRICK) {
            return 45;
        }
        return super.getResistance();
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
