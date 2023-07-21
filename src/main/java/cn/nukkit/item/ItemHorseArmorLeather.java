package cn.nukkit.item;

public class ItemHorseArmorLeather extends Item {
    public ItemHorseArmorLeather() {
        this(0, 1);
    }

    public ItemHorseArmorLeather(Integer meta) {
        this(meta, 1);
    }

    public ItemHorseArmorLeather(Integer meta, int count) {
        super(LEATHER_HORSE_ARMOR, meta, 1, "Leather Horse Armor");
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
