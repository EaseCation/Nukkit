package cn.nukkit.item;

import cn.nukkit.network.protocol.LevelSoundEventPacket;

public class ItemSpearGolden extends ItemSpear {
    public ItemSpearGolden() {
        this(0, 1);
    }

    public ItemSpearGolden(Integer meta) {
        this(meta, 1);
    }

    public ItemSpearGolden(Integer meta, int count) {
        super(GOLDEN_SPEAR, meta, count, "Golden Spear");
    }

    @Override
    public int getTier() {
        return TIER_GOLD;
    }

    @Override
    public int getMaxDurability() {
        return DURABILITY_GOLD;
    }

    @Override
    public int getAttackDamage() {
        return 1;
    }

    @Override
    public int getSwingDuration() {
        return 19;
    }

    @Override
    public int getAttackMissSound() {
        return LevelSoundEventPacket.SOUND_GOLDEN_SPEAR_ATTACK_MISS;
    }

    @Override
    public int getAttackHitSound() {
        return LevelSoundEventPacket.SOUND_GOLDEN_SPEAR_ATTACK_HIT;
    }

    @Override
    public int getAttackDelay() {
        return 14;
    }

    @Override
    public float getDamageMultiplier() {
        return 0.7f;
    }

    @Override
    public int getDamageDuration() {
        return 275;
    }

    @Override
    public int getKnockbackDuration() {
        return 110;
    }

    @Override
    public int getDismountDuration() {
        return 70;
    }

    @Override
    public int getDismountSpeed() {
        return 13;
    }
}
