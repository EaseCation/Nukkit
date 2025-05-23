package cn.nukkit.item;

public class ItemSmithingTemplateArmorTrimBolt extends Item {
    public ItemSmithingTemplateArmorTrimBolt() {
        this(0, 1);
    }

    public ItemSmithingTemplateArmorTrimBolt(Integer meta) {
        this(meta, 1);
    }

    public ItemSmithingTemplateArmorTrimBolt(Integer meta, int count) {
        super(BOLT_ARMOR_TRIM_SMITHING_TEMPLATE, meta, count, "Bolt Armor Trim");
    }

    @Override
    public boolean isArmorTrimSmithingTemplate() {
        return true;
    }
}
