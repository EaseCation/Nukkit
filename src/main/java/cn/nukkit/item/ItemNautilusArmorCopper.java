package cn.nukkit.item;

public class ItemNautilusArmorCopper extends Item {
    public ItemNautilusArmorCopper() {
        this(0, 0);
    }

    public ItemNautilusArmorCopper(Integer meta) {
        this(meta, 0);
    }

    public ItemNautilusArmorCopper(Integer meta, int count) {
        super(COPPER_NAUTILUS_ARMOR, meta, count, "Copper Nautilus Armor");
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public int getArmorPoints() {
        return 4;
    }

    @Override
    public boolean isNautilusArmor() {
        return true;
    }
}
