package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.data.Vector3fEntityData;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.entity.*;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.item.Item;
import cn.nukkit.item.randomitem.Fishing;
import cn.nukkit.level.MovingObjectPosition;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.BubbleParticle;
import cn.nukkit.level.particle.WaterParticle;
import cn.nukkit.level.sound.LaunchSound;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.network.protocol.SetEntityLinkPacket;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


/**
 * Created by PetteriM1
 */
public class EntityFishingHook extends EntityProjectile {

    public static final int NETWORK_ID = 77;

    public static final int WAIT_CHANCE = 120;
    public static final int CHANCE = 40;

    public boolean chance = false;
    public int waitChance = WAIT_CHANCE * 2;
    public boolean attracted = false;
    public int attractTimer = 0;
    public boolean caught = false;
    public int coughtTimer = 0;

    public Entity linkedEntity = null;

    public Vector3 fish = null;

    public Item rod = null;

    public EntityFishingHook(FullChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, null);
    }

    public EntityFishingHook(FullChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        super(chunk, nbt, shootingEntity);
        long ownerId = -1;
        if (this.shootingEntity != null) {
            ownerId = this.shootingEntity.getId();
        }
        this.dataProperties.putLong(DATA_OWNER_EID, ownerId);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.2f;
    }

    @Override
    public float getLength() {
        return 0.2f;
    }

    @Override
    public float getHeight() {
        return 0.2f;
    }

    @Override
    public float getGravity() {
        return 0.05f;
    }

    @Override
    public float getDrag() {
        return 0.04f;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        boolean hasUpdate = super.onUpdate(currentTick);

        if (this.linkedEntity != null) {
            this.setPosition(this.linkedEntity);
        } else if (this.isInsideOfWater()) {
            this.motionX = 0;
            this.motionY -= getGravity() * -0.04;
            this.motionZ = 0;
            this.setImmobile();
            this.addMovement(this.x, this.y + this.getBaseOffset() + 0.5, this.z, this.yaw, this.pitch, this.yaw);
            hasUpdate = true;
        } else if (this.isCollided || this.isOnGround() || this.isInsideOfSolid()) {
            this.motionX = 0;
            this.motionY = 0;
            this.motionZ = 0;
            this.keepMovement = false;
            this.updateMovement();
            this.setImmobile();
            hasUpdate = false;
        }

        Random random = ThreadLocalRandom.current();

        if (this.isInsideOfWater()) {
            if (!this.attracted) {
                if (this.waitChance > 0) {
                    --this.waitChance;
                }
                if (this.waitChance == 0) {
                    if (random.nextInt(100) < 90) {
                        this.attractTimer = (random.nextInt(40) + 20);
                        this.spawnFish();
                        this.caught = false;
                        this.attracted = true;
                    } else {
                        this.waitChance = WAIT_CHANCE;
                    }
                }
            } else if (!this.caught) {
                if (this.attractFish()) {
                    this.coughtTimer = (random.nextInt(20) + 30);
                    this.fishBites();
                    this.caught = true;
                }
            } else {
                if (this.coughtTimer > 0) {
                    --this.coughtTimer;
                }
                if (this.coughtTimer == 0) {
                    this.attracted = false;
                    this.caught = false;
                    this.waitChance = WAIT_CHANCE * 3;
                }
            }
        }

        return hasUpdate;
    }

    public int getWaterHeight() {
        for (int y = this.getFloorY(); y < 256; y++) {
            int id = this.level.getBlockIdAt(this.getFloorX(), y, this.getFloorZ());
            if (id == Block.AIR) {
                return y;
            }
        }
        return this.getFloorY();
    }

    public void fishBites() {
        EntityEventPacket pk = new EntityEventPacket();
        pk.eid = this.getId();
        pk.event = EntityEventPacket.FISH_HOOK_HOOK;
        Server.broadcastPacket(this.getViewers().values(), pk);

        EntityEventPacket bubblePk = new EntityEventPacket();
        bubblePk.eid = this.getId();
        bubblePk.event = EntityEventPacket.FISH_HOOK_BUBBLE;
        Server.broadcastPacket(this.getViewers().values(), bubblePk);

        EntityEventPacket teasePk = new EntityEventPacket();
        teasePk.eid = this.getId();
        teasePk.event = EntityEventPacket.FISH_HOOK_TEASE;
        Server.broadcastPacket(this.getViewers().values(), teasePk);

        Random random = ThreadLocalRandom.current();
        for (int i = 0; i < 5; i++) {
            this.level.addParticle(new BubbleParticle(this.setComponents(
                    this.x + random.nextDouble() * 0.5 - 0.25,
                    this.getWaterHeight(),
                    this.z + random.nextDouble() * 0.5 - 0.25
            )));
        }
    }

    public void spawnFish() {
        Random random = ThreadLocalRandom.current();
        this.fish = new Vector3(
                this.x + (random.nextDouble() * 1.2 + 1) * (random.nextBoolean() ? -1 : 1),
                this.getWaterHeight(),
                this.z + (random.nextDouble() * 1.2 + 1) * (random.nextBoolean() ? -1 : 1)
        );
    }

    public boolean attractFish() {
        double multiply = 0.1;
        this.fish.setComponents(
                this.fish.x + (this.x - this.fish.x) * multiply,
                this.fish.y,
                this.fish.z + (this.z - this.fish.z) * multiply
        );
        if (ThreadLocalRandom.current().nextInt(100) < 85) {
            this.level.addParticle(new WaterParticle(this.fish));
        }
        double dist = Math.abs(Math.sqrt(this.x * this.x + this.z * this.z) - Math.sqrt(this.fish.x * this.fish.x + this.fish.z * this.fish.z));
        if (dist < 0.15) {
            return true;
        }
        return false;
    }

    public void reelLine() {
        if (this.shootingEntity instanceof Player && this.caught) {
            this.level.addSound(new LaunchSound(this.shootingEntity));
            Item item = Fishing.getFishingResult(this.rod);
            int experience = ThreadLocalRandom.current().nextInt((3 - 1) + 1) + 1;
            Vector3 motion;

            if (this.shootingEntity != null) {
                motion = this.shootingEntity.subtract(this).multiply(0.1);
                motion.y += Math.sqrt(this.shootingEntity.distance(this)) * 0.08;
            } else {
                motion = new Vector3();
            }

            CompoundTag itemTag = NBTIO.putItemHelper(item);
            itemTag.setName("Item");

            EntityItem itemEntity = new EntityItem(
                    this.level.getChunk((int) this.x >> 4, (int) this.z >> 4, true),
                    new CompoundTag()
                            .putList(new ListTag<DoubleTag>("Pos")
                                    .add(new DoubleTag("", this.getX()))
                                    .add(new DoubleTag("", this.getWaterHeight()))
                                    .add(new DoubleTag("", this.getZ())))
                            .putList(new ListTag<DoubleTag>("Motion")
                                    .add(new DoubleTag("", motion.x))
                                    .add(new DoubleTag("", motion.y))
                                    .add(new DoubleTag("", motion.z)))
                            .putList(new ListTag<FloatTag>("Rotation")
                                    .add(new FloatTag("", ThreadLocalRandom.current().nextFloat() * 360))
                                    .add(new FloatTag("", 0)))
                            .putShort("Health", 5).putCompound("Item", itemTag).putShort("PickupDelay", 1));

            if (this.shootingEntity != null && this.shootingEntity instanceof Player) {
                itemEntity.setOwner(this.shootingEntity.getName());
            }
            itemEntity.spawnToAll();

            Player player = (Player) this.shootingEntity;
            if (experience > 0) {
                player.addExperience(experience);
            }
        }
        if (this.linkedEntity != null) {
            float damage = this.getResultDamage();
            EntityDamageEvent ev;
            if (this.shootingEntity == null) {
                ev = new EntityDamageByEntityEvent(this, this.linkedEntity, DamageCause.PROJECTILE, damage, 0);
            } else {
                ev = new EntityDamageByChildEntityEvent(this.shootingEntity, this, this.linkedEntity, DamageCause.PROJECTILE, damage);
                ((EntityDamageByChildEntityEvent) ev).setKnockBack(0);
            }

            this.linkedEntity.attack(ev);
            if (!ev.isCancelled() && this.shootingEntity != null) {
                this.linkedEntity.setMotion(this.shootingEntity.subtract(this.linkedEntity).divide(8).add(0, 0.3, 0));
            }
        }
        if (this.shootingEntity instanceof Player) {
            EntityEventPacket pk = new EntityEventPacket();
            pk.eid = this.getId();
            pk.event = EntityEventPacket.FISH_HOOK_TEASE;
            Server.broadcastPacket(this.getViewers().values(), pk);
        }
        if (!this.closed) {
            this.kill();
            this.close();
        }
    }

    @Override
    public void spawnTo(Player player) {
        AddEntityPacket pk = new AddEntityPacket();
        pk.entityRuntimeId = this.getId();
        pk.entityUniqueId = this.getId();
        pk.type = NETWORK_ID;
        pk.x = (float) this.x;
        pk.y = (float) this.y;
        pk.z = (float) this.z;
        pk.speedX = (float) this.motionX;
        pk.speedY = (float) this.motionY;
        pk.speedZ = (float) this.motionZ;
        pk.yaw = (float) this.yaw;
        pk.pitch = (float) this.pitch;

        pk.metadata = this.dataProperties;
        player.dataPacket(pk);
        super.spawnTo(player);
    }

    @Override
    public void onCollideWithEntity(Entity entity) {
        if (this.linkedEntity == null) {
            EntityFishingRodCollideEntityEvent collideEntityEvent = new EntityFishingRodCollideEntityEvent(this, entity);
            this.server.getPluginManager().callEvent(collideEntityEvent);
            if (collideEntityEvent.isCancelled()) return;

            this.server.getPluginManager().callEvent(new ProjectileHitEvent(this, MovingObjectPosition.fromEntity(entity)));
            float damage = this.getResultDamage();

            EntityDamageEvent ev;
            if (this.shootingEntity == null) {
                ev = new EntityDamageByEntityEvent(this, entity, DamageCause.PROJECTILE, damage, 0f);
            } else {
                ev = new EntityDamageByChildEntityEvent(this.shootingEntity, this, entity, DamageCause.PROJECTILE, damage);
                ((EntityDamageByChildEntityEvent) ev).setKnockBack(0f);
            }

            entity.attack(ev);

            if (!ev.isCancelled() && this.shootingEntity != null) {
                entity.setMotion(entity.subtract(this.shootingEntity).divide(15).add(0, 0.3, 0));
            }

            this.linkedEntity = entity;

            SetEntityLinkPacket pk;

            pk = new SetEntityLinkPacket();
            pk.vehicleUniqueId = entity.getId();
            pk.riderUniqueId = this.getId();
            pk.type = SetEntityLinkPacket.TYPE_PASSENGER;
            Server.broadcastPacket(this.hasSpawned.values(), pk);

            this.setDataProperty(new Vector3fEntityData(DATA_RIDER_SEAT_POSITION,
                    new Vector3f(0, entity instanceof EntityHuman ? entity.getMountedOffset(this).y - entity.getEyeHeight() : entity.getMountedOffset(this).y, 0)));
        }
    }
}
