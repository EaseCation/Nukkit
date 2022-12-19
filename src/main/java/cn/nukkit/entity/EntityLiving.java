package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockWater;
import cn.nukkit.entity.data.ShortEntityData;
import cn.nukkit.entity.passive.EntityWaterAnimal;
import cn.nukkit.event.entity.*;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.sound.SoundEnum;
import cn.nukkit.math.Mth;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.BlockIterator;
import co.aikar.timings.Timings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class EntityLiving extends Entity implements EntityDamageable {

    public EntityLiving(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected float getGravity() {
        return 0.08f;
    }

    @Override
    protected float getDrag() {
        return 0.02f;
    }

    @Deprecated
    protected int attackTime = 0;
    protected long nextAllowAttack = 0;  // EC优化，在低TPS时也确保正确的攻击冷却时间

    protected boolean invisible = false;

    protected float movementSpeed = 0.1f;

    //EaseCation 优化
    protected boolean needLivingBaseTick = true;
    protected boolean needCollidingWithRideable = true;

    public boolean isNeedCollidingWithRideable() {
        return needCollidingWithRideable;
    }

    public boolean isNeedLivingBaseTick() {
        return needLivingBaseTick;
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        if (this.namedTag.contains("HealF")) {
            this.namedTag.putFloat("Health", this.namedTag.getShort("HealF"));
            this.namedTag.remove("HealF");
        }

        if (!this.namedTag.contains("Health") || !(this.namedTag.get("Health") instanceof FloatTag)) {
            this.namedTag.putFloat("Health", this.getMaxHealth());
        }

        this.health = this.namedTag.getFloat("Health");
        if (this.health > this.getMaxHealth()) {
            this.setMaxHealth((int) this.health);
        }
    }

    @Override
    public void setHealth(float health) {
        boolean wasAlive = this.isAlive();
        super.setHealth(health);
        if (this.isAlive() && !wasAlive) {
            EntityEventPacket pk = new EntityEventPacket();
            pk.eid = this.getId();
            pk.event = EntityEventPacket.RESPAWN;
            Server.broadcastPacket(this.hasSpawned.values(), pk);
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putFloat("Health", this.getHealth());
    }

    public boolean hasLineOfSight(Entity entity) {
        //todo
        return true;
    }

    public void collidingWith(Entity ent) { // can override (IronGolem|Bats)
        ent.applyEntityCollision(this);
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (System.currentTimeMillis() < this.nextAllowAttack/*this.attackTime > 0*/) {
            EntityDamageEvent lastCause = this.getLastDamageCause();
            if (lastCause != null && (lastCause.getFinalDamage() == 0 || lastCause.getCause() == DamageCause.FIRE_TICK)) {
                //上次伤害是0，这次允许输出
            } else {
                //叠刀时的自我安慰
                if (source instanceof EntityDamageByEntityEvent && source.getCause() == DamageCause.ENTITY_ATTACK && ((EntityDamageByEntityEvent) source).getDamager() instanceof Player)
                    this.getLevel().addSound(this.add(0, 15, 0), SoundEnum.GAME_PLAYER_HURT, 1, 1, (Player) ((EntityDamageByEntityEvent) source).getDamager());
                return false;
            }
        }
        if (this.noDamageTicks > 0) {
            EntityDamageEvent lastCause = this.getLastDamageCause();
            if (lastCause != null && lastCause.getDamage() >= source.getDamage()) {
                return false;
            }
        }

        if (super.attack(source)) {
            if (source instanceof EntityDamageByEntityEvent) {
                Entity e = ((EntityDamageByEntityEvent) source).getDamager();
                /*if (source instanceof EntityDamageByChildEntityEvent) {
                    e = ((EntityDamageByChildEntityEvent) source).getChild();
                }*/

                if (e.isOnFire() && !(e instanceof Player)) {
                    this.setOnFire(2 * this.server.getDifficulty());
                }

                if (((EntityDamageByEntityEvent) source).hasKnockBack()) {
                    double deltaX = this.x - e.x;
                    double deltaZ = this.z - e.z;
                    this.knockBack(e, source.getDamage(), deltaX, deltaZ, ((EntityDamageByEntityEvent) source).getKnockBackH(), ((EntityDamageByEntityEvent) source).getKnockBackV());
                }
            }

            EntityEventPacket pk = new EntityEventPacket();
            pk.eid = this.getId();
            pk.event = this.getHealth() <= 0 ? EntityEventPacket.DEATH_ANIMATION : EntityEventPacket.HURT_ANIMATION;
            Server.broadcastPacket(this.hasSpawned.values(), pk);

            // this.attackTime = source.getAttackCooldown();
            this.nextAllowAttack = System.currentTimeMillis() + source.getAttackCooldown() * 50L; // EC优化，在低TPS时也确保正确的攻击冷却时间

            return true;
        } else {
            return false;
        }
    }

    public void knockBack(Entity attacker, double damage, double x, double z) {
        this.knockBack(attacker, damage, x, z, 0.4);
    }

    public void knockBack(Entity attacker, double damage, double x, double z, double base) {
        double f = Math.sqrt(x * x + z * z);
        if (f <= 0) {
            return;
        }

        f = 1 / f;

        Vector3 motion = new Vector3(this.motionX, this.motionY, this.motionZ);

        motion.x /= 2d;
        motion.y /= 2d;
        motion.z /= 2d;
        motion.x += x * f * base;
        motion.y += base;
        motion.z += z * f * base;

        if (motion.y > base) {
            motion.y = base;
        }

        double length = Math.sqrt(motion.x * motion.x + motion.z * motion.z);
        length = Math.min(Math.max(length, 0.2), 0.58);
        Vector2 multiply = new Vector2(x, z).normalize().multiply(length);
        motion.x = multiply.x;
        motion.z = multiply.y;

        //this.getServer().getLogger().debug("[knockback] xz=" + new Vector2(motion.x, motion.z).length() + " y=" + motion.y);

        this.setMotion(motion);
    }

    public void knockBack(Entity attacker, double damage, double x, double z, double baseH, double baseV) {
        double f = Math.sqrt(x * x + z * z);
        if (f <= 0) {
            return;
        }

        f = 1 / f;

        Vector3 motion = new Vector3(this.motionX, this.motionY, this.motionZ);

        motion.x /= 2d;
        motion.y /= 2d;
        motion.z /= 2d;
        motion.x += x * f * baseH;
        motion.y += baseV;
        motion.z += z * f * baseH;

        if (motion.y > baseV) {
            motion.y = baseV;
        }

        // 修正击退方向
        double length = Math.sqrt(motion.x * motion.x + motion.z * motion.z);
        length = Math.min(Math.max(length, 0.2), 0.58);
        Vector2 multiply = new Vector2(x, z).normalize().multiply(length);
        motion.x = multiply.x;
        motion.z = multiply.y;

        //this.getServer().getLogger().debug("[knockback] xz=" + new Vector2(motion.x, motion.z).length() + " y=" + motion.y);

        this.setMotion(motion);
    }

    @Override
    public void kill() {
        if (!this.isAlive()) {
            return;
        }
        super.kill();
        EntityDeathEvent ev = new EntityDeathEvent(this, this.getDrops());
        this.server.getPluginManager().callEvent(ev);

        if (this.level.getGameRules().getBoolean(GameRule.DO_ENTITY_DROPS)) {
            for (cn.nukkit.item.Item item : ev.getDrops()) {
                this.getLevel().dropItem(this, item);
            }
        }
    }

    @Override
    public boolean entityBaseTick() {
        return this.entityBaseTick(1);
    }

    @Override
    public boolean entityBaseTick(int tickDiff) {
        Timings.livingEntityBaseTickTimer.startTiming();

        boolean hasUpdate = super.entityBaseTick(tickDiff);

        if (this.isAlive() && this.needLivingBaseTick) {
            boolean breathing = canBreathe();
            this.setDataFlag(DATA_FLAGS, DATA_FLAG_BREATHING, breathing);

            if (this.isInsideOfSolid()) {
                hasUpdate = true;
                this.attack(new EntityDamageEvent(this, DamageCause.SUFFOCATION, 1));
            }

            if (isPlayer) {
                int x = getFloorX();
                int y = getFloorY();
                int z = getFloorZ();
                //TODO: getCollisionBlocks
                int block = level.getBlock(x, y, z).getId();
                int below = level.getBlock(x, y - 1, z).getId();
                boolean inScaffolding = block == BlockID.SCAFFOLDING;
                boolean inPowderSnow = block == BlockID.POWDER_SNOW;
                boolean overScaffolding = below == BlockID.SCAFFOLDING;
                boolean overPowderSnow = below == BlockID.POWDER_SNOW; //TODO: check
                PlayerInventory inventory = ((EntityHumanType) this).getInventory();
                boolean leatherBoots = inventory != null && inventory.getBoots().getId() == ItemID.LEATHER_BOOTS;
                setDataFlag(DATA_FLAGS_EXTENDED, DATA_FLAG_IN_SCAFFOLDING, inScaffolding);
                setDataFlag(DATA_FLAGS_EXTENDED, DATA_FLAG_IN_ASCENDABLE_BLOCK, inScaffolding || leatherBoots && inPowderSnow);
                setDataFlag(DATA_FLAGS_EXTENDED, DATA_FLAG_OVER_SCAFFOLDING, overScaffolding);
                setDataFlag(DATA_FLAGS_EXTENDED, DATA_FLAG_OVER_DESCENDABLE_BLOCK, leatherBoots && overPowderSnow || overScaffolding && !level.getBlock(x, y - 2, z).isAir());
                if (inScaffolding || overScaffolding || inPowderSnow || overPowderSnow) {
                    resetFallDistance();
                }
            }

            if (!this.hasEffect(Effect.WATER_BREATHING) && !breathing) {
                if (this instanceof EntityWaterAnimal) {
                    this.setDataProperty(new ShortEntityData(DATA_AIR, 400));
                } else {
                    hasUpdate = true;
                    int airTicks = this.getDataPropertyShort(DATA_AIR) - tickDiff;

                    if (airTicks <= -20) {
                        airTicks = 0;
                        this.attack(new EntityDamageEvent(this, DamageCause.DROWNING, 2));
                    }

                    this.setDataProperty(new ShortEntityData(DATA_AIR, airTicks));
                }
            } else {
                if (this instanceof EntityWaterAnimal) {
                    hasUpdate = true;
                    int airTicks = this.getDataPropertyInt(DATA_AIR) - tickDiff;

                    if (airTicks <= -20) {
                        airTicks = 0;
                        this.attack(new EntityDamageEvent(this, DamageCause.DROWNING, 2));
                    }

                    this.setDataProperty(new ShortEntityData(DATA_AIR, airTicks));
                } else {
                    hasUpdate = true;
                    int airTicks = this.getDataPropertyInt(DATA_AIR) + tickDiff * 5;

                    if (airTicks > this.getDataPropertyShort(Entity.DATA_MAX_AIR)) {
                        airTicks = this.getDataPropertyShort(Entity.DATA_MAX_AIR);
                    }
                    this.setDataProperty(new ShortEntityData(DATA_AIR, airTicks));
                }
            }
        }

        /*if (this.attackTime > 0) {
            this.attackTime -= tickDiff;
        }*/
        if (this.riding == null && this.needCollidingWithRideable) {
            for (Entity entity : level.getNearbyEntities(this.boundingBox.grow(0.20000000298023224D, 0.0D, 0.20000000298023224D), this)) {
                if (entity instanceof EntityRideable) {
                    this.collidingWith(entity);
                }
            }
        }

        Timings.livingEntityBaseTickTimer.stopTiming();

        return hasUpdate;
    }

    public Item[] getDrops() {
        return new Item[0];
    }

    public Block[] getLineOfSight(int maxDistance) {
        return this.getLineOfSight(maxDistance, 0);
    }

    public Block[] getLineOfSight(int maxDistance, int maxLength) {
        return this.getLineOfSight(maxDistance, maxLength, new Integer[]{});
    }

    @Deprecated
    public Block[] getLineOfSight(int maxDistance, int maxLength, Map<Integer, Object> transparent) {
        return this.getLineOfSight(maxDistance, maxLength, transparent.keySet().toArray(new Integer[0]));
    }

    public Block[] getLineOfSight(int maxDistance, int maxLength, Integer[] transparent) {
        if (maxDistance > 120) {
            maxDistance = 120;
        }

        if (transparent != null && transparent.length == 0) {
            transparent = null;
        }

        List<Block> blocks = new ArrayList<>();

        BlockIterator itr = new BlockIterator(this.level, this.getPosition(), this.getDirectionVector(), this.getEyeHeight(), maxDistance);

        while (itr.hasNext()) {
            Block block = itr.next();
            blocks.add(block);

            if (maxLength != 0 && blocks.size() > maxLength) {
                blocks.remove(0);
            }

            int id = block.getId();

            if (transparent == null) {
                if (id != 0) {
                    break;
                }
            } else {
                if (Arrays.binarySearch(transparent, id) < 0) {
                    break;
                }
            }
        }

        return blocks.toArray(new Block[0]);
    }

    public Block getTargetBlock(int maxDistance) {
        return getTargetBlock(maxDistance, new Integer[]{});
    }

    @Deprecated
    public Block getTargetBlock(int maxDistance, Map<Integer, Object> transparent) {
        return getTargetBlock(maxDistance, transparent.keySet().toArray(new Integer[0]));
    }

    public Block getTargetBlock(int maxDistance, Integer[] transparent) {
        try {
            Block[] blocks = this.getLineOfSight(maxDistance, 1, transparent);
            Block block = blocks[0];
            if (block != null) {
                if (transparent != null && transparent.length != 0) {
                    if (Arrays.binarySearch(transparent, block.getId()) < 0) {
                        return block;
                    }
                } else {
                    return block;
                }
            }
        } catch (Exception ignored) {

        }

        return null;
    }

    public void setMovementSpeed(float speed) {
        this.movementSpeed = speed;
    }

    public float getMovementSpeed() {
        return this.movementSpeed;
    }

    public int getAirTicks() {
        return this.getDataPropertyShort(DATA_AIR);
    }

    public void setAirTicks(int ticks) {
        this.setDataProperty(new ShortEntityData(DATA_AIR, ticks));
    }

    public boolean canBreathe() {
        double y = this.y + this.getEyeHeight();
        Block block = level.getBlock(getFloorX(), Mth.floor(y), getFloorZ());

        if (block.getId() == BlockID.BUBBLE_COLUMN) {
            return true;
        }

        if (!block.isWater()) {
            block = level.getExtraBlock(block);
            if (!block.isWater()) {
                return true;
            }
        }

        return y >= (block.y + 1) - (((BlockWater) block).getFluidHeightPercent() - 0.1111111);
    }
}
