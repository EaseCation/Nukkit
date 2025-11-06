package cn.nukkit.item;

import cn.nukkit.item.armortrim.TrimPatternNames;

public class ItemSmithingTemplateArmorTrimCoast extends Item {
    public ItemSmithingTemplateArmorTrimCoast() {
        this(0, 1);
    }

    public ItemSmithingTemplateArmorTrimCoast(Integer meta) {
        this(meta, 1);
    }

    public ItemSmithingTemplateArmorTrimCoast(Integer meta, int count) {
        super(COAST_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Coast Armor Trim");
    }

    @Override
    public String getTrimPatternName() {
        return TrimPatternNames.COAST;
    }
}
