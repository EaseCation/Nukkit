package cn.nukkit.item;

import cn.nukkit.utils.BannerPattern.Type;

public abstract class ItemBannerPattern extends Item {
    static final int CREEPER_CHARGE = 0;
    static final int SKULL_CHARGE = 1;
    static final int FLOWER_CHARGE = 2;
    static final int MOJANG = 3;
    static final int FIELD_MASONED = 4;
    static final int BORDURE_INDENTED = 5;
    static final int PIGLIN = 6;
    static final int GLOBE = 7;

    public static final int[] BANNER_PATTERNS = {
            CREEPER_BANNER_PATTERN,
            SKULL_BANNER_PATTERN,
            FLOWER_BANNER_PATTERN,
            MOJANG_BANNER_PATTERN,
            FIELD_MASONED_BANNER_PATTERN,
            BORDURE_INDENTED_BANNER_PATTERN,
            PIGLIN_BANNER_PATTERN,
            GLOBE_BANNER_PATTERN,
    };

    protected ItemBannerPattern(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean isBannerPattern() {
        return true;
    }

    public abstract Type getPattern();
}
