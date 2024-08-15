package cn.nukkit.item;

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
}
