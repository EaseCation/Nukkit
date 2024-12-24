package cn.nukkit.item;

public class ItemSmithingTemplateArmorTrimSilence extends Item {
    public ItemSmithingTemplateArmorTrimSilence() {
        this(0, 1);
    }

    public ItemSmithingTemplateArmorTrimSilence(Integer meta) {
        this(meta, 1);
    }

    public ItemSmithingTemplateArmorTrimSilence(Integer meta, int count) {
        super(SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Silence Armor Trim");
    }

    @Override
    public boolean isArmorTrimSmithingTemplate() {
        return true;
    }
}
