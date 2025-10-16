package cn.nukkit.item;

public class ItemHorseArmorGold extends Item {
    public ItemHorseArmorGold() {
        this(0, 0);
    }

    public ItemHorseArmorGold(Integer meta) {
        this(meta, 0);
    }

    public ItemHorseArmorGold(Integer meta, int count) {
        super(GOLDEN_HORSE_ARMOR, meta, count, "Golden Horse Armor");
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
        return 7;
    }
}
