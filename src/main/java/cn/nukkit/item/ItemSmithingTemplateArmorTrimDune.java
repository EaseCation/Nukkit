package cn.nukkit.item;

import cn.nukkit.item.armortrim.TrimPatternNames;

public class ItemSmithingTemplateArmorTrimDune extends Item {
    public ItemSmithingTemplateArmorTrimDune() {
        this(0, 1);
    }

    public ItemSmithingTemplateArmorTrimDune(Integer meta) {
        this(meta, 1);
    }

    public ItemSmithingTemplateArmorTrimDune(Integer meta, int count) {
        super(DUNE_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Dune Armor Trim");
    }

    @Override
    public String getTrimPatternName() {
        return TrimPatternNames.DUNE;
    }
}
