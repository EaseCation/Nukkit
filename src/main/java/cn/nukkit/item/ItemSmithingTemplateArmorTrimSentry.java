package cn.nukkit.item;

public class ItemSmithingTemplateArmorTrimSentry extends Item {
    public ItemSmithingTemplateArmorTrimSentry() {
        this(0, 1);
    }

    public ItemSmithingTemplateArmorTrimSentry(Integer meta) {
        this(meta, 1);
    }

    public ItemSmithingTemplateArmorTrimSentry(Integer meta, int count) {
        super(SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Smithing Template");
    }
}
