package cn.nukkit.item;

public class ItemSmithingTemplateArmorTrimHost extends Item {
    public ItemSmithingTemplateArmorTrimHost() {
        this(0, 1);
    }

    public ItemSmithingTemplateArmorTrimHost(Integer meta) {
        this(meta, 1);
    }

    public ItemSmithingTemplateArmorTrimHost(Integer meta, int count) {
        super(HOST_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Host Armor Trim");
    }
}
