package cn.nukkit.item;

import cn.nukkit.utils.BannerPattern.Type;

public class ItemBannerPatternGuster extends ItemBannerPattern {
    public ItemBannerPatternGuster() {
        this(0, 1);
    }

    public ItemBannerPatternGuster(Integer meta) {
        this(meta, 1);
    }

    public ItemBannerPatternGuster(Integer meta, int count) {
        super(GUSTER_BANNER_PATTERN, meta, count, "Guster Banner Pattern");
    }

    @Override
    public Type getPattern() {
        return Type.PATTERN_GUSTER;
    }
}
