package cn.nukkit.item;

public class ItemHorseArmorCopper extends Item {
    public ItemHorseArmorCopper() {
        this(0, 0);
    }

    public ItemHorseArmorCopper(Integer meta) {
        this(meta, 0);
    }

    public ItemHorseArmorCopper(Integer meta, int count) {
        super(COPPER_HORSE_ARMOR, meta, count, "Copper Horse Armor");
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
    public boolean isHorseArmor() {
        return true;
    }
}
