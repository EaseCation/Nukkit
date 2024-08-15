package cn.nukkit.item;

public class ItemSmithingTemplateArmorTrimSpire extends Item {
    public ItemSmithingTemplateArmorTrimSpire() {
        this(0, 1);
    }

    public ItemSmithingTemplateArmorTrimSpire(Integer meta) {
        this(meta, 1);
    }

    public ItemSmithingTemplateArmorTrimSpire(Integer meta, int count) {
        super(SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Spire Armor Trim");
    }
}
