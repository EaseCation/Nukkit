package cn.nukkit.block;

/**
 * Created on 2015/12/2 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public abstract class BlockSlabWood extends BlockSlab {
    public static final int[] WOODEN_SLABS = {
            OAK_SLAB,
            SPRUCE_SLAB,
            BIRCH_SLAB,
            JUNGLE_SLAB,
            ACACIA_SLAB,
            DARK_OAK_SLAB,
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
    protected abstract int getDoubleSlabBlockId();
}
