package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockPlanks extends BlockSolid {
    public static final int OAK = 0;
    public static final int SPRUCE = 1;
    public static final int BIRCH = 2;
    public static final int JUNGLE = 3;
    public static final int ACACIA = 4;
    public static final int DARK_OAK = 5;

    private static final String[] NAMES = new String[]{
            "Oak Wood Planks",
            "Spruce Wood Planks",
            "Birch Wood Planks",
            "Jungle Wood Planks",
            "Acacia Wood Planks",
            "Dark Oak Wood Planks",
            "Planks",
            "Planks",
    };

    public BlockPlanks() {
        this(0);
    }

    public BlockPlanks(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return PLANKS;
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
    public String getName() {
        return NAMES[this.getDamage() & 0x7];
    }

    @Override
    public int getToolType() {
        return BlockToolType.AXE;
    }

    @Override
    public BlockColor getColor() {
        switch (getDamage() & 0x07) {
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
}
