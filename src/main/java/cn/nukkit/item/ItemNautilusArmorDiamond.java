package cn.nukkit.item;

public class ItemNautilusArmorDiamond extends Item {
    public ItemNautilusArmorDiamond() {
        this(0, 0);
    }

    public ItemNautilusArmorDiamond(Integer meta) {
        this(meta, 0);
    }

    public ItemNautilusArmorDiamond(Integer meta, int count) {
        super(DIAMOND_NAUTILUS_ARMOR, meta, count, "Diamond Nautilus Armor");
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public int getArmorPoints() {
        return 11;
    }

    @Override
    public int getToughness() {
        return 2;
    }

    @Override
    public boolean isNautilusArmor() {
        return true;
    }
}
