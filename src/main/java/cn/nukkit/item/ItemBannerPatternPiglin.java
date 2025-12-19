package cn.nukkit.item;

import cn.nukkit.utils.BannerPattern.Type;

public class ItemBannerPatternPiglin extends ItemBannerPattern {
    public ItemBannerPatternPiglin() {
        this(0, 1);
    }

    public ItemBannerPatternPiglin(Integer meta) {
        this(meta, 1);
    }

    public ItemBannerPatternPiglin(Integer meta, int count) {
        super(PIGLIN_BANNER_PATTERN, meta, count, "Snout Banner Pattern");
    }

    @Override
    public Type getPattern() {
        return Type.PATTERN_PIGLIN;
    }
}
