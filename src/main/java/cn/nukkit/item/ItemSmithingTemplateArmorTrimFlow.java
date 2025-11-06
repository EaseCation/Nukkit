package cn.nukkit.item;

import cn.nukkit.item.armortrim.TrimPatternNames;

public class ItemSmithingTemplateArmorTrimFlow extends Item {
    public ItemSmithingTemplateArmorTrimFlow() {
        this(0, 1);
    }

    public ItemSmithingTemplateArmorTrimFlow(Integer meta) {
        this(meta, 1);
    }

    public ItemSmithingTemplateArmorTrimFlow(Integer meta, int count) {
        super(FLOW_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Flow Armor Trim");
    }

    @Override
    public String getTrimPatternName() {
        return TrimPatternNames.FLOW;
    }
}
