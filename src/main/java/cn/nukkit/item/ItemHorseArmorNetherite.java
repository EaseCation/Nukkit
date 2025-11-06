package cn.nukkit.item;

public class ItemHorseArmorNetherite extends Item {
    public ItemHorseArmorNetherite() {
        this(0, 0);
    }

    public ItemHorseArmorNetherite(Integer meta) {
        this(meta, 0);
    }

    public ItemHorseArmorNetherite(Integer meta, int count) {
        super(NETHERITE_HORSE_ARMOR, meta, count, "Netherite Horse Armor");
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
    public float getKnockbackResistance() {
        return 0.1f;
    }

    @Override
    public boolean isFireResistant() {
        return true;
    }

    @Override
    public boolean isHorseArmor() {
        return true;
    }
}
