package cn.nukkit.entity.projectile;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.data.LongEntityData;
import cn.nukkit.entity.weather.EntityLightning;
import cn.nukkit.event.entity.EntityDamageByChildEntityEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.ProjectileHitEvent;
import cn.nukkit.event.inventory.InventoryPickupTridentEvent;
import cn.nukkit.event.weather.LightningStrikeEvent;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.item.Items;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.MovingObjectPosition;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.network.protocol.TakeItemEntityPacket;
import cn.nukkit.Server;
import lombok.Setter;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by PetteriM1
 */
public class EntityThrownTrident extends EntityProjectile {

    public static final int NETWORK_ID = EntityID.THROWN_TRIDENT;
    public static final int FAVORED_SLOT_OFFHAND = -2;

    protected int favoredSlot;
    protected Item trident;

    protected int pickupMode;

    private boolean dealtDamage;
    private boolean returning;
    private int returnDelayRemaining;
    /** 玩法额外等待发生在起飞前，接住时间仍由实际返航决定。 */
    @Setter
    private int loyaltyReturnDelayTicks;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.25f;
    }

    @Override
    public float getHeight() {
        return 0.35f;
    }

    @Override
    public float getGravity() {
        return 0.05f;
    }

    @Override
    public float getDrag() {
        return 0.01f;
    }

    public EntityThrownTrident(FullChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, null);
    }

    public EntityThrownTrident(FullChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        super(chunk, nbt, shootingEntity);
        if (shootingEntity != null) {
            this.setDataProperty(new LongEntityData(DATA_OWNER_EID, shootingEntity.getId()));
//            this.setDataProperty(new LongEntityData(DATA_ARROW_SHOOTER_EID, shootingEntity.getId()));
        }
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        this.favoredSlot = namedTag.contains("favoredSlot") ? namedTag.getInt("favoredSlot") : -1;
        this.trident = namedTag.contains("Trident") ? NBTIO.getItemHelper(namedTag.getCompound("Trident")) : Items.air();
        this.pickupMode = namedTag.contains("pickup") ? namedTag.getByte("pickup") : PICKUP_ANY;
        dealtDamage = namedTag.getBoolean("DealtDamage");
        returning = namedTag.getBoolean("Returning");
        returnDelayRemaining = namedTag.contains("ReturnDelay") ? namedTag.getInt("ReturnDelay")
            : (dealtDamage ? 0 : (stuckToBlockPos != null ? 5 : -1));
        setDataFlag(DATA_FLAG_RETURN_TRIDENT, returning, false);

        if (trident != null && trident.hasEnchantments()) {
            setDataFlag(DATA_FLAG_ENCHANTED, true, false);
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        this.namedTag.putInt("favoredSlot", this.favoredSlot);
        this.namedTag.put("Trident", NBTIO.putItemHelper(this.trident));
        this.namedTag.putByte("pickup", this.pickupMode);
        namedTag.putBoolean("DealtDamage", dealtDamage);
        namedTag.putBoolean("Returning", returning);
        namedTag.putInt("ReturnDelay", returnDelayRemaining);
    }

    public int getFavoredSlot() {
        return favoredSlot;
    }

    public void setFavoredSlot(int slot) {
        favoredSlot = slot;
    }

    public Item getItem() {
        return this.trident != null ? this.trident.clone() : Items.air();
    }

    public boolean hasLoyalty() {
        return trident != null && trident.hasEnchantment(Enchantment.LOYALTY);
    }

    public void setItem(Item item) {
        this.trident = item != null ? item.clone() : null;

        if (this.trident != null) {
            setDataFlag(DATA_FLAG_ENCHANTED, trident.hasEnchantments());
        }
    }

    @Override
    public int getResultDamage() {
        return 8;
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
        if (dealtDamage || returning) {
            return false;
        }

        finishEntityHit();
        this.server.getPluginManager().callEvent(new ProjectileHitEvent(this, MovingObjectPosition.fromEntity(entity)));
        float damage = this.getResultDamage();

        if (trident != null) {
            int impaling = trident.getEnchantmentLevel(Enchantment.IMPALING);
            if (impaling > 0 && (entity.isInsideOfWater() || level.isRaining() && level.canBlockSeeSky(this))) {
                damage += impaling * 2.5f;
            }
        }

        EntityDamageEvent ev;
        if (this.shootingEntity == null) {
            ev = new EntityDamageByEntityEvent(this, entity, DamageCause.PROJECTILE, damage);
        } else {
            ev = new EntityDamageByChildEntityEvent(this.shootingEntity, this, entity, DamageCause.PROJECTILE, damage);
        }
        entity.attack(ev);

        if (trident != null && level.isThundering() && trident.hasEnchantment(Enchantment.CHANNELING) && level.canBlockSeeSky(this)) {
            EntityLightning bolt = new EntityLightning(this.getChunk(), getDefaultNBT(this));
            LightningStrikeEvent strikeEvent = new LightningStrikeEvent(level, bolt);
            server.getPluginManager().callEvent(strikeEvent);
            if (!strikeEvent.isCancelled()) {
                bolt.spawnToAll();
                level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_ITEM_TRIDENT_THUNDER);
            } else {
                bolt.setEffect(false);
            }
        }

        return true;
    }

    /** 命中后保留同一实体，结束伤害并在下一次更新开始忠诚返航。 */
    protected final void finishEntityHit() {
        dealtDamage = true;
        hadCollision = true;
        returnDelayRemaining = Math.max(0, loyaltyReturnDelayTicks);
        motionX *= -0.01;
        motionY *= -0.1;
        motionZ *= -0.01;
        level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_ITEM_TRIDENT_HIT);
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return !dealtDamage && !returning && super.canCollideWith(entity);
    }

    @Override
    protected void onHitBlock(MovingObjectPosition blockHitResult) {
        super.onHitBlock(blockHitResult);
        returnDelayRemaining = 5 + Math.max(0, loyaltyReturnDelayTicks);
        level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_ITEM_TRIDENT_HIT_GROUND);
    }

    public int getPickupMode() {
        return this.pickupMode;
    }

    public void setPickupMode(int pickupMode) {
        this.pickupMode = pickupMode;
    }

    @Override
    protected boolean shouldStickInGround() {
        return true;
    }

    @Override
    protected boolean shouldBounce() {
        return true;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (closed) {
            return false;
        }
        if (currentTick <= lastUpdate && !justCreated) {
            return true;
        }
        if (!usesLoyaltyReturn() || !hasLoyalty() || shootingEntity == null) {
            return super.onUpdate(currentTick);
        }
        if (!isReturnOwnerValid()) {
            onReturnOwnerLost();
            return false;
        }
        // 忠诚不会跨维度追踪；主人回到本维度后再继续。
        if (shootingEntity.level != level) {
            lastUpdate = currentTick;
            return true;
        }
        if (!returning && (y <= level.getHeightRange().getMinY() - 15
                || returnDelayRemaining >= 0 && (returnDelayRemaining == 0 || --returnDelayRemaining == 0))) {
            returning = true;
            dealtDamage = true;
            stuckToBlockPos = null;
            onGround = false;
            isCollided = false;
            hadCollision = false;
            setDataFlag(DATA_FLAG_RETURN_TRIDENT, true);
            level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_ITEM_TRIDENT_RETURN);
        }
        if (!returning) {
            return super.onUpdate(currentTick);
        }

        int tickDiff = Math.max(1, currentTick - lastUpdate);
        lastUpdate = currentTick;
        entityBaseTick(tickDiff);
        if (closed) {
            return false;
        }
        tickLoyaltyReturn();
        return !closed;
    }

    /** 特殊玩法可明确使用自身的回收流程。 */
    protected boolean usesLoyaltyReturn() {
        return true;
    }

    protected boolean isReturnOwnerValid() {
        return shootingEntity != null && !shootingEntity.isClosed() && shootingEntity.isAlive()
            && (!(shootingEntity instanceof Player player) || player.isOnline() && !player.isSpectator());
    }

    protected void onReturnOwnerLost() {
        if (pickupMode == PICKUP_ANY && trident != null && !trident.isNull()) {
            level.dropItem(this, trident);
        }
        close();
    }

    /** 返回阶段不参与方块交互或虚空伤害，避免穿墙及虚空返航途中被销毁。 */
    @Override
    protected void checkBlockCollision() {
        if (!returning) {
            super.checkBlockCollision();
        }
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        return !returning && super.attack(source);
    }

    private void tickLoyaltyReturn() {
        int loyalty = trident.getEnchantmentLevel(Enchantment.LOYALTY);
        Vector3 previous = copyVec();
        Vector3 direction = shootingEntity.getEyePosition().subtract(this);
        // 与原版保持相同顺序：高度修正、惯性与加速、位移、空气阻力。
        Vector3 velocity = getMotion().multiply(0.95).add(direction.normalize().multiply(0.05 * loyalty));
        Vector3 next = add(velocity).add(0, direction.y * 0.015 * loyalty, 0);
        setPosition(next);
        motionX = velocity.x * 0.99;
        motionY = velocity.y * 0.99;
        motionZ = velocity.z * 0.99;
        updateRotation();
        yaw = (yaw + 180) % 360;
        updateMovement();

        AxisAlignedBB pickupBox = shootingEntity.getBoundingBox().grow(0.5, 0.5, 0.5);
        if (pickupBox.isVectorInside(previous) || pickupBox.isVectorInside(next)
                || pickupBox.calculateIntercept(previous, next) != null) {
            tryReturnToOwner();
        }
    }

    /** 入包成功才删除实体；取消拾取或背包已满时继续围绕主人飞行。 */
    protected final void tryReturnToOwner() {
        if (closed || !isReturnOwnerValid() || !(shootingEntity instanceof InventoryHolder holder)
                || pickupMode == PICKUP_NONE) {
            return;
        }
        Inventory inventory = holder.getInventory();
        if (favoredSlot == FAVORED_SLOT_OFFHAND && shootingEntity instanceof Player player
                && player.getOffhandInventory().getItem().isNull()) {
            inventory = player.getOffhandInventory();
        }
        InventoryPickupTridentEvent event = new InventoryPickupTridentEvent(inventory, this);
        server.getPluginManager().callEvent(event);
        if (event.isCancelled() || closed || !isReturnOwnerValid()) {
            return;
        }
        if (pickupMode != PICKUP_CREATIVE && !receiveReturnedTrident(inventory)) {
            return;
        }
        TakeItemEntityPacket packet = new TakeItemEntityPacket();
        packet.entityId = shootingEntity.getId();
        packet.target = getId();
        Server.broadcastPacket(getViewers().values(), packet);
        if (shootingEntity instanceof Player player) {
            player.dataPacket(packet);
        }
        level.addLevelEvent(shootingEntity, LevelEventPacket.EVENT_SOUND_INFINITY_ARROW_PICKUP,
            (int) ((ThreadLocalRandom.current().nextGaussian() * 0.7 + 1) * 2 * 1000));
        close();
    }

    protected boolean receiveReturnedTrident(Inventory inventory) {
        if (favoredSlot == FAVORED_SLOT_OFFHAND && shootingEntity instanceof Player player
                && inventory == player.getOffhandInventory()) {
            return inventory.setItem(0, trident);
        }
        if (favoredSlot >= 0 && favoredSlot < inventory.getSize() && inventory.getItem(favoredSlot).isNull()) {
            return inventory.setItem(favoredSlot, trident);
        }
        return inventory.canAddItem(trident) && inventory.addItem(trident).length == 0;
    }

    @Override
    protected float getLiquidInertia() {
        return 0.99f;
    }
}
