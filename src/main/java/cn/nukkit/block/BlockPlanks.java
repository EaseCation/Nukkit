package cn.nukkit.block;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class BlockPlanks extends BlockSolid {
    public static final int[] WOODEN_PLANKS = {
            OAK_PLANKS,
            SPRUCE_PLANKS,
            BIRCH_PLANKS,
            JUNGLE_PLANKS,
            ACACIA_PLANKS,
            DARK_OAK_PLANKS,
    };

    public static final int OAK = 0;
    public static final int SPRUCE = 1;
    public static final int BIRCH = 2;
    public static final int JUNGLE = 3;
    public static final int ACACIA = 4;
    public static final int DARK_OAK = 5;

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
    public int getFuelTime() {
        return 300;
    }

    @Override
    public boolean isPlanks() {
        return true;
    }
}
