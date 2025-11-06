package cn.nukkit.item;

import cn.nukkit.item.armortrim.TrimPatternNames;

public class ItemSmithingTemplateArmorTrimSnout extends Item {
    public ItemSmithingTemplateArmorTrimSnout() {
        this(0, 1);
    }

    public ItemSmithingTemplateArmorTrimSnout(Integer meta) {
        this(meta, 1);
    }

    public ItemSmithingTemplateArmorTrimSnout(Integer meta, int count) {
        super(SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Snout Armor Trim");
    }

    @Override
    public String getTrimPatternName() {
        return TrimPatternNames.SNOUT;
    }
}
