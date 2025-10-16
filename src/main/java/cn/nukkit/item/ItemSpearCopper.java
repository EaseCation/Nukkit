package cn.nukkit.item;

public class ItemSpearCopper extends ItemSpear {
    public ItemSpearCopper() {
        this(0, 1);
    }

    public ItemSpearCopper(Integer meta) {
        this(meta, 1);
    }

    public ItemSpearCopper(Integer meta, int count) {
        super(COPPER_SPEAR, meta, count, "Copper Spear");
    }

    @Override
    public int getTier() {
        return TIER_COPPER;
    }

    @Override
    public int getMaxDurability() {
        return DURABILITY_COPPER;
    }

    @Override
    public int getAttackDamage() {
        return 2;
    }

    @Override
    public int getSwingDuration() {
        return 15;
    }

    @Override
    public int getAttackDelay() {
        return 13;
    }

    @Override
    public float getDamageMultiplier() {
        return 0.82f;
    }

    @Override
    public int getDamageDuration() {
        return 250;
    }

    @Override
    public int getKnockbackDuration() {
        return 100;
    }

    @Override
    public int getDismountDuration() {
        return 80;
    }

    @Override
    public int getDismountSpeed() {
        return 12;
    }
}
