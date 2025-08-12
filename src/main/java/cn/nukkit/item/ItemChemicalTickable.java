package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.DyeColor;

public abstract class ItemChemicalTickable extends Item {
    public static final int ACTIVE_BIT = 0b100000;

    public static final int MAX_DURABILITY = 100;

    protected ItemChemicalTickable(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        return !isActivated();
    }

    @Override
    public boolean onUse(Player player, int ticksUsed) {
        if (isActivated()) {
            return false;
        }

        if (ticksUsed < 30) {
            return false;
        }

        setDurabilityDamage(1);
        setActivated(true);
        setActiveTime(player.getServer().getTick());
        player.getInventory().setItemInHand(this);
        return true;
    }

    @Override
    public boolean onRelease(Player player, int ticksUsed) {
        return !isActivated();
    }

    @Override
    public boolean canRelease() {
        return !isActivated();
    }

    @Override
    public int getUseDuration() {
        return 32;
    }

    @Override
    public boolean isChemistryFeature() {
        return true;
    }

    @Override
    public boolean keepDamageTag() {
        return true;
    }

    public void setDurabilityDamage(int damage) {
        setNamedTag(getOrCreateNamedTag().putInt("Damage", damage));
    }

    public int getDurabilityDamage() {
        CompoundTag tag = getNamedTag();
        if (tag == null) {
            return 0;
        }
        return tag.getInt("Damage");
    }

    public void setActiveTime(long levelTick) {
        setNamedTag(getOrCreateNamedTag().putLong("active_time", levelTick));
    }

    public long getActiveTime() {
        CompoundTag tag = getNamedTag();
        if (tag == null) {
            return 0;
        }
        return tag.getLong("active_time");
    }

    public void setActivated(boolean activated) {
        setDamage(activated ? ACTIVE_BIT | getDamage() : getDamage() & ~ACTIVE_BIT);
    }

    public boolean isActivated() {
        return (getDamage() & ACTIVE_BIT) != 0;
    }

    public DyeColor getColor() {
        return DyeColor.getByDyeData(getDamage());
    }

    public boolean tick() {
        if (!isActivated()) {
            return false;
        }

        long activeTime = getActiveTime();
        if (activeTime == 0) {
            setActiveTime(Server.getInstance().getTick());
            return true;
        }

        if ((Server.getInstance().getTick() - activeTime) % getTickRate() != 0) {
            return false;
        }

        int newDamage = getDurabilityDamage() + 10;
        if (newDamage < MAX_DURABILITY) {
            setDurabilityDamage(newDamage);
        } else {
            pop();
        }
        return true;
    }

    protected abstract int getTickRate();
}
