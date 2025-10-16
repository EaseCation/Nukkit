package cn.nukkit.item;

public class ItemNautilusArmorIron extends Item {
    public ItemNautilusArmorIron() {
        this(0, 0);
    }

    public ItemNautilusArmorIron(Integer meta) {
        this(meta, 0);
    }

    public ItemNautilusArmorIron(Integer meta, int count) {
        super(IRON_NAUTILUS_ARMOR, meta, count, "Iron Nautilus Armor");
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public int getArmorPoints() {
        return 5;
    }

    @Override
    public boolean isNautilusArmor() {
        return true;
    }
}
