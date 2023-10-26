package cn.nukkit.item;

public class ItemSmithingTemplateArmorTrimRaiser extends Item {
    public ItemSmithingTemplateArmorTrimRaiser() {
        this(0, 1);
    }

    public ItemSmithingTemplateArmorTrimRaiser(Integer meta) {
        this(meta, 1);
    }

    public ItemSmithingTemplateArmorTrimRaiser(Integer meta, int count) {
        super(RAISER_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Smithing Template");
    }
}
