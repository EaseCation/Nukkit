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
import cn.nukkit.event.weather.LightningStrikeEvent;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.item.Items;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.MovingObjectPosition;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import lombok.Setter;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by PetteriM1
 */
public class EntityThrownTrident extends EntityProjectile {

    public static final int NETWORK_ID = EntityID.THROWN_TRIDENT;

    protected int favoredSlot;
    protected Item trident;

    protected int pickupMode;

    public boolean alreadyCollided;
    @Setter
    protected int loyaltyBackTick = 0;
    protected int returnTridentTickCount;

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

            if (this.shootingEntity == null) {
                return;
            }
            int loyaltyEnchant = this.trident.getEnchantmentLevel(Enchantment.LOYALTY);
            if (loyaltyEnchant > 0) {
                // TODO different ticks for different levels
                this.loyaltyBackTick = 20;
            }
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
        if (this.alreadyCollided) {
            this.move(this.motionX, this.motionY, this.motionZ);
            return true;
        }

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
        this.hadCollision = true;
        this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_ITEM_TRIDENT_HIT);
        this.close();

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

        EntityThrownTrident newTrident = new EntityThrownTrident(getChunk(), getDefaultNBT(this), this.shootingEntity);
        newTrident.alreadyCollided = true;
        newTrident.pickupMode = this.pickupMode;
        newTrident.shootingEntity = this.shootingEntity;
        newTrident.setItem(this.trident);
        newTrident.setFavoredSlot(this.favoredSlot);
        newTrident.spawnToAll();
        return true;
    }

    @Override
    protected void onHitBlock(MovingObjectPosition blockHitResult) {
        super.onHitBlock(blockHitResult);

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
        boolean update = super.onUpdate(currentTick);
        if (this.loyaltyBackTick > 0) {
            if (this.isCollided || this.hadCollision || this.alreadyCollided || this.getY() <= level.getHeightRange().getMinY()) {
                if (returnTridentTickCount++ == 0) {
                    if (shootingEntity != null) {
                        shootingEntity.level.addLevelSoundEvent(shootingEntity, LevelSoundEventPacket.SOUND_ITEM_TRIDENT_RETURN);
                    }
                    setDataFlag(DATA_FLAG_RETURN_TRIDENT, true);
                }

                //TODO: ThrownTrident::returnWithLoyalty animation

                if (--this.loyaltyBackTick <= 0 || this.getY() <= level.getHeightRange().getMinY()) {
                    if (this.shootingEntity instanceof InventoryHolder) {
                        this.close();

                        if (pickupMode == PICKUP_ANY) {
                            Inventory inventory = ((InventoryHolder) this.shootingEntity).getInventory();
                            if (favoredSlot != -1) {
                                Item item = inventory.getItem(favoredSlot);
                                if (item.isNull()) {
                                    inventory.setItem(favoredSlot, trident);
                                } else {
                                    inventory.addItem(trident);
                                }
                            } else {
                                inventory.addItem(this.trident);
                            }
                        }

                        this.shootingEntity.getLevel().addLevelEvent(this.shootingEntity, LevelEventPacket.EVENT_SOUND_INFINITY_ARROW_PICKUP, (int) ((ThreadLocalRandom.current().nextGaussian() * 0.7 + 1) * 2 * 1000));
                    }
                }
            }
            return true;
        }
        return update;
    }

    @Override
    protected float getLiquidInertia() {
        return 0.99f;
    }
}
