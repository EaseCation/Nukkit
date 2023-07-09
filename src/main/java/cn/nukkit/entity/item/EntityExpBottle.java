package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.level.particle.SpellParticle;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author xtypr
 */
public class EntityExpBottle extends EntityProjectile {

    public static final int NETWORK_ID = EntityID.XP_BOTTLE;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.25f;
    }

    @Override
    public float getLength() {
        return 0.25f;
    }

    @Override
    public float getHeight() {
        return 0.25f;
    }

    @Override
    protected float getGravity() {
        return 0.1f;
    }

    @Override
    protected float getDrag() {
        return 0.01f;
    }

    public EntityExpBottle(FullChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, null);
    }

    public EntityExpBottle(FullChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        super(chunk, nbt, shootingEntity);
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        boolean hasUpdate = super.onUpdate(currentTick);

        if (this.age > 1200) {
            this.close();
            hasUpdate = true;
        }

        if (this.isCollided) {
            this.close();
            Particle particle2 = new SpellParticle(this, 0x00385dc6);
            this.getLevel().addParticle(particle2);

            this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_GLASS);

            hasUpdate = true;

            this.getLevel().dropExpOrb(this, ThreadLocalRandom.current().nextInt(3, 12));
        }

        return hasUpdate;
    }

    @Override
    public void spawnTo(Player player) {
        if (this.hasSpawned.containsKey(player.getLoaderId())) {
            return;
        }

        player.dataPacket(createAddEntityPacket());

        super.spawnTo(player);
    }
}
