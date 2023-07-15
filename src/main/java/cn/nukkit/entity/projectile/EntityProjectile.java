package cn.nukkit.entity.projectile;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.data.LongEntityData;
import cn.nukkit.entity.item.EntityBoat;
import cn.nukkit.entity.item.EntityEndCrystal;
import cn.nukkit.entity.item.EntityMinecartAbstract;
import cn.nukkit.event.entity.*;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.level.MovingObjectPosition;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Mth;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class EntityProjectile extends Entity {

    public static final int DATA_SHOOTER_ID = 17;

    public Entity shootingEntity;

    protected double getDamage() {
        return namedTag.contains("damage") ? namedTag.getDouble("damage") : getBaseDamage();
    }

    protected double getBaseDamage() {
        return 0;
    }

    public boolean hadCollision = false;

    public boolean closeOnCollide = true;

    protected double damage = 0;

    protected float knockBackH = EntityDamageByEntityEvent.GLOBAL_KNOCKBACK_H;
    protected float knockBackV = EntityDamageByEntityEvent.GLOBAL_KNOCKBACK_V;

    public static final int PICKUP_NONE = 0;
    public static final int PICKUP_ANY = 1;
    public static final int PICKUP_CREATIVE = 2;

    protected BlockVector3 stuckToBlockPos;

    protected int entityHitCount;
    @Nullable
    protected LongSet piercingIgnoreEntityIds;

    public EntityProjectile(FullChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, null);
    }

    public EntityProjectile(FullChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        super(chunk, nbt);
        this.shootingEntity = shootingEntity;
        if (shootingEntity != null) {
            this.setDataProperty(new LongEntityData(DATA_SHOOTER_ID, shootingEntity.getId()));
        }
    }

    public int getResultDamage() {
        return Mth.ceil(Math.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ) * getDamage());
    }

    public boolean attack(EntityDamageEvent source) {
        return source.getCause() == DamageCause.VOID && super.attack(source);
    }

    public void onCollideWithEntity(Entity entity) {
        int entityHitCount = this.entityHitCount;
        boolean piercing = entityHitCount > 1;
        if (piercing) {
            if (piercingIgnoreEntityIds == null) {
                piercingIgnoreEntityIds = new LongOpenHashSet(5);
            }
            if (piercingIgnoreEntityIds.size() < entityHitCount) {
                piercingIgnoreEntityIds.add(entity.getId());
            } else {
                close();
                return;
            }
        }

        this.server.getPluginManager().callEvent(new ProjectileHitEvent(this, MovingObjectPosition.fromEntity(entity)));
        float damage = this.getResultDamage();
        if (piercing && entity.isBlocking()) {
            damage *= (entityHitCount - 1) / 4f;
            entityHitCount = 0;
        }

        EntityDamageEvent ev;
        if (this.shootingEntity == null) {
            ev = new EntityDamageByEntityEvent(this, entity, DamageCause.PROJECTILE, damage, knockBackH, knockBackV);
        } else {
            ev = new EntityDamageByChildEntityEvent(this.shootingEntity, this, entity, DamageCause.PROJECTILE, damage);
            ((EntityDamageByChildEntityEvent) ev).setKnockBack(knockBackH, knockBackV);
        }
        boolean success = entity.attack(ev);
        this.hadCollision = true;

        if (success) {
            postHurt(entity);

            if (this.fireTicks > 0) {
                EntityCombustByEntityEvent event = new EntityCombustByEntityEvent(this, entity, 5);
                this.server.getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    entity.setOnFire(event.getDuration());
                }
            }

            if (piercing && piercingIgnoreEntityIds.size() < entityHitCount) {
                return;
            }
        } else if (shouldBounce()) {
            motionX *= -0.1;
            motionY *= -0.1;
            motionZ *= -0.1;
            yaw = (yaw + 180) % 360;

            hadCollision = false;
            return;
        }

        if (closeOnCollide) {
            this.close();
        }
    }

    protected void postHurt(Entity entity) {
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        this.setMaxHealth(1);
        this.setHealth(1);

        if (this.namedTag.contains("Age")) {
            this.age = this.namedTag.getShort("Age");
        }

        if (namedTag.contains("StuckToBlockPosX") && namedTag.contains("StuckToBlockPosY") && namedTag.contains("StuckToBlockPosZ")) {
            stuckToBlockPos = new BlockVector3(namedTag.getInt("StuckToBlockPosX"), namedTag.getInt("StuckToBlockPosY"), namedTag.getInt("StuckToBlockPosZ"));
        }

        entityHitCount = namedTag.getByte("PierceLevel") + 1;

        if (this.namedTag.contains("Knockback")) {
            this.knockBackH = this.namedTag.getFloat("Knockback");
            this.knockBackV = this.namedTag.getFloat("Knockback");
        }
        if (this.namedTag.contains("KnockbackH")) {
            this.knockBackH = this.namedTag.getFloat("KnockbackH");
        }
        if (this.namedTag.contains("KnockbackV")) {
            this.knockBackV = this.namedTag.getFloat("KnockbackV");
        }
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        if (this.onGround) return false;
        if (piercingIgnoreEntityIds != null && piercingIgnoreEntityIds.contains(entity.getId())) {
            return false;
        }
        if (entity instanceof EntityLiving) {
            if (entity instanceof Player) {
                return !((Player) entity).isSpectator() && !entity.getDataFlag(DATA_FLAG_INVISIBLE);
            } else return true;
        } else return entity instanceof EntityEndCrystal || entity instanceof EntityMinecartAbstract || entity instanceof EntityBoat;
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putShort("Age", this.age);

        if (stuckToBlockPos != null) {
            namedTag.putInt("StuckToBlockPosX", stuckToBlockPos.x);
            namedTag.putInt("StuckToBlockPosY", stuckToBlockPos.y);
            namedTag.putInt("StuckToBlockPosZ", stuckToBlockPos.z);
        } else {
            namedTag.remove("StuckToBlockPosX");
            namedTag.remove("StuckToBlockPosY");
            namedTag.remove("StuckToBlockPosZ");
        }

        int piercingLevel = entityHitCount - 1;
        if (piercingLevel > 0) {
            namedTag.putByte("PierceLevel", piercingLevel);
        } else {
            namedTag.remove("PierceLevel");
        }
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

        if (this.isAlive()) {
            MovingObjectPosition movingObjectPosition = null;

            if (!this.isCollided) {
                this.motionY -= this.getGravity();
                this.motionX *= 1 - this.getDrag();
                this.motionZ *= 1 - this.getDrag();
            }

            Vector3 moveVector = new Vector3(this.x + this.motionX, this.y + this.motionY, this.z + this.motionZ);
            MovingObjectPosition blockHitResult = null;

            if (!this.isCollided) {
                blockHitResult = level.clip(copyVec(), moveVector, false, 200);
                if (blockHitResult != null) {
                    Vector3 hitPos = blockHitResult.hitVector;

                    moveVector.setComponents(hitPos);

                    this.motionX = hitPos.x - this.x;
                    this.motionY = hitPos.y - this.y;
                    this.motionZ = hitPos.z - this.z;

                    this.isCollided = true;
                    onGround = true;
                    stuckToBlockPos = new BlockVector3(blockHitResult.blockX, blockHitResult.blockY, blockHitResult.blockZ);

                    blockHitResult.block.onProjectileHit(this, blockHitResult);
                }

                //TODO: hit water sfx
            } else if (shouldStickInGround() && stuckToBlockPos != null) {
                Block stuckToBlock = level.getBlock(stuckToBlockPos);
                if (!stuckToBlock.collidesWithBB(boundingBox.grow(0.06))) {
                    stuckToBlockPos = null;
                    onGround = false;
                    isCollided = false;
                    hadCollision = false;
                    /*
                    Random random = ThreadLocalRandom.current();
                    motionX *= random.nextFloat() * 0.2f;
                    motionY *= random.nextFloat() * 0.2f;
                    motionZ *= random.nextFloat() * 0.2f;
                    */
                } else {
                    onGround = true;
                }
            }

            Entity[] list = this.getLevel().getCollidingEntities(this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1, 1, 1), this);

            double nearDistance = Integer.MAX_VALUE;
            Entity nearEntity = null;

            for (Entity entity : list) {
                if (/*!entity.canCollideWith(this) ||*/ !this.canCollideWith(entity) || (entity == this.shootingEntity && this.ticksLived < 5)) {
                    continue;
                }

                AxisAlignedBB axisalignedbb = entity.boundingBox.grow(0.3, 0.3, 0.3);
                MovingObjectPosition ob = axisalignedbb.calculateIntercept(this, moveVector);

                if (ob == null) {
                    continue;
                }

                double distance = this.distanceSquared(ob.hitVector);

                if (distance < nearDistance) {
                    nearDistance = distance;
                    nearEntity = entity;
                }
            }

            if (nearEntity != null) {
                movingObjectPosition = MovingObjectPosition.fromEntity(nearEntity);
            }

            if (movingObjectPosition != null) {
                if (movingObjectPosition.entityHit != null) {
                    onCollideWithEntity(movingObjectPosition.entityHit);
                    return true;
                }
            }

            this.move(this.motionX, this.motionY, this.motionZ);

            if (this.isCollided && !this.hadCollision) { //collide with block
                this.hadCollision = true;

                this.motionX = 0;
                this.motionY = 0;
                this.motionZ = 0;

                this.addMovement(this.x, this.y + this.getBaseOffset(), this.z, this.yaw, this.pitch, this.yaw);

                if (this.piercingIgnoreEntityIds != null) {
                    this.piercingIgnoreEntityIds.clear();
                }

                if (blockHitResult != null) {
                    this.server.getPluginManager().callEvent(new ProjectileHitEvent(this, blockHitResult));
                }
                return false;
            } else if (!this.isCollided && this.hadCollision) {
                this.hadCollision = false;
            }

            if (!this.hadCollision || Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionY) > 0.00001 || Math.abs(this.motionZ) > 0.00001) {
                updateRotation();
                hasUpdate = true;
            }

            this.updateMovement();

        }

        return hasUpdate;
    }

    @Override
    public boolean move(double dx, double dy, double dz) {
        if (useLegacyMovement()) {
            return super.move(dx, dy, dz);
        }

        if (dx == 0 && dz == 0 && dy == 0) {
            return true;
        }

        if (this.keepMovement) {
            this.boundingBox.offset(dx, dy, dz);
            this.setPosition(this.temporalVector.setComponents((this.boundingBox.getMinX() + this.boundingBox.getMaxX()) / 2, this.boundingBox.getMinY(), (this.boundingBox.getMinZ() + this.boundingBox.getMaxZ()) / 2));
            this.onGround = this.isPlayer;
            return true;
        }

        this.boundingBox.offset(dx, dy, dz);

        this.x += dx;
        this.y += dy;
        this.z += dz;

        this.checkChunks();

        return true;
    }

    public void updateRotation() {
        double f = Math.sqrt((this.motionX * this.motionX) + (this.motionZ * this.motionZ));
        this.yaw = Mth.atan2(this.motionX, this.motionZ) * 180 / Math.PI;
        this.pitch = Mth.atan2(this.motionY, f) * 180 / Math.PI;
    }

    public void inaccurate(float modifier) {
        Random rand = ThreadLocalRandom.current();

        this.motionX += rand.nextGaussian() * 0.007499999832361937 * modifier;
        this.motionY += rand.nextGaussian() * 0.007499999832361937 * modifier;
        this.motionZ += rand.nextGaussian() * 0.007499999832361937 * modifier;
    }

    @Override
    public boolean canBeMovedByCurrents() {
        return false;
    }

    @Override
    public boolean canPassThroughBarrier() {
        return true;
    }

    protected boolean shouldStickInGround() {
        return false;
    }

    protected boolean shouldBounce() {
        return false;
    }

    public int getEntityHitCount() {
        return entityHitCount;
    }

    protected boolean useLegacyMovement() {
        return false;
    }
}
