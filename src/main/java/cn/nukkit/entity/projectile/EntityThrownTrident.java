package cn.nukkit.entity.projectile;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.weather.EntityLightning;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.EntityDamageByChildEntityEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.ProjectileHitEvent;
import cn.nukkit.event.weather.LightningStrikeEvent;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.MovingObjectPosition;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

/**
 * Created by PetteriM1
 */
public class EntityThrownTrident extends EntityProjectile {

    public static final int NETWORK_ID = EntityID.THROWN_TRIDENT;

    protected Item trident;

    protected int pickupMode;
    public boolean alreadyCollided;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.05f;
    }

    @Override
    public float getLength() {
        return 0.5f;
    }

    @Override
    public float getHeight() {
        return 0.05f;
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
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        this.trident = namedTag.contains("Trident") ? NBTIO.getItemHelper(namedTag.getCompound("Trident")) : Item.get(Item.AIR);
        this.pickupMode = namedTag.contains("pickup") ? namedTag.getByte("pickup") : PICKUP_ANY;
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        this.namedTag.put("Trident", NBTIO.putItemHelper(this.trident));
        this.namedTag.putByte("pickup", this.pickupMode);
    }

    public Item getItem() {
        return this.trident != null ? this.trident.clone() : Item.get(Item.AIR);
    }

    public void setItem(Item item) {
        this.trident = item.clone();
    }

    @Override
    protected double getBaseDamage() {
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
    public void onCollideWithEntity(Entity entity) {
        if (this.alreadyCollided) {
            this.move(this.motionX, this.motionY, this.motionZ);
            return;
        }

        this.server.getPluginManager().callEvent(new ProjectileHitEvent(this, MovingObjectPosition.fromEntity(entity)));
        float damage = this.getResultDamage();

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
        newTrident.setItem(this.trident);
        newTrident.spawnToAll();
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
}
