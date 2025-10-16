package cn.nukkit.item;

public class ItemNautilusArmorNetherite extends Item {
    public ItemNautilusArmorNetherite() {
        this(0, 0);
    }

    public ItemNautilusArmorNetherite(Integer meta) {
        this(meta, 0);
    }

    public ItemNautilusArmorNetherite(Integer meta, int count) {
        super(NETHERITE_NAUTILUS_ARMOR, meta, count, "Netherite Nautilus Armor");
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public int getArmorPoints() {
        return 19;
    }

    @Override
    public int getToughness() {
        return 3;
    }

    @Override
    public boolean isNautilusArmor() {
        return true;
    }
}
