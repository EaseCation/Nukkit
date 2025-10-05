package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.utils.DyeColor;

import static cn.nukkit.SharedConstants.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemDye extends Item {

    public static final int WHITE = 0;
    public static final int ORANGE = 1;
    public static final int MAGENTA = 2;
    public static final int LIGHT_BLUE = 3;
    public static final int YELLOW = 4;
    public static final int LIME = 5;
    public static final int PINK = 6;
    public static final int GRAY = 7;
    public static final int LIGHT_GRAY = 8;
    public static final int CYAN = 9;
    public static final int PURPLE = 10;
    public static final int BLUE = 11;
    public static final int BROWN = 12;
    public static final int GREEN = 13;
    public static final int RED = 14;
    public static final int BLACK = 15;

    public static final int INK_SAC = 0;
    public static final int COCOA_BEANS = 3;
    public static final int LAPIS_LAZULI = 4;
    public static final int BONE_MEAL = 15;

    public static final int BLACK_NEW = 16;
    public static final int BROWN_NEW = 17;
    public static final int BLUE_NEW = 18;
    public static final int WHITE_NEW = 19;

    public static final int DYE_COUNT = 20;

    private static final String[] NAMES = new String[20];

    static {
        for (int i = 0; i < 16; i++) {
            NAMES[i] = DyeColor.getByDyeData(i).getDyeName();
        }
        NAMES[BLACK_NEW] = "Black Dye";
        NAMES[BROWN_NEW] = "Brown Dye";
        NAMES[BLUE_NEW] = "Blue Dye";
        NAMES[WHITE_NEW] = "White Dye";
    }

    public ItemDye() {
        this(0, 1);
    }

    public ItemDye(Integer meta) {
        this(meta, 1);
    }

    public ItemDye(DyeColor dyeColor) {
        this(dyeColor.getDyeData(), 1);
    }

    public ItemDye(DyeColor dyeColor, int amount) {
        this(dyeColor.getDyeData(), amount);
    }

    public ItemDye(Integer meta, int amount) {
        super(DYE, meta, amount, meta == null ? "Dye" : meta >= 0 && meta <= 19 ? NAMES[meta] : UNKNOWN_STR);

        if (this.getDamage() == DyeColor.BROWN.getDyeData()) {
            this.block = Block.get(BlockID.COCOA);
        }
    }

    @Override
    public boolean isStackedByData() {
        return !ITEM_FLATTEN;
    }

    @Override
    public float getFurnaceXpMultiplier() {
        return switch (getDamage()) {
            case LAPIS_LAZULI, GREEN -> 0.2f;
            case LIME -> 0.1f;
            default -> 0;
        };
    }

    public DyeColor getDyeColor() {
        return DyeColor.getByDyeNewData(getDamage());
    }

    @Override
    public boolean isFertilizer() {
        return getDamage() == BONE_MEAL;
    }
}
