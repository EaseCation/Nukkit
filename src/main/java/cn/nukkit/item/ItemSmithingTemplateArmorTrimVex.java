package cn.nukkit.item;

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
}
