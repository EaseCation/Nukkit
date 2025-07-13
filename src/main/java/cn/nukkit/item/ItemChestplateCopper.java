package cn.nukkit.item;

public class ItemChestplateCopper extends ItemArmor {
    public ItemChestplateCopper() {
        this(0, 1);
    }

    public ItemChestplateCopper(Integer meta) {
        this(meta, 1);
    }

    public ItemChestplateCopper(Integer meta, int count) {
        super(COPPER_CHESTPLATE, meta, count, "Copper Chestplate");
    }

    @Override
    public boolean isChestplate() {
        return true;
    }

    @Override
    public int getTier() {
        return TIER_COPPER;
    }

    @Override
    public int getMaxDurability() {
        return 176;
    }

    @Override
    public int getArmorPoints() {
        return 4;
    }
}
