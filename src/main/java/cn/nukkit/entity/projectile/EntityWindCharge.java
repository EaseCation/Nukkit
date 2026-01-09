package cn.nukkit.entity.projectile;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

public class EntityWindCharge extends EntityProjectile {
    public static final int NETWORK_ID = EntityID.WIND_CHARGE_PROJECTILE;

    public EntityWindCharge(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    public EntityWindCharge(FullChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        super(chunk, nbt, shootingEntity);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.3125f;
    }

    @Override
    public float getHeight() {
        return 0.3125f;
    }

    @Override
    protected float getLiquidInertia() {
        return 1;
    }

    @Override
    public int getResultDamage() {
        return 1;
    }

    @Override
    protected void initEntity() {
        super.initEntity();

    }

    @Override
    public void saveNBT() {
        super.saveNBT();

    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (closed) {
            return false;
        }

        boolean hasUpdate = super.onUpdate(currentTick);

        if (isCollided || hadCollision) {
            explode();

            close();
            hasUpdate = true;
        }

        if (age > 1200 || isCollided || !level.getHeightRange().isValidBlockY(y)) {
            close();
            hasUpdate = false;
        }

        return hasUpdate;
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        switch (entity.getNetworkId()) {
            case EntityID.ENDER_CRYSTAL:
            case EntityID.WIND_CHARGE_PROJECTILE:
            case EntityID.BREEZE_WIND_CHARGE_PROJECTILE:
                return false;
        }
        return super.canCollideWith(entity);
    }

    @Override
    public void spawnTo(Player player) {
        if (hasSpawned.containsKey(player.getLoaderId())) {
            return;
        }

        player.dataPacket(createAddEntityPacket());

        super.spawnTo(player);
    }

    protected void explode() {
        //TODO: impact & block interactive
        //new WindChargeExplosion(this, 1.2f, this, false).explodeB();

        level.addLevelEvent(this, LevelEventPacket.EVENT_PARTICLE_WIND_EXPLOSION, 1);
        level.addLevelEvent(this, LevelEventPacket.EVENT_PARTICLE_BLOCK_EXPLOSION, new CompoundTag()
                .putFloat("originX", (float) x)
                .putFloat("originY", (float) y)
                .putFloat("originZ", (float) z)
                .putFloat("radius", 1.2f)
                .putInt("size", 0));
        level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_WIND_CHARGE_BURST);
    }
}
