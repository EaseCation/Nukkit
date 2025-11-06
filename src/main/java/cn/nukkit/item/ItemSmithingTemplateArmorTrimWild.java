package cn.nukkit.item;

import cn.nukkit.item.armortrim.TrimPatternNames;

public class ItemSmithingTemplateArmorTrimWild extends Item {
    public ItemSmithingTemplateArmorTrimWild() {
        this(0, 1);
    }

    public ItemSmithingTemplateArmorTrimWild(Integer meta) {
        this(meta, 1);
    }

    public ItemSmithingTemplateArmorTrimWild(Integer meta, int count) {
        super(WILD_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Wild Armor Trim");
    }

    @Override
    public String getTrimPatternName() {
        return TrimPatternNames.WILD;
    }
}
