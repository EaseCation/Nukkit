package cn.nukkit.item;

import cn.nukkit.item.armortrim.TrimPatternNames;

public class ItemSmithingTemplateArmorTrimSentry extends Item {
    public ItemSmithingTemplateArmorTrimSentry() {
        this(0, 1);
    }

    public ItemSmithingTemplateArmorTrimSentry(Integer meta) {
        this(meta, 1);
    }

    public ItemSmithingTemplateArmorTrimSentry(Integer meta, int count) {
        super(SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Sentry Armor Trim");
    }

    @Override
    public String getTrimPatternName() {
        return TrimPatternNames.SENTRY;
    }
}
