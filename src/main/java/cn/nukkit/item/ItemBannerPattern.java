package cn.nukkit.item;

public class ItemBannerPattern extends Item {
    public static final int CREEPER_BANNER_PATTERN = 0;
    public static final int SKULL_BANNER_PATTERN = 1;
    public static final int FLOWER_BANNER_PATTERN = 2;
    public static final int MOJANG_BANNER_PATTERN = 3;
    public static final int FIELD_MASONED_BANNER_PATTERN = 4;
    public static final int BORDURE_INDENTED_BANNER_PATTERN = 5;
    public static final int PIGLIN_BANNER_PATTERN = 6;
    public static final int GLOBE_BANNER_PATTERN = 7;
    public static final int FLOW_BANNER_PATTERN = 8;
    public static final int GUSTER_BANNER_PATTERN = 9;
    public static final int UNDEFINED_BANNER_PATTERN = 10;

    public ItemBannerPattern() {
        this(0, 1);
    }

    public ItemBannerPattern(Integer meta) {
        this(meta, 1);
    }

    public ItemBannerPattern(Integer meta, int count) {
        super(BANNER_PATTERN, meta, 1, "Banner Pattern");
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
