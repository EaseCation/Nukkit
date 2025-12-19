package cn.nukkit.item;

import cn.nukkit.utils.BannerPattern.Type;

public class ItemBannerPatternBordureIndented extends ItemBannerPattern {
    public ItemBannerPatternBordureIndented() {
        this(0, 1);
    }

    public ItemBannerPatternBordureIndented(Integer meta) {
        this(meta, 1);
    }

    public ItemBannerPatternBordureIndented(Integer meta, int count) {
        super(BORDURE_INDENTED_BANNER_PATTERN, meta, count, "Bordure Indented Banner Pattern");
    }

    @Override
    public Type getPattern() {
        return Type.PATTERN_CURLY_BORDER;
    }
}
