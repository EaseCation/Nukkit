package cn.nukkit.item;

public class ItemHorseArmorIron extends Item {
    public ItemHorseArmorIron() {
        this(0, 0);
    }

    public ItemHorseArmorIron(Integer meta) {
        this(meta, 0);
    }

    public ItemHorseArmorIron(Integer meta, int count) {
        super(IRON_HORSE_ARMOR, meta, 1, "Iron Horse Armor");
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean isHorseArmor() {
        return true;
    }

    @Override
    public int getArmorPoints() {
        return 5;
    }
}
