package cn.nukkit.item;

public class ItemNautilusArmorGolden extends Item {
    public ItemNautilusArmorGolden() {
        this(0, 0);
    }

    public ItemNautilusArmorGolden(Integer meta) {
        this(meta, 0);
    }

    public ItemNautilusArmorGolden(Integer meta, int count) {
        super(GOLDEN_NAUTILUS_ARMOR, meta, count, "Golden Nautilus Armor");
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public int getArmorPoints() {
        return 7;
    }

    @Override
    public boolean isNautilusArmor() {
        return true;
    }
}
