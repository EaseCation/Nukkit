package cn.nukkit.item;

public class ItemSmithingTemplateArmorTrimTide extends Item {
    public ItemSmithingTemplateArmorTrimTide() {
        this(0, 1);
    }

    public ItemSmithingTemplateArmorTrimTide(Integer meta) {
        this(meta, 1);
    }

    public ItemSmithingTemplateArmorTrimTide(Integer meta, int count) {
        super(TIDE_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Tide Armor Trim");
    }

    @Override
    public boolean isArmorTrimSmithingTemplate() {
        return true;
    }
}
