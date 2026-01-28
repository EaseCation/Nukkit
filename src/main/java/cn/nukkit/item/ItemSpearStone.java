package cn.nukkit.item;

import cn.nukkit.network.protocol.LevelSoundEventPacket;

public class ItemSpearStone extends ItemSpear {
    public ItemSpearStone() {
        this(0, 1);
    }

    public ItemSpearStone(Integer meta) {
        this(meta, 1);
    }

    public ItemSpearStone(Integer meta, int count) {
        super(STONE_SPEAR, meta, count, "Stone Spear");
    }

    @Override
    public int getTier() {
        return TIER_STONE;
    }

    @Override
    public int getMaxDurability() {
        return DURABILITY_STONE;
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
    public int getAttackMissSound() {
        return LevelSoundEventPacket.SOUND_STONE_SPEAR_ATTACK_MISS;
    }

    @Override
    public int getAttackHitSound() {
        return LevelSoundEventPacket.SOUND_STONE_SPEAR_ATTACK_HIT;
    }

    @Override
    public int getAttackDelay() {
        return 14;
    }

    @Override
    public float getDamageMultiplier() {
        return 0.82f;
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
        return 90;
    }

    @Override
    public int getDismountSpeed() {
        return 13;
    }
}
