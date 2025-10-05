package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

import static cn.nukkit.GameVersion.*;

/**
 * Created on 2015/12/2 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockSlabWood extends BlockSlab {

    public static final int OAK = 0;
    public static final int SPRUCE = 1;
    public static final int BIRCH = 2;
    public static final int JUNGLE = 3;
    public static final int ACACIA = 4;
    public static final int DARK_OAK = 5;

    private static final String[] NAMES = new String[]{
            "Oak Wood Slab",
            "Spruce Wood Slab",
            "Birch Wood Slab",
            "Jungle Wood Slab",
            "Acacia Wood Slab",
            "Dark Oak Wood Slab",
            "Wood Slab",
            "Wood Slab",
    };

    public BlockSlabWood() {
        this(0);
    }

    public BlockSlabWood(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return (isTopSlot() ? "Upper " : "") + NAMES[this.getSlabType()];
    }

    @Override
    public int getId() {
        return WOODEN_SLAB;
    }

    @Override
    public boolean isStackedByData() {
        return !V1_20_70.isAvailable();
    }

    @Override
    public float getHardness() {
        return 2;
    }

    @Override
    public float getResistance() {
        return 15;
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int getBurnAbility() {
        return 20;
    }

    @Override
    public int getToolType() {
        return BlockToolType.AXE;
    }

    @Override
    public BlockColor getColor() {
        switch (getSlabType()) {
            default:
            case OAK:
                return BlockColor.WOOD_BLOCK_COLOR;
            case SPRUCE:
                return BlockColor.PODZOL_BLOCK_COLOR;
            case BIRCH:
                return BlockColor.SAND_BLOCK_COLOR;
            case JUNGLE:
                return BlockColor.DIRT_BLOCK_COLOR;
            case ACACIA:
                return BlockColor.ORANGE_BLOCK_COLOR;
            case DARK_OAK:
                return BlockColor.BROWN_BLOCK_COLOR;
        }
    }

    @Override
    public int getFuelTime() {
        return 300;
    }

    @Override
    protected int getDoubleSlabBlockId() {
        return DOUBLE_WOODEN_SLAB;
    }
}
