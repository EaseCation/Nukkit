package cn.nukkit.item;

import cn.nukkit.utils.BannerPattern.Type;

public class ItemBannerPatternFlow extends ItemBannerPattern {
    public ItemBannerPatternFlow() {
        this(0, 1);
    }

    public ItemBannerPatternFlow(Integer meta) {
        this(meta, 1);
    }

    public ItemBannerPatternFlow(Integer meta, int count) {
        super(FLOW_BANNER_PATTERN, meta, count, "Flow Banner Pattern");
    }

    @Override
    public Type getPattern() {
        return Type.PATTERN_FLOW;
    }
}
