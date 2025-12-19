package cn.nukkit.item;

import cn.nukkit.utils.BannerPattern.Type;

public class ItemBannerPatternCreeper extends ItemBannerPattern {
    public ItemBannerPatternCreeper() {
        this(0, 1);
    }

    public ItemBannerPatternCreeper(Integer meta) {
        this(meta, 1);
    }

    public ItemBannerPatternCreeper(Integer meta, int count) {
        super(CREEPER_BANNER_PATTERN, meta, count, "Creeper Charge Banner Pattern");
    }

    @Override
    public Type getPattern() {
        return Type.PATTERN_CREEPER;
    }
}
