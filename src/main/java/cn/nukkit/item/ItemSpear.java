package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

public abstract class ItemSpear extends ItemTool {
    protected ItemSpear(int id) {
        super(id);
    }

    protected ItemSpear(int id, Integer meta) {
        super(id, meta);
    }

    protected ItemSpear(int id, Integer meta, int count) {
        super(id, meta, count);
    }

    protected ItemSpear(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    @Override
    public boolean isSpear() {
        return true;
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        return true;
    }

    @Override
    public boolean onUse(Player player, int ticksUsed) {
        return true;
    }

    @Override
    public boolean onRelease(Player player, int ticksUsed) {
        return true;
    }

    @Override
    public boolean canRelease() {
        return true;
    }

    @Override
    public int getUseDuration() {
        return 72000;
    }

    @Override
    public boolean additionalDamageOnBreak() {
        return true;
    }

    @Override
    public int getAttackMissSound() {
        return LevelSoundEventPacket.SOUND_SPEAR_ATTACK_MISS;
    }

    @Override
    public int getAttackHitSound() {
        return LevelSoundEventPacket.SOUND_SPEAR_ATTACK_HIT;
    }

    public abstract int getAttackDelay();

    public abstract float getDamageMultiplier();

    public abstract int getDamageDuration();

    public abstract int getKnockbackDuration();

    public abstract int getDismountDuration();

    public abstract int getDismountSpeed();
}
