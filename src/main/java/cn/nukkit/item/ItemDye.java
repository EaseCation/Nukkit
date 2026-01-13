package cn.nukkit.item;

import cn.nukkit.utils.DyeColor;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class ItemDye extends Item {

    static final int BLACK = 0;
    static final int RED = 1;
    static final int GREEN = 2;
    static final int BROWN = 3;
    static final int BLUE = 4;
    static final int PURPLE = 5;
    static final int CYAN = 6;
    static final int LIGHT_GRAY = 7;
    static final int GRAY = 8;
    static final int PINK = 9;
    static final int LIME = 10;
    static final int YELLOW = 11;
    static final int LIGHT_BLUE = 12;
    static final int MAGENTA = 13;
    static final int ORANGE = 14;
    static final int WHITE = 15;
    static final int BLACK_NEW = 16;
    static final int BROWN_NEW = 17;
    static final int BLUE_NEW = 18;
    static final int WHITE_NEW = 19;

    public static final int[] DYES = {
            INK_SAC,
            RED_DYE,
            GREEN_DYE,
            COCOA_BEANS,
            LAPIS_LAZULI,
            PURPLE_DYE,
            CYAN_DYE,
            LIGHT_GRAY_DYE,
            GRAY_DYE,
            PINK_DYE,
            LIME_DYE,
            YELLOW_DYE,
            LIGHT_BLUE_DYE,
            MAGENTA_DYE,
            ORANGE_DYE,
            BONE_MEAL,
            BLACK_DYE,
            BROWN_DYE,
            BLUE_DYE,
            WHITE_DYE,
    };

    protected ItemDye(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    @Override
    public String getDescriptionId() {
        return "item.dye." + getDyeColor().getDyeDescriptionName() + ".name";
    }

    @Override
    public boolean isDye() {
        return true;
    }

    public abstract DyeColor getDyeColor();
}
