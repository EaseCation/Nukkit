package cn.nukkit.item;

import cn.nukkit.network.protocol.LevelSoundEventPacket;

public class ItemSpearDiamond extends ItemSpear {
    public ItemSpearDiamond() {
        this(0, 1);
    }

    public ItemSpearDiamond(Integer meta) {
        this(meta, 1);
    }

    public ItemSpearDiamond(Integer meta, int count) {
        super(DIAMOND_SPEAR, meta, count, "Diamond Spear");
    }

    @Override
    public int getTier() {
        return TIER_DIAMOND;
    }

    @Override
    public int getMaxDurability() {
        return DURABILITY_DIAMOND;
    }

    @Override
    public int getAttackDamage() {
        return 4;
    }

    @Override
    public int getSwingDuration() {
        return 21;
    }

    @Override
    public int getAttackMissSound() {
        return LevelSoundEventPacket.SOUND_DIAMOND_SPEAR_ATTACK_MISS;
    }

    @Override
    public int getAttackHitSound() {
        return LevelSoundEventPacket.SOUND_DIAMOND_SPEAR_ATTACK_HIT;
    }

    @Override
    public int getAttackDelay() {
        return 10;
    }

    @Override
    public float getDamageMultiplier() {
        return 1.075f;
    }

    @Override
    public int getDamageDuration() {
        return 200;
    }

    @Override
    public int getKnockbackDuration() {
        return 80;
    }

    @Override
    public int getDismountDuration() {
        return 60;
    }

    @Override
    public int getDismountSpeed() {
        return 10;
    }
}
