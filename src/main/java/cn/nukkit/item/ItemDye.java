package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.utils.DyeColor;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemDye extends Item {

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

    public DyeColor getDyeColor() {
        return DyeColor.getByDyeNewData(getDamage());
    }

    @Override
    public boolean isBoneMeal() {
        return getDamage() == BONE_MEAL;
    }
}
