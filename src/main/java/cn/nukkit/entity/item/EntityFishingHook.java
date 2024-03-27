package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.data.LongEntityData;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.entity.*;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.player.PlayerFishEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.randomitem.Fishing;
import cn.nukkit.level.MovingObjectPosition;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.BubbleParticle;
import cn.nukkit.level.particle.WaterParticle;
import cn.nukkit.level.sound.LaunchSound;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Mth;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.EntityEventPacket;

import java.util.Collection;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by PetteriM1
 */
public class EntityFishingHook extends EntityProjectile {

    public static final int NETWORK_ID = EntityID.FISHING_HOOK;

    public int waitChance = 120;
    public int waitTimer = 240;
    public boolean attracted = false;
    public int attractTimer = 0;
    public boolean caught = false;
    public int caughtTimer = 0;
    public boolean canCollide = true;

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
    protected void initEntity() {
        super.initEntity();
        if (this.age > 0) {
            this.close();
        }
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
    public float getHeight() {
        return 0.2f;
    }

    @Override
    public float getGravity() {
        return 0.07f;
    }

    @Override
    public float getDrag() {
        return 0.05f;
    }

    @Override
    public boolean canCollide() {
        return this.canCollide;
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

        boolean hasUpdate = this.entityBaseTick(tickDiff);

        if (false) {
            // send debug info
            if (shootingEntity instanceof Player) {
                ((Player) shootingEntity).sendActionBar(this.debugText() + " "
                        + this.isCollided + " "
                );
            }
        }

        if (!this.isAlive()) {
            return hasUpdate;
        }

        long target = this.getDataPropertyLong(DATA_TARGET_EID);
        if (target != 0) {
            Entity ent = this.level.getEntity(target);
            if (ent == null || !ent.isAlive()) {
                this.setTarget(0);
            } else {
                this.setPosition(new Vector3(ent.x, ent.y + (getHeight() * 0.75f), ent.z));
            }
            return false;
        }

        boolean updateGravity = false;
        this.motionY -= this.getGravity();
        if (!this.isCollided) {
            this.motionX *= 1 - this.getDrag();
            this.motionZ *= 1 - this.getDrag();
        } else {
            updateGravity = true;
        }
        boolean buoyancy = false;
        boolean inWater = false;

        Vector3 moveVector = new Vector3(this.x + this.motionX, this.y + this.motionY, this.z + this.motionZ);
        MovingObjectPosition blockHitResult = level.clip(copyVec(), moveVector, true, 200, canPassThroughBarrier());
        if (updateGravity) {
            if (blockHitResult != null) {
                Vector3 hitPos = blockHitResult.hitVector;
                double deltaY = hitPos.y - this.y;
                if (deltaY < -Mth.EPSILON) {
                    motionY = deltaY;

                    moveVector.y = hitPos.y;

                    stuckToBlockPos = new BlockVector3(blockHitResult.blockX, blockHitResult.blockY, blockHitResult.blockZ);
                } else {
                    motionY = 0;

                    Block hitBlock = blockHitResult.block;
                    inWater = hitBlock.isWater();
                    if (!inWater && !hitBlock.isAir() && hitBlock.canContainWater()) {
                        inWater = level.getExtraBlock(hitBlock).isWater();
                    }
                    if (inWater) {
                        Block above = level.getBlock(hitBlock.up());
                        if (!above.isWater() && !above.isAir() && above.canContainWater()) {
                            above = level.getExtraBlock(above);
                        }
                        if (above.isWater()) {
                            buoyancy = true;
                            motionY = 0.35f;

                            stuckToBlockPos = null;
                            isCollided = false;
                            hadCollision = false;
                        }
                    }
                }
            } else if (stuckToBlockPos != null) {
                AxisAlignedBB aabb = boundingBox.grow(0.06);
                Block stuckToExtraBlock = level.getExtraBlock(stuckToBlockPos);
                boolean stuck = stuckToExtraBlock.collidesWithBB(aabb, stuckToExtraBlock.isLiquid());
                if (!stuck) {
                    Block stuckToBlock = level.getBlock(stuckToBlockPos);
                    stuck = stuckToBlock.collidesWithBB(aabb, stuckToBlock.isLiquid());
                    if (stuckToBlock.isLava()) {
                        close();
                        return false;
                    } else if (stuck) {
                        inWater = stuckToBlock.isWater();
                        hasUpdate = true;
                    }
                } else {
                    inWater = stuckToExtraBlock.isWater();
                    hasUpdate = true;
                }
                Block above = level.getExtraBlock(stuckToBlockPos.up());
                if (!above.isWater()) {
                    above = level.getBlock(above);
                }
                if (inWater && above.isWater()) {
                    buoyancy = true;

                    stuckToBlockPos = null;
                    isCollided = false;
                    hadCollision = false;
                } else if (!stuck) {
                    stuckToBlockPos = null;
                    isCollided = false;
                    hadCollision = false;
                }
            } else {
                isCollided = false;
                hadCollision = false;
            }
        } else if (blockHitResult != null) {
            Vector3 hitPos = blockHitResult.hitVector;

            moveVector.setComponents(hitPos);

            this.motionX = (hitPos.x - this.x) * 0.9;
            this.motionY = hitPos.y - this.y;
            this.motionZ = (hitPos.z - this.z) * 0.9;

            this.isCollided = true;
            stuckToBlockPos = new BlockVector3(blockHitResult.blockX, blockHitResult.blockY, blockHitResult.blockZ);

            //TODO: water sfx
        }

        if (!updateGravity) {
            Entity nearEntity = null;
            double nearDistance = Integer.MAX_VALUE;

            Entity[] entities = this.getLevel().getCollidingEntities(this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1), this);
            for (Entity entity : entities) {
                if (!this.canCollideWith(entity) || entity == this.shootingEntity) {
                    continue;
                }

                AxisAlignedBB aabb = entity.boundingBox.grow(0.3, 0.3, 0.3);
                MovingObjectPosition entityHitResult = aabb.calculateIntercept(this, moveVector);
                if (entityHitResult == null) {
                    continue;
                }

                double distance = this.distanceSquared(entityHitResult.hitVector);
                if (distance < nearDistance) {
                    nearDistance = distance;
                    nearEntity = entity;
                }
            }

            if (nearEntity != null && onCollideWithEntity(nearEntity)) {
                return true;
            }
        }

        this.move(this.motionX, this.motionY, this.motionZ);

        if (this.isCollided && !this.hadCollision) {
            this.hadCollision = true;

            this.motionX = 0;
            this.motionY = 0;
            this.motionZ = 0;

//            this.addMovement(this.x, this.y + this.getBaseOffset(), this.z, this.yaw, this.pitch, this.yaw);
            return false;
        } else if (!this.isCollided && this.hadCollision) {
            this.hadCollision = false;
        }

        if (!this.hadCollision || Math.abs(this.motionX) > Mth.EPSILON || Math.abs(this.motionY) >Mth.EPSILON || Math.abs(this.motionZ) > Mth.EPSILON) {
            updateRotation();
            hasUpdate = true;
        }

        if (buoyancy) {
            motionY = 0.35f + getGravity();
        }
        //TODO: underwater inertia

        this.updateMovement();

        if (inWater) {
            if (this.waitTimer == 240) {
                this.waitTimer = this.waitChance << 1;
            } else if (this.waitTimer == 360) {
                this.waitTimer = this.waitChance * 3;
            }
            if (!this.attracted) {
                if (this.waitTimer > 0) {
                    --this.waitTimer;
                }
                if (this.waitTimer == 0) {
                    Random random = ThreadLocalRandom.current();
                    if (random.nextInt(100) < 90) {
                        this.attractTimer = (random.nextInt(40) + 20);
                        this.spawnFish();
                        this.caught = false;
                        this.attracted = true;
                    } else {
                        this.waitTimer = this.waitChance;
                    }
                }
            } else if (!this.caught) {
                if (this.attractFish()) {
                    this.caughtTimer = (ThreadLocalRandom.current().nextInt(20) + 30);
                    this.fishBites();
                    this.caught = true;
                }
            } else {
                if (this.caughtTimer > 0) {
                    --this.caughtTimer;
                }
                if (this.caughtTimer == 0) {
                    this.attracted = false;
                    this.caught = false;
                    this.waitTimer = this.waitChance * 3;
                }
            }
        }

        return hasUpdate;
    }

