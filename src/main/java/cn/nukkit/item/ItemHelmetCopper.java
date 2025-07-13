package cn.nukkit.item;

public class ItemHelmetCopper extends ItemArmor {
    public ItemHelmetCopper() {
        this(0, 1);
    }

    public ItemHelmetCopper(Integer meta) {
        this(meta, 1);
    }

    public ItemHelmetCopper(Integer meta, int count) {
        super(COPPER_HELMET, meta, count, "Copper Helmet");
    }

    @Override
    public boolean isHelmet() {
        return true;
    }

    @Override
    public int getTier() {
        return TIER_COPPER;
    }

    @Override
    public int getMaxDurability() {
        return 121;
    }

    @Override
    public int getArmorPoints() {
        return 2;
    }
}
