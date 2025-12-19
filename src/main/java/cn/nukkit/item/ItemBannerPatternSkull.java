package cn.nukkit.item;

import cn.nukkit.utils.BannerPattern.Type;

public class ItemBannerPatternSkull extends ItemBannerPattern {
    public ItemBannerPatternSkull() {
        this(0, 1);
    }

    public ItemBannerPatternSkull(Integer meta) {
        this(meta, 1);
    }

    public ItemBannerPatternSkull(Integer meta, int count) {
        super(SKULL_BANNER_PATTERN, meta, count, "Skull Charge Banner Pattern");
    }

    @Override
    public Type getPattern() {
        return Type.PATTERN_SKULL;
    }
}
