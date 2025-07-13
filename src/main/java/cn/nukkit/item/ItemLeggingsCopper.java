package cn.nukkit.item;

public class ItemLeggingsCopper extends ItemArmor {
    public ItemLeggingsCopper() {
        this(0, 1);
    }

    public ItemLeggingsCopper(Integer meta) {
        this(meta, 1);
    }

    public ItemLeggingsCopper(Integer meta, int count) {
        super(COPPER_LEGGINGS, meta, count, "Copper Leggings");
    }

    @Override
    public boolean isLeggings() {
        return true;
    }

    @Override
    public int getTier() {
        return TIER_COPPER;
    }

    @Override
    public int getMaxDurability() {
        return 165;
    }

    @Override
    public int getArmorPoints() {
        return 3;
    }
}
