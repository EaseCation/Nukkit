package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockWater;
import cn.nukkit.entity.data.FloatEntityData;
import cn.nukkit.entity.data.ShortEntityData;
import cn.nukkit.entity.passive.EntityWaterAnimal;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.knockback.KnockbackManager;
import cn.nukkit.knockback.KnockbackProfile;
import cn.nukkit.event.entity.EntityDeathEvent;
import cn.nukkit.event.entity.EntityResurrectEvent;
import cn.nukkit.inventory.ArmorInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Mth;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.network.protocol.AnimatePacket;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.potion.Effect;
import cn.nukkit.potion.EffectID;
import cn.nukkit.utils.BlockIterator;
import it.unimi.dsi.fastutil.objects.ObjectIntPair;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

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

    protected long nextAllowAttack = 0;  // EC优化，在低TPS时也确保正确的攻击冷却时间
    protected long nextAllowKnockback;
    protected float lastHurt;

    @Nullable
    private KnockbackProfile knockbackProfile;

    public KnockbackProfile getKnockbackProfile() {
        return knockbackProfile != null ? knockbackProfile : KnockbackManager.get().getDefaultProfile();
    }

    public void setKnockbackProfile(@Nullable KnockbackProfile profile) {
        this.knockbackProfile = profile;
    }

    protected boolean invisible = false;

    protected float movementSpeed = 0.1f;
    protected float waterMovementSpeed = 0.02f;
    protected float lavaMovementSpeed = 0.02f;

    protected int turtleTicks;

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

        this.setDataFlag(DATA_FLAG_HIDDEN_WHEN_INVISIBLE, true, false);
    }

    @Override
    public boolean setHealth(float health) {
        boolean wasAlive = this.isAlive();
        if (!super.setHealth(health)) {
            return false;
        }
        if (this.isAlive() && !wasAlive) {
            EntityEventPacket pk = new EntityEventPacket();
            pk.eid = this.getId();
            pk.event = EntityEventPacket.RESPAWN;
            Server.broadcastPacket(this.hasSpawned.values(), pk);
        }
        return true;
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
    public boolean canBeAffected(int effectId) {
        return !(this instanceof EntitySmite) || effectId != Effect.REGENERATION && effectId != Effect.POISON;
    }

    protected boolean isInvulnerableTo(EntityDamageEvent source) {
        return this.invulnerable && source.getCause() != DamageCause.VOID && !(source.getEntity() instanceof Player player && player.isCreativeLike());
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (this.isClosed() || !this.isAlive()) {
            return false;
        }
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        if (source.getCause() == DamageCause.FIRE_TICK || source.getCause() == DamageCause.LAVA || source.getCause() == DamageCause.FIRE || source.getCause() == DamageCause.MAGMA || source.getCause() == DamageCause.CAMPFIRE || source.getCause() == DamageCause.SOUL_CAMPFIRE) {
            if (this.hasEffect(EffectID.FIRE_RESISTANCE)) {
                return false;
            }
        }

        boolean notSuicide = source.getCause() != DamageCause.SUICIDE;
        if (notSuicide) {
            if (this.noDamageTicks > 0) {
                EntityDamageEvent lastCause = this.getLastDamageCause();
                if (lastCause != null && lastCause.getDamage() >= source.getDamage()) {
                    return false;
                }
            }
        }

        float damage = source.getDamage();
        boolean knockback;
        // 偷偷扣血+击退, 不要让玩家知道自己被打红了
        boolean hurtAnimationSelf;
        long time = 0;
        if (notSuicide) {
            time = System.currentTimeMillis();
            // 冷却中
            if (time < this.nextAllowAttack) {
                if (damage > 0) {
                    if (damage <= this.lastHurt) {
                        return false;
                    }
                    source.setDamage(damage - lastHurt);
                    if (!damageEntity0(source)) {
                        return false;
                    }
                    this.lastHurt = damage;
                    // 这边不修改冷却时间，因为只是补上了冷却期间的伤害差
                    // 冷却时的伤害补充，不应该造成击退
                    knockback = false;
                    // 冷却时的伤害补充，不应该变红、发出音效
                    hurtAnimationSelf = false;
                } else {
                    // EC特性：伤害为0的攻击，无冷却，并且造成击退（但是不修改lastHurt，确保有伤害的攻击是继续冷却的）
                    if (!damageEntity0(source)) {
                        return false;
                    }
                    // 这种情况下也应该变红和音效，以及击退
                    knockback = true;
                    hurtAnimationSelf = false;
                }
            } else {
                if (!damageEntity0(source)) {
                    return false;
                }
                this.lastHurt = damage;
                // EC优化，在低TPS时也确保正确的攻击冷却时间
                this.nextAllowAttack = time + source.getAttackCooldown() * 50L;
                // 正常攻击，变红和击退都安排上
                knockback = true;
                hurtAnimationSelf = true;
            }
        } else {
            // 自杀，应该有变红动画，但是没有击退
            hurtAnimationSelf = true;
            knockback = false;
        }

        // 变红（实际上对他人来说，始终能看到变红）
        this.onHurt(source);
        // 如果是玩家，给玩家自己发受伤动画
        if (hurtAnimationSelf && this instanceof Player player) {
            EntityEventPacket pk = new EntityEventPacket();
            pk.eid = this.getId();
            pk.event = EntityEventPacket.HURT_ANIMATION;
            // 这边只发给自己，因为广播给他人的已经在onHurt中发送了
            player.dataPacket(pk);
        }

        if (notSuicide && source instanceof EntityDamageByEntityEvent ev) {
            Entity damager = ev.getDamager();
            /*if (source instanceof EntityDamageByChildEntityEvent event) {
                damager = event.getChild();
            }*/
            damager.onAttackSuccess(ev);

            // 击退
            if ((knockback || time >= nextAllowKnockback) && ev.hasKnockBack()) {
                nextAllowKnockback = nextAllowAttack;
                double deltaX = this.x - damager.x;
                double deltaZ = this.z - damager.z;
                KnockbackProfile hitProfile = ev.getKnockbackProfile();
                this.knockBack(damager, damage, deltaX, deltaZ, hitProfile);

                // 攻击者后处理：疾跑相关（默认关闭，Profile 可开启）
                if (damager instanceof Player attacker && attacker.isSprinting()) {
                    if (hitProfile.getSprintSlowdownH() != 1.0f) {
                        attacker.motionX *= hitProfile.getSprintSlowdownH();
                        attacker.motionZ *= hitProfile.getSprintSlowdownH();
                    }
                    if (hitProfile.isStopSprinting()) {
                        attacker.setSprinting(false);
                    }
                }
            }
        }

        return true;
    }

    /**
     * 属于EntityLiving的处理伤害逻辑
     * 在此处应用护甲和附魔、药水等计算
     * @param source 伤害来源（事件）
     * @return 是否成功造成伤害
     */
    protected boolean damageEntity0(EntityDamageEvent source) {
        source.call();
        if (source.isCancelled()) {
            return false;
        }

        if (source.getCause() != DamageCause.SUICIDE) {
            // Make fire aspect to set the target in fire before dealing any damage so the target is in fire on death even if killed by the first hit
            if (source instanceof EntityDamageByEntityEvent damageByEntityEvent) {
                Enchantment[] enchantments = damageByEntityEvent.getWeaponEnchantments();
                if (enchantments != null) {
                    for (Enchantment enchantment : enchantments) {
                        enchantment.doAttack(damageByEntityEvent.getDamager(), this);
                    }
                }
            }

            if (this.absorption > 0) {  // Damage Absorption
                this.setAbsorption(Math.max(0, this.getAbsorption() + source.getDamage(EntityDamageEvent.DamageModifier.ABSORPTION)));
            }
        }

        setLastDamageCause(source);

        float newHealth = getHealth() - source.getFinalDamage();
        if (newHealth >= 1 || !checkTotemDeathProtection(source)) {
            setHealth(newHealth);
        }
        return true;
    }

    protected void onHurt(EntityDamageEvent source) {
        EntityEventPacket pk = new EntityEventPacket();
        pk.eid = this.getId();
        pk.event = this.getHealth() < 1 ? EntityEventPacket.DEATH_ANIMATION : EntityEventPacket.HURT_ANIMATION;
        Server.broadcastPacket(this.hasSpawned.values(), pk);

        doMagicCriticalHit(source);
    }

    protected void doMagicCriticalHit(EntityDamageEvent source) {
        if (source.getCause() != DamageCause.ENTITY_ATTACK) {
            return;
        }
        if (!(source instanceof EntityDamageByEntityEvent event)) {
            return;
        }

        Enchantment[] enchantments = event.getWeaponEnchantments();
        if (enchantments == null) {
            return;
        }

        boolean success = false;
        ENCHANTMENTS:
        for (Enchantment enchantment : enchantments) {
            switch (enchantment.getId()) {
                case Enchantment.SHARPNESS:
                    success = true;
                    break ENCHANTMENTS;
                case Enchantment.SMITE:
                    if (this instanceof EntitySmite) {
                        success = true;
                        break ENCHANTMENTS;
                    }
                    break;
                case Enchantment.BANE_OF_ARTHROPODS:
                    if (this instanceof EntityArthropod) {
                        success = true;
                        break ENCHANTMENTS;
                    }
                    break;
            }
        }
        if (!success) {
            return;
        }

        AnimatePacket packet = new AnimatePacket();
        packet.eid = getId();
        packet.action = AnimatePacket.Action.MAGIC_CRITICAL_HIT;
        packet.data = 15;
        Server.broadcastPacket(getViewers().values(), packet);
    }

    public void knockBack(Entity attacker, double damage, double x, double z) {
        this.knockBack(attacker, damage, x, z, 0.4);
    }

    public void knockBack(Entity attacker, double damage, double x, double z, double base) {
        this.knockBack(attacker, damage, x, z, base, base);
    }

    public void knockBack(Entity attacker, double damage, double x, double z, double baseH, double baseV) {
        knockBackInternal(attacker, damage, x, z, baseH, baseV, getKnockbackProfile());
    }

    /**
     * Profile 驱动的击退方法，attack() 使用此版本
     */
    public void knockBack(Entity attacker, double damage, double x, double z, KnockbackProfile profile) {
        double baseH = profile.getEffectiveBaseH();
        double baseV = profile.getEffectiveBaseV();
        knockBackInternal(attacker, damage, x, z, baseH, baseV, profile);
    }

    private void knockBackInternal(Entity attacker, double damage, double x, double z,
                                    double baseH, double baseV, KnockbackProfile profile) {
        float resistance = getKnockbackResistance();
        if (resistance >= 1) {
            return;
        }
        float scale = 1 - resistance;
        baseH *= scale;
        baseV *= scale;

        double f = Math.sqrt(x * x + z * z);
        if (f <= 0) {
            return;
        }
        f = 1 / f;

        // 旧动量来源
        double oldMotX, oldMotY, oldMotZ;
        if (profile.isUseRealVelocity() && this instanceof Player player && player.speed != null) {
            oldMotX = -player.speed.x;
            oldMotY = -player.speed.y;
            oldMotZ = -player.speed.z;
        } else {
            oldMotX = this.motionX;
            oldMotY = this.motionY;
            oldMotZ = this.motionZ;
        }

        // 旧动量衰减 + 继承比例
        double motX = profile.isInheritHorizontal() ? oldMotX * profile.getFriction() * profile.getInheritRatioH() : 0;
        double motY = profile.isInheritVertical() ? oldMotY * profile.getFriction() * profile.getInheritRatioV() : 0;
        double motZ = profile.isInheritHorizontal() ? oldMotZ * profile.getFriction() * profile.getInheritRatioH() : 0;

        // 叠加新击退
        motX += x * f * baseH;
        motY += baseV;
        motZ += z * f * baseH;

        // 疾跑加成（默认倍率 1.0，无效果）
        if (attacker instanceof Player p && p.isSprinting()) {
            motX *= profile.getSprintMultiplierH();
            motZ *= profile.getSprintMultiplierH();
            motY *= profile.getSprintMultiplierV();
        }

        // 地面加成（默认倍率 1.0，无效果）
        if (this.onGround) {
            motX *= profile.getGroundMultiplierH();
            motZ *= profile.getGroundMultiplierH();
            motY *= profile.getGroundMultiplierV();
        }

        // 垂直上限
        float vLimit = profile.getVerticalLimit() >= 0 ? profile.getVerticalLimit() : (float) baseV;
        if (motY > vLimit) {
            motY = vLimit;
        }

        // 水平方向修正
        if (baseH > 0 && profile.isDirectionCorrection()) {
            double length = Math.sqrt(motX * motX + motZ * motZ);
            length = Math.min(Math.max(length, profile.getHorizontalMin()), profile.getHorizontalMax());
            Vector2 dir = new Vector2(x, z).normalize().multiply(length);
            motX = dir.x;
            motZ = dir.y;
        }

        this.setMotion(new Vector3(motX, motY, motZ));
    }

    protected float getKnockbackResistance() {
        return 0;
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
            for (Item item : ev.getDrops()) {
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
        boolean hasUpdate = super.entityBaseTick(tickDiff);

        if (this.isAlive() && this.needLivingBaseTick) {
            boolean breathing = canBreathe();
            breathing = checkTurtleHelmet(breathing);
            this.setDataFlag(DATA_FLAG_BREATHING, breathing);

            if (this.isInsideOfSolid()) {
                hasUpdate = true;
                this.attack(new EntityDamageEvent(this, DamageCause.SUFFOCATION, 1));
            }

            if (this.isOnLadder() || this.hasEffect(Effect.SLOW_FALLING) || this.hasEffect(Effect.LEVITATION)) {
                this.resetFallDistance();
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
                ArmorInventory inventory = ((EntityHumanType) this).getArmorInventory();
                boolean leatherBoots = inventory != null && inventory.getBoots().getId() == ItemID.LEATHER_BOOTS;
                setDataFlag(DATA_FLAG_IN_SCAFFOLDING, inScaffolding);
                setDataFlag(DATA_FLAG_IN_ASCENDABLE_BLOCK, inScaffolding || leatherBoots && inPowderSnow);
                setDataFlag(DATA_FLAG_OVER_SCAFFOLDING, overScaffolding);
                setDataFlag(DATA_FLAG_OVER_DESCENDABLE_BLOCK, leatherBoots && overPowderSnow || overScaffolding && !level.getBlock(x, y - 2, z).isAir());
                if (inScaffolding || overScaffolding || inPowderSnow || overPowderSnow) {
                    resetFallDistance();
                }
            }

            if (!(this instanceof EntitySmite) && !this.hasEffect(Effect.WATER_BREATHING) && !breathing) {
                if (this instanceof EntityWaterAnimal) {
                    this.setAirTicks(300);
                } else if (turtleTicks == 0 || turtleTicks == 200) {
                    hasUpdate = true;

                    boolean immune = false;
                    if (hasEffect(Effect.BREATH_OF_THE_NAUTILUS)) {
                        immune = true;
                    } else if (this instanceof Player player) {
                        ArmorInventory inventory = player.getArmorInventory();
                        int respiration = inventory.getHelmet().getEnchantmentLevel(Enchantment.RESPIRATION);
                        if (respiration > 0 && ThreadLocalRandom.current().nextInt(respiration + 1) > 0) {
                            immune = true;
                        }
                    }

                    int oldAirTicks = getAirTicks();
                    int airTicks = immune ? oldAirTicks : oldAirTicks - tickDiff;

                    if (airTicks <= -20) {
                        airTicks = 0;
                        if (!immune && (!isPlayer || level.gameRules.getBoolean(GameRule.DROWNING_DAMAGE))) {
                            this.attack(new EntityDamageEvent(this, DamageCause.DROWNING, 2));
                        }
                    }

                    if (oldAirTicks != airTicks) {
                        setAirTicks(airTicks);
                    }
                }
            } else {
                if (this instanceof EntityWaterAnimal) {
                    hasUpdate = true;
                    int oldAirTicks = getAirTicks();
                    int airTicks = oldAirTicks - tickDiff;

                    if (airTicks <= -20) {
                        airTicks = 0;
                        if (!isPlayer || level.gameRules.getBoolean(GameRule.DROWNING_DAMAGE)) {
                            this.attack(new EntityDamageEvent(this, DamageCause.DEHYDRATION, 2));
                        }
                    }

                    if (oldAirTicks != airTicks) {
                        setAirTicks(airTicks);
                    }
                } else {
                    hasUpdate = true;
                    int airTicks = getAirTicks();
                    int maxAir = this.getDataPropertyShort(DATA_MAX_AIR);

                    if (airTicks != maxAir) {
                        airTicks += tickDiff * 4;

                        if (airTicks > maxAir) {
                            airTicks = maxAir;
                        }

                        setAirTicks(airTicks);
                    }
                }
            }

            if (freezing) {
                freezing = false;
                if (frozenTicks < 140) {
                    frozenTicks = Math.min(frozenTicks + tickDiff, 140);
                    setFreezeEffectStrength(frozenTicks / 140f);
                } else if ((age % 40 == 4 || tickDiff > 40) && (!isPlayer || level.gameRules.getBoolean(GameRule.FREEZE_DAMAGE))) {
                    if (attack(new EntityDamageEvent(this, DamageCause.FREEZE, 1))) {
                        level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_PLAYER_HURT_FREEZE);
                    }
                }
            } else if (frozenTicks > 0) {
                frozenTicks = Math.max(frozenTicks - tickDiff * 2, 0);
                setFreezeEffectStrength(frozenTicks / 140f);
            }

            int floorY = Mth.floor(y - (4 / 16.0));
            if (floorY != getFloorY()) {
                Block block = this.level.getBlock(getFloorX(), floorY, getFloorZ());
                if (block.getId() == Block.MAGMA || block.getId() == Block.CACTUS) {
                    block.onEntityCollide(this);
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

        return hasUpdate;
    }

    protected boolean checkTurtleHelmet(boolean breathing) {
        return breathing;
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

    public int getCurrentSwingDuration() {
        int amplifier = 0;
        Effect digSpeed = getEffect(Effect.HASTE);
        if (digSpeed != null) {
            amplifier = digSpeed.getAmplifier();
        }
        Effect conduitPower = getEffect(Effect.CONDUIT_POWER);
        if (conduitPower != null) {
            amplifier = Math.max(amplifier, conduitPower.getAmplifier() + 1);
        }
        if (amplifier > 0) {
            return 6 - (amplifier + 1);
        }

        Effect digSlowdown = getEffect(Effect.MINING_FATIGUE);
        if (digSlowdown == null) {
            return 6;
        }
        return 2 * (digSlowdown.getAmplifier() + 1) + 6;
    }

    @Override
    public void blockedByShield(Entity blocker) {
        knockBack(blocker, 0, x - blocker.x, z - blocker.z);
    }

    protected void setFreezeEffectStrength(float freezeEffectStrength) {
        setDataProperty(new FloatEntityData(DATA_FREEZING_EFFECT_STRENGTH, freezeEffectStrength));
    }

    @Override
    public void resetFrozenState() {
        super.resetFrozenState();
        setFreezeEffectStrength(0);
    }

    public boolean isSleeping() {
        return false;
    }

    public int getBaseArmorValue() {
        return 0;
    }

    public boolean checkTotemDeathProtection(EntityDamageEvent source) {
        switch (source.getCause()) {
            case SUICIDE:
            case VOID:
                return false;
        }

        ObjectIntPair<Inventory> itemStack = getEquippedTotem();
        if (itemStack == null) {
            return false;
        }

        EntityResurrectEvent event = new EntityResurrectEvent(this);
        event.call();
        if (event.isCancelled()) {
            return false;
        }

        resetFallDistance();
        extinguish();
        removeAllEffects();
        setHealth(1);

        addEffect(Effect.getEffect(Effect.REGENERATION).setDuration(45 * 20).setAmplifier(1),
                Effect.getEffect(Effect.ABSORPTION).setDuration(5 * 20).setAmplifier(1),
                Effect.getEffect(Effect.FIRE_RESISTANCE).setDuration(40 * 20));

        broadcastEntityEvent(EntityEventPacket.CONSUME_TOTEM);
        level.addLevelEvent(this, LevelEventPacket.EVENT_SOUND_TOTEM);

        itemStack.left().clear(itemStack.rightInt());
        return true;
    }

    @Nullable
    protected ObjectIntPair<Inventory> getEquippedTotem() {
        return null;
    }

    public long getNextAllowAttack() {
        return nextAllowAttack;
    }

    public EntityLiving setNextAllowAttack(long nextAllowAttack) {
        this.nextAllowAttack = nextAllowAttack;
        return this;
    }
}
