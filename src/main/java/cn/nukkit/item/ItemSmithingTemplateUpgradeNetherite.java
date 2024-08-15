package cn.nukkit.item;

public class ItemSmithingTemplateUpgradeNetherite extends Item {
    public ItemSmithingTemplateUpgradeNetherite() {
        this(0, 1);
    }

    public ItemSmithingTemplateUpgradeNetherite(Integer meta) {
        this(meta, 1);
    }

    public ItemSmithingTemplateUpgradeNetherite(Integer meta, int count) {
        super(NETHERITE_UPGRADE_SMITHING_TEMPLATE, meta, count, "Netherite Upgrade");
    }
}
