package cn.nukkit.item;

public class ItemBootsCopper extends ItemArmor {
    public ItemBootsCopper() {
        this(0, 1);
    }

    public ItemBootsCopper(Integer meta) {
        this(meta, 1);
    }

    public ItemBootsCopper(Integer meta, int count) {
        super(COPPER_BOOTS, meta, count, "Copper Boots");
    }

    @Override
    public boolean isBoots() {
        return true;
    }

    @Override
    public int getTier() {
        return TIER_COPPER;
    }

    @Override
    public int getMaxDurability() {
        return 143;
    }

    @Override
    public int getArmorPoints() {
        return 1;
    }
}
