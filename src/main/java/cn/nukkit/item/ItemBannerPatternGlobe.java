package cn.nukkit.item;

import cn.nukkit.utils.BannerPattern.Type;

public class ItemBannerPatternGlobe extends ItemBannerPattern {
    public ItemBannerPatternGlobe() {
        this(0, 1);
    }

    public ItemBannerPatternGlobe(Integer meta) {
        this(meta, 1);
    }

    public ItemBannerPatternGlobe(Integer meta, int count) {
        super(GLOBE_BANNER_PATTERN, meta, count, "Globe Banner Pattern");
    }

    @Override
    public Type getPattern() {
        return Type.PATTERN_GLOBE;
    }
}
