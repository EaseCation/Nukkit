package cn.nukkit.item;

import cn.nukkit.item.armortrim.TrimPatternNames;

public class ItemSmithingTemplateArmorTrimEye extends Item {
    public ItemSmithingTemplateArmorTrimEye() {
        this(0, 1);
    }

    public ItemSmithingTemplateArmorTrimEye(Integer meta) {
        this(meta, 1);
    }

    public ItemSmithingTemplateArmorTrimEye(Integer meta, int count) {
        super(EYE_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Eye Armor Trim");
    }

    @Override
    public String getTrimPatternName() {
        return TrimPatternNames.EYE;
    }
}