    public int getWaterHeight() {
        for (int y = this.getFloorY(); y < level.getHeightRange().getMaxY(); y++) {
            if (this.level.getBlock(this.getFloorX(), y, this.getFloorZ()).isAir()) {
                return y;
            }
        }
        return this.getFloorY();
    }

    public void fishBites() {
        Collection<Player> viewers = this.getViewers().values();

        EntityEventPacket pk = new EntityEventPacket();
        pk.eid = this.getId();
        pk.event = EntityEventPacket.FISH_HOOK_HOOK;
        Server.broadcastPacket(viewers, pk);

        EntityEventPacket bubblePk = new EntityEventPacket();
        bubblePk.eid = this.getId();
        bubblePk.event = EntityEventPacket.FISH_HOOK_BUBBLE;
        Server.broadcastPacket(viewers, bubblePk);

        EntityEventPacket teasePk = new EntityEventPacket();
        teasePk.eid = this.getId();
        teasePk.event = EntityEventPacket.FISH_HOOK_TEASE;
        Server.broadcastPacket(viewers, teasePk);

        Random random = ThreadLocalRandom.current();
        for (int i = 0; i < 5; i++) {
            this.level.addParticle(new BubbleParticle(this.setComponents(
                    this.x + random.nextFloat() * 0.5f - 0.25f,
                    this.getWaterHeight(),
                    this.z + random.nextFloat() * 0.5f - 0.25f
            )));
        }
    }

