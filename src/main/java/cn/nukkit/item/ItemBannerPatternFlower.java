package cn.nukkit.item;

import cn.nukkit.utils.BannerPattern.Type;

public class ItemBannerPatternFlower extends ItemBannerPattern {
    public ItemBannerPatternFlower() {
        this(0, 1);
    }

    public ItemBannerPatternFlower(Integer meta) {
        this(meta, 1);
    }

    public ItemBannerPatternFlower(Integer meta, int count) {
        super(FLOWER_BANNER_PATTERN, meta, count, "Flower Charge Banner Pattern");
    }

    @Override
    public Type getPattern() {
        return Type.PATTERN_FLOWER;
    }
}
