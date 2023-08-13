package cn.nukkit.entity;

import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class EntityHanging extends Entity {

    private int checkInterval;

    public EntityHanging(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        this.setMaxHealth(1);
        this.setHealth(1);
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        int tickDiff = currentTick - this.lastUpdate;
        if (tickDiff <= 0 && !this.justCreated) {
            return true;
        }
        this.lastUpdate = currentTick;

        if (!this.isAlive()) {
            this.close();
        } else if ((this.checkInterval += tickDiff) >= 100) {
            this.checkInterval = 0;

            if (!this.isSurfaceValid()) {
                this.close();
                this.dropItem(null);
            }
        }

        return true;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (!super.attack(source)) {
            return false;
        }

        this.dropItem(source instanceof EntityDamageByEntityEvent ? ((EntityDamageByEntityEvent) source).getDamager() : null);
        this.close();
        return true;
    }

    @Override
    public boolean canBeMovedByCurrents() {
        return false;
    }

    @Override
    public void updateMovement() {
    }

    protected abstract boolean isSurfaceValid();

    protected abstract void dropItem(Entity entity);
}