    public void spawnFish() {
        Random random = ThreadLocalRandom.current();
        this.fish = new Vector3(
                this.x + (random.nextFloat() * 1.2f + 1) * (random.nextBoolean() ? -1 : 1),
                this.getWaterHeight(),
                this.z + (random.nextFloat() * 1.2f + 1) * (random.nextBoolean() ? -1 : 1)
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
        return dist < 0.15;
    }

    public void reelLine() {
        if (this.shootingEntity instanceof Player && this.caught) {
            this.level.addSound(new LaunchSound(this.shootingEntity));
            Player player = (Player) this.shootingEntity;
            Item item = Fishing.getFishingResult(this.rod);
            int experience = ThreadLocalRandom.current().nextInt(1, 4);
            Vector3 motion = player.subtract(this).multiply(0.1);
            motion.y += Math.sqrt(player.distance(this)) * 0.08;

            PlayerFishEvent event = new PlayerFishEvent(player, this, item, experience, motion);
            this.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                EntityItem itemEntity = new EntityItem(
                        this.level.getChunk(this.getChunkX(), this.getChunkZ(), true),
                        Entity.getDefaultNBT(new Vector3(this.x, this.getWaterHeight(), this.z), event.getMotion(), ThreadLocalRandom.current().nextFloat() * 360, 0)
                                .putShort("Health", 5)
                                .putCompound("Item", NBTIO.putItemHelper(event.getLoot()))
                                .putShort("PickupDelay", 1));

                itemEntity.setOwner(player.getName());
                itemEntity.spawnToAll();

                player.getLevel().dropExpOrb(player, event.getExperience());
            }
        }

        if (this.shootingEntity != null) {
            long target = this.getDataPropertyLong(DATA_TARGET_EID);
            if (target != 0) {
                Entity entity = this.level.getEntity(target);
                if (entity != null && entity.isAlive()) {
                    boolean riding;
                    if (entity.isRiding()) {
                        riding = !entity.getRiding().dismountEntity(entity);
                    } else {
                        riding = false;
                    }

                    if (!riding) {
                        if (false) {
                            // van
                            Vector3 diff = this.shootingEntity.add(0, 1, 0).subtract(entity).multiply(0.1);
                            diff.y = Math.sqrt(diff.length()) * 0.08;
                            entity.setMotion(diff);
                        } else {
                            // ec
                            entity.setMotion(this.shootingEntity.subtract(entity).divide(8).add(0, 0.3, 0));
                        }
                    }
                }
            }
        }

        this.close();
    }

    @Override
    public void spawnTo(Player player) {
        if (this.hasSpawned.containsKey(player.getLoaderId())) {
            return;
        }

        player.dataPacket(createAddEntityPacket());

        super.spawnTo(player);
    }

    @Override
    public boolean onCollideWithEntity(Entity entity) {
        EntityFishingRodCollideEntityEvent collideEntityEvent = new EntityFishingRodCollideEntityEvent(this, entity);
        this.server.getPluginManager().callEvent(collideEntityEvent);
        if (collideEntityEvent.isCancelled()) {
            return true;
        }

        this.server.getPluginManager().callEvent(new ProjectileHitEvent(this, MovingObjectPosition.fromEntity(entity)));
        float damage = this.getResultDamage();

        EntityDamageEvent ev;
        if (this.shootingEntity == null) {
            ev = new EntityDamageByEntityEvent(this, entity, DamageCause.PROJECTILE, damage, 0f, 0f);
        } else {
            ev = new EntityDamageByChildEntityEvent(this.shootingEntity, this, entity, DamageCause.PROJECTILE, damage);
            ((EntityDamageByChildEntityEvent) ev).clearKnockBack();
        }

        if (entity.attack(ev)) {
            this.setTarget(entity.getId());

            if (this.shootingEntity != null) {
                entity.setMotion(entity.subtract(this.shootingEntity).divide(15).add(0, 0.3, 0)); // 这边还是用EC的特殊钩回motion，营造EC的特殊手感
                //entity.setMotion(entity.getMotion().add(entity.subtract(this.shootingEntity).multiply(0.1)));
            }
        } else if (entity instanceof Player && ((Player) entity).getGamemode() == Player.CREATIVE) {
            setTarget(entity.getId());
        } else {
            close();
        }
        return true;
    }

    public void checkLure() {
        if (rod != null) {
            Enchantment ench = rod.getEnchantment(Enchantment.LURE);
            if (ench != null) {
                this.waitChance = 120 - (25 * ench.getLevel());
            }
        }
    }

    public void setTarget(long eid) {
        this.setDataProperty(new LongEntityData(DATA_TARGET_EID, eid));
        this.canCollide = eid == 0;
    }

    @Override
    protected boolean shouldStickInGround() {
        return true;
    }
}
