package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

/**
 * Created on 2015/12/2 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public abstract class BlockWool extends BlockSolid {
    public static final int[] WOOLS = {
            WHITE_WOOL,
            ORANGE_WOOL,
            MAGENTA_WOOL,
            LIGHT_BLUE_WOOL,
            YELLOW_WOOL,
            LIME_WOOL,
            PINK_WOOL,
            GRAY_WOOL,
            LIGHT_GRAY_WOOL,
            CYAN_WOOL,
            PURPLE_WOOL,
            BLUE_WOOL,
            BROWN_WOOL,
            GREEN_WOOL,
            RED_WOOL,
            BLACK_WOOL,
    };

    public BlockWool() {
    }

    @Override
    public int getToolType() {
        return BlockToolType.SHEARS;
    }

    @Override
    public float getHardness() {
        return 0.8f;
    }

    @Override
    public float getResistance() {
        return 4;
    }

    @Override
    public int getBurnChance() {
        return 30;
    }

    @Override
    public int getBurnAbility() {
        return 60;
    }

    public abstract DyeColor getDyeColor();

    @Override
    public boolean isWool() {
        return true;
    }
}
