package cn.nukkit.item;

import cn.nukkit.utils.BannerPattern.Type;

public class ItemBannerPatternMojang extends ItemBannerPattern {
    public ItemBannerPatternMojang() {
        this(0, 1);
    }

    public ItemBannerPatternMojang(Integer meta) {
        this(meta, 1);
    }

    public ItemBannerPatternMojang(Integer meta, int count) {
        super(MOJANG_BANNER_PATTERN, meta, count, "Thing Banner Pattern");
    }

    @Override
    public Type getPattern() {
        return Type.PATTERN_MOJANG;
    }
}
