package cn.nukkit.item;

import cn.nukkit.item.armortrim.TrimPatternNames;

public class ItemSmithingTemplateArmorTrimRaiser extends Item {
    public ItemSmithingTemplateArmorTrimRaiser() {
        this(0, 1);
    }

    public ItemSmithingTemplateArmorTrimRaiser(Integer meta) {
        this(meta, 1);
    }

    public ItemSmithingTemplateArmorTrimRaiser(Integer meta, int count) {
        super(RAISER_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Raiser Armor Trim");
    }

    @Override
    public String getTrimPatternName() {
        return TrimPatternNames.RAISER;
    }
}
