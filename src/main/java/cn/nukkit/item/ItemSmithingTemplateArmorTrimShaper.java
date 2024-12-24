package cn.nukkit.item;

public class ItemSmithingTemplateArmorTrimShaper extends Item {
    public ItemSmithingTemplateArmorTrimShaper() {
        this(0, 1);
    }

    public ItemSmithingTemplateArmorTrimShaper(Integer meta) {
        this(meta, 1);
    }

    public ItemSmithingTemplateArmorTrimShaper(Integer meta, int count) {
        super(SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Shaper Armor Trim");
    }

    @Override
    public boolean isArmorTrimSmithingTemplate() {
        return true;
    }
}
