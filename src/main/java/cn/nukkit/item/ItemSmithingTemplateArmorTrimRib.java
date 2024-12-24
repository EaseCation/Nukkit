package cn.nukkit.item;

public class ItemSmithingTemplateArmorTrimRib extends Item {
    public ItemSmithingTemplateArmorTrimRib() {
        this(0, 1);
    }

    public ItemSmithingTemplateArmorTrimRib(Integer meta) {
        this(meta, 1);
    }

    public ItemSmithingTemplateArmorTrimRib(Integer meta, int count) {
        super(RIB_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Rib Armor Trim");
    }

    @Override
    public boolean isArmorTrimSmithingTemplate() {
        return true;
    }
}
