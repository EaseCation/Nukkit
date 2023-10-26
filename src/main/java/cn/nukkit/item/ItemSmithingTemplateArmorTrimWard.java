package cn.nukkit.item;

public class ItemSmithingTemplateArmorTrimWard extends Item {
    public ItemSmithingTemplateArmorTrimWard() {
        this(0, 1);
    }

    public ItemSmithingTemplateArmorTrimWard(Integer meta) {
        this(meta, 1);
    }

    public ItemSmithingTemplateArmorTrimWard(Integer meta, int count) {
        super(WARD_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Smithing Template");
    }
}
