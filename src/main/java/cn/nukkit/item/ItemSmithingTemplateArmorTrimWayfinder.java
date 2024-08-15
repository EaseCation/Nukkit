package cn.nukkit.item;

public class ItemSmithingTemplateArmorTrimWayfinder extends Item {
    public ItemSmithingTemplateArmorTrimWayfinder() {
        this(0, 1);
    }

    public ItemSmithingTemplateArmorTrimWayfinder(Integer meta) {
        this(meta, 1);
    }

    public ItemSmithingTemplateArmorTrimWayfinder(Integer meta, int count) {
        super(WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Wayfinder Armor Trim");
    }
}
