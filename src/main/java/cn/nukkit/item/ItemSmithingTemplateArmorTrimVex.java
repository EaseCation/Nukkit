package cn.nukkit.item;

import cn.nukkit.item.armortrim.TrimPatternNames;

public class ItemSmithingTemplateArmorTrimVex extends Item {
    public ItemSmithingTemplateArmorTrimVex() {
        this(0, 1);
    }

    public ItemSmithingTemplateArmorTrimVex(Integer meta) {
        this(meta, 1);
    }

    public ItemSmithingTemplateArmorTrimVex(Integer meta, int count) {
        super(VEX_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Vex Armor Trim");
    }

    @Override
    public String getTrimPatternName() {
        return TrimPatternNames.VEX;
    }
}
