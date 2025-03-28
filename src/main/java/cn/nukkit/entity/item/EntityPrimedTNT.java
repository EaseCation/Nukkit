package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityExplosive;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.data.IntEntityData;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.EntityExplosionPrimeEvent;
import cn.nukkit.level.Explosion;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.SmokeParticle;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

/**
 * @author MagicDroidX
 */
public class EntityPrimedTNT extends Entity implements EntityExplosive {

    public static final int NETWORK_ID = EntityID.TNT;

    @Override
    public float getWidth() {
        return 0.98f;
    }

    @Override
    public float getHeight() {
        return 0.98f;
    }

    @Override
    protected float getGravity() {
        return 0.04f;
    }

    @Override
    protected float getDrag() {
        return 0.02f;
    }

    @Override
    protected float getBaseOffset() {
        return 0.49f;
    }

    @Override
    public boolean canCollide() {
        return false;
    }

    protected int fuse;
    protected int force;
    protected boolean allowUnderwater;

    protected Entity source;

    public EntityPrimedTNT(FullChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, null);
    }

    public EntityPrimedTNT(FullChunk chunk, CompoundTag nbt, Entity source) {
        super(chunk, nbt);
        this.source = source;
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        return source.getCause() == DamageCause.VOID && super.attack(source);
    }

    protected void initEntity() {
        super.initEntity();

        if (namedTag.contains("Fuse")) {
            fuse = namedTag.getByte("Fuse");
        } else {
            fuse = 80;
        }
        if (namedTag.contains("Force")) {
            force = namedTag.getByte("Force");
        } else {
            force = 4;
        }
        if (namedTag.contains("AllowUnderwater")) {
            allowUnderwater = namedTag.getBoolean("AllowUnderwater");
        } else {
            allowUnderwater = false;
        }

        this.setDataFlag(DATA_FLAG_IGNITED, true, false);
        this.setDataProperty(new IntEntityData(DATA_FUSE_LENGTH, fuse), false);

        this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_FIZZ);
    }

    public boolean canCollideWith(Entity entity) {
        return false;
    }

    public void saveNBT() {
        super.saveNBT();
        namedTag.putByte("Fuse", fuse);
        namedTag.putByte("Force", force);
    }

    public boolean onUpdate(int currentTick) {
        if (closed) {
            return false;
        }

        int tickDiff = currentTick - lastUpdate;
        if (tickDiff <= 0 && !justCreated) {
            return true;
        }
        lastUpdate = currentTick;

        this.setDataProperty(new IntEntityData(DATA_FUSE_LENGTH, fuse));
        this.getLevel().addParticle(new SmokeParticle(this.add(0, 1, 0)));
        this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_FIZZ);

        boolean hasUpdate = entityBaseTick(tickDiff);

        if (isAlive()) {
            motionY -= getGravity();

            move(motionX, motionY, motionZ);

            float friction = 1 - getDrag();

            motionX *= friction;
            motionY *= friction;
            motionZ *= friction;

            updateMovement();

            if (onGround) {
                motionY *= -0.5;
                motionX *= 0.7;
                motionZ *= 0.7;
            }

            fuse -= tickDiff;

            if (fuse <= 0) {
                if (this.level.getGameRules().getBoolean(GameRule.TNT_EXPLODES)) {
                    explode();
                }
                this.close();
            }
        }

        return hasUpdate || fuse >= 0 || Math.abs(motionX) > 0.00001 || Math.abs(motionY) > 0.00001 || Math.abs(motionZ) > 0.00001;
    }

    public void explode() {
        EntityExplosionPrimeEvent event = new EntityExplosionPrimeEvent(this, this.force);
        server.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        Explosion explosion = new Explosion(this, event.getForce(), this);
        explosion.setAllowUnderwater(allowUnderwater);
        if (event.isBlockBreaking()) {
            explosion.explodeA();
        }
        explosion.explodeB();
    }

    public void spawnTo(Player player) {
        if (this.hasSpawned.containsKey(player.getLoaderId())) {
            return;
        }

        player.dataPacket(createAddEntityPacket());

        super.spawnTo(player);
    }

    public Entity getSource() {
        return source;
    }
}
