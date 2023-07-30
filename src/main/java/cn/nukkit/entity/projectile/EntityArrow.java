package cn.nukkit.entity.projectile;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.EntitySmite;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.EntityRegainHealthEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.MovingObjectPosition;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.potion.Effect;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.concurrent.ThreadLocalRandom;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EntityArrow extends EntityProjectile {
    public static final int NETWORK_ID = EntityID.ARROW;

    private Int2ObjectMap<Effect> mobEffects;
    private int auxValue;

    protected int pickupMode;

    protected boolean playedHitSound = false;

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
    public float getGravity() {
//        return 0.05f;
        return 0.03f;
    }

    @Override
    public float getDrag() {
        return 0.01f;
    }

    public EntityArrow(FullChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, null);
    }

    public EntityArrow(FullChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        this(chunk, nbt, shootingEntity, false);
    }

    public EntityArrow(FullChunk chunk, CompoundTag nbt, Entity shootingEntity, boolean critical) {
        super(chunk, nbt, shootingEntity);
        this.setCritical(critical);
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        this.damage = namedTag.contains("damage") ? namedTag.getDouble("damage") : 2;
        this.pickupMode = namedTag.contains("pickup") ? namedTag.getByte("pickup") : PICKUP_ANY;

        ListTag<CompoundTag> mobEffects = namedTag.getList("mobEffects", CompoundTag.class);
        this.mobEffects = new Int2ObjectOpenHashMap<>(mobEffects.size());
        for (CompoundTag tag : mobEffects.getAll()) {
            Effect effect = Effect.load(tag);
            if (effect == null) {
                continue;
            }
            this.mobEffects.put(effect.getId(), effect);
        }

        auxValue = namedTag.getByte("auxValue");
        dataProperties.putByte(DATA_ARROW_AUX_VALUE, auxValue);
    }

    public void setCritical() {
        this.setCritical(true);
    }

    public void setCritical(boolean value) {
        this.setDataFlag(DATA_FLAG_CRITICAL, value);
    }

    public boolean isCritical() {
        return this.getDataFlag(DATA_FLAG_CRITICAL);
    }

    @Override
    public int getResultDamage() {
        int base = super.getResultDamage();

        if (this.isCritical()) {
            base += ThreadLocalRandom.current().nextInt(base / 2 + 2);
        }

        return base;
    }

    @Override
    protected double getBaseDamage() {
        return 2;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        this.timing.startTiming();

        boolean hasUpdate = super.onUpdate(currentTick);

        if (this.onGround || this.hadCollision) {
            this.setCritical(false);
            if (!this.playedHitSound) {
                this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_BOW_HIT);
                this.playedHitSound = true;
            }
        }

        if (this.age > 1200) {
            this.close();
            hasUpdate = true;
        }

        this.timing.stopTiming();

        return hasUpdate;
    }

    @Override
    protected void onHitBlock(MovingObjectPosition blockHitResult) {
        super.onHitBlock(blockHitResult);

        EntityEventPacket pk = new EntityEventPacket();
        pk.eid = this.getId();
        pk.event = EntityEventPacket.ARROW_SHAKE;
        pk.data = 7;
        Server.broadcastPacket(this.getViewers().values(), pk);
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
    public void saveNBT() {
        super.saveNBT();

        this.namedTag.putByte("pickup", this.pickupMode);

        ListTag<CompoundTag> list = new ListTag<>("mobEffects");
        for (Effect effect : this.mobEffects.values()) {
            list.add(effect.save());
        }
        namedTag.putList(list);

        namedTag.putByte("auxValue", auxValue);
    }

    public Int2ObjectMap<Effect> getMobEffects() {
        return mobEffects;
    }

    public int getPickupMode() {
        return this.pickupMode;
    }

    public void setPickupMode(int pickupMode) {
        this.pickupMode = pickupMode;
    }

    public Item getItem() {
        return Item.get(Item.ARROW, auxValue);
    }

    @Override
    protected void postHurt(Entity entity) {
        for (Effect effect : mobEffects.values()) {
            switch (effect.getId()) {
                case Effect.NO_EFFECT:
                    break;
                case Effect.INSTANT_HEALTH:
                    if (entity instanceof EntitySmite) {
                        if (shootingEntity != null) {
                            entity.attack(new EntityDamageByEntityEvent(shootingEntity, entity, DamageCause.MAGIC, 0.5f * (6 << effect.getAmplifier())));
                        } else {
                            entity.attack(new EntityDamageEvent(entity, DamageCause.MAGIC, 0.5f * (6 << effect.getAmplifier())));
                        }
                    } else {
                        entity.heal(new EntityRegainHealthEvent(entity, 0.5f * (4 << effect.getAmplifier()), EntityRegainHealthEvent.CAUSE_MAGIC));
                    }
                    break;
                case Effect.INSTANT_DAMAGE:
                    if (entity instanceof EntitySmite) {
                        entity.heal(new EntityRegainHealthEvent(entity, 0.5f * (4 << effect.getAmplifier()), EntityRegainHealthEvent.CAUSE_MAGIC));
                    } else if (shootingEntity != null) {
                        entity.attack(new EntityDamageByEntityEvent(shootingEntity, entity, DamageCause.MAGIC, 0.5f * (6 << effect.getAmplifier())));
                    } else {
                        entity.attack(new EntityDamageEvent(entity, DamageCause.MAGIC, 0.5f * (6 << effect.getAmplifier())));
                    }
                    break;
                default:
                    entity.addEffect(effect.clone().setDuration(effect.getDuration() / 8));
                    break;
            }
        }
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
