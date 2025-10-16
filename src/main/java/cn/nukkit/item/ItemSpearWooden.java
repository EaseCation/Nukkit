package cn.nukkit.item;

import cn.nukkit.network.protocol.LevelSoundEventPacket;

public class ItemSpearWooden extends ItemSpear {
    public ItemSpearWooden() {
        this(0, 1);
    }

    public ItemSpearWooden(Integer meta) {
        this(meta, 1);
    }

    public ItemSpearWooden(Integer meta, int count) {
        super(WOODEN_SPEAR, meta, count, "Wooden Spear");
    }

    @Override
    public int getTier() {
        return TIER_WOODEN;
    }

    @Override
    public int getMaxDurability() {
        return DURABILITY_WOODEN;
    }

    @Override
    public int getAttackDamage() {
        return 1;
    }

    @Override
    public int getSwingDuration() {
        return 13;
    }

    @Override
    public int getAttackMissSound() {
        return LevelSoundEventPacket.SOUND_WOODEN_SPEAR_ATTACK_MISS;
    }

    @Override
    public int getAttackHitSound() {
        return LevelSoundEventPacket.SOUND_WOODEN_SPEAR_ATTACK_HIT;
    }

    @Override
    public int getAttackDelay() {
        return 15;
    }

    @Override
    public float getDamageMultiplier() {
        return 0.7f;
    }

    @Override
    public int getDamageDuration() {
        return 300;
    }

    @Override
    public int getKnockbackDuration() {
        return 120;
    }

    @Override
    public int getDismountDuration() {
        return 100;
    }

    @Override
    public int getDismountSpeed() {
        return 14;
    }

    @Override
    public int getFuelTime() {
        return 200;
    }
}
