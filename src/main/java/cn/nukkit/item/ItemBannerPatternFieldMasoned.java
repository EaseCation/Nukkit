package cn.nukkit.item;

import cn.nukkit.utils.BannerPattern.Type;

public class ItemBannerPatternFieldMasoned extends ItemBannerPattern {
    public ItemBannerPatternFieldMasoned() {
        this(0, 1);
    }

    public ItemBannerPatternFieldMasoned(Integer meta) {
        this(meta, 1);
    }

    public ItemBannerPatternFieldMasoned(Integer meta, int count) {
        super(FIELD_MASONED_BANNER_PATTERN, meta, count, "Field Masoned Banner Pattern");
    }

    @Override
    public Type getPattern() {
        return Type.PATTERN_BRICK;
    }
}
