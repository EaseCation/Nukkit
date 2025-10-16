package cn.nukkit.item;

public class ItemSpearNetherite extends ItemSpear {
    public ItemSpearNetherite() {
        this(0, 1);
    }

    public ItemSpearNetherite(Integer meta) {
        this(meta, 1);
    }

    public ItemSpearNetherite(Integer meta, int count) {
        super(NETHERITE_SPEAR, meta, count, "Netherite Spear");
    }

    @Override
    public int getTier() {
        return TIER_NETHERITE;
    }

    @Override
    public int getMaxDurability() {
        return DURABILITY_NETHERITE;
    }

    @Override
    public int getAttackDamage() {
        return 5;
    }

    @Override
    public int getSwingDuration() {
        return 20;
    }

    @Override
    public int getAttackDelay() {
        return 8;
    }

    @Override
    public float getDamageMultiplier() {
        return 1.2f;
    }

    @Override
    public int getDamageDuration() {
        return 175;
    }

    @Override
    public int getKnockbackDuration() {
        return 70;
    }

    @Override
    public int getDismountDuration() {
        return 50;
    }

    @Override
    public int getDismountSpeed() {
        return 9;
    }

    @Override
    public boolean isFireResistant() {
        return true;
    }
}
