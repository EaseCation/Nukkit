package cn.nukkit.item;

import cn.nukkit.item.armortrim.TrimPatternNames;

public class ItemSmithingTemplateArmorTrimRib extends Item {
    public ItemSmithingTemplateArmorTrimRib() {
        this(0, 1);
    }

    public ItemSmithingTemplateArmorTrimRib(Integer meta) {
        this(meta, 1);
    }

    public ItemSmithingTemplateArmorTrimRib(Integer meta, int count) {
        super(RIB_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Rib Armor Trim");
    }

    @Override
    public String getTrimPatternName() {
        return TrimPatternNames.RIB;
    }
}
