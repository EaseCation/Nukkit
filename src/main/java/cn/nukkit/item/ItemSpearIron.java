package cn.nukkit.item;

public class ItemSpearIron extends ItemSpear {
    public ItemSpearIron() {
        this(0, 1);
    }

    public ItemSpearIron(Integer meta) {
        this(meta, 1);
    }

    public ItemSpearIron(Integer meta, int count) {
        super(IRON_SPEAR, meta, count, "Iron Spear");
    }

    @Override
    public int getTier() {
        return TIER_IRON;
    }

    @Override
    public int getMaxDurability() {
        return DURABILITY_IRON;
    }

    @Override
    public int getAttackDamage() {
        return 3;
    }

    @Override
    public int getSwingDuration() {
        return 19;
    }

    @Override
    public int getAttackDelay() {
        return 12;
    }

    @Override
    public float getDamageMultiplier() {
        return 0.95f;
    }

    @Override
    public int getDamageDuration() {
        return 225;
    }

    @Override
    public int getKnockbackDuration() {
        return 90;
    }

    @Override
    public int getDismountDuration() {
        return 50;
    }

    @Override
    public int getDismountSpeed() {
        return 11;
    }
}
