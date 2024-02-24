package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.EntitySmite;
import cn.nukkit.entity.data.FloatEntityData;
import cn.nukkit.entity.data.IntEntityData;
import cn.nukkit.entity.data.LongEntityData;
import cn.nukkit.entity.data.ShortEntityData;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.EntityRegainHealthEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.math.Mth;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.potion.Effect;
import cn.nukkit.potion.Potion;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;

import javax.annotation.Nullable;

public class EntityAreaEffectCloud extends Entity {
    public static final int NETWORK_ID = EntityID.AREA_EFFECT_CLOUD;

    private int duration;
    private int durationOnUse;
    private int reapplicationDelay;
    private float initialRadius;
    private float radiusOnUse;
    private float radiusPerTick;
    private float radiusChangeOnPickup;
    private int pickupCount;
    private Int2ObjectMap<Effect> mobEffects;
    private int particleColor;
    private int particleId;
    private int potionId;
    private long spawnTick;
    private long ownerId;

    private boolean affectOwner = true;
    @Nullable
    private Entity owner;
    private final Long2LongMap victims = new Long2LongOpenHashMap();

    public EntityAreaEffectCloud(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 1.8f;
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        if (namedTag.contains("Duration")) {
            duration = namedTag.getInt("Duration");
        } else {
            duration = 600;
        }
        dataProperties.putInt(DATA_AREA_EFFECT_CLOUD_DURATION, duration);

        durationOnUse = namedTag.getInt("DurationOnUse");

        if (namedTag.contains("ReapplicationDelay")) {
            reapplicationDelay = namedTag.getInt("ReapplicationDelay");
        } else {
            reapplicationDelay = 40;
        }

        if (namedTag.contains("InitialRadius")) {
            initialRadius = namedTag.getFloat("InitialRadius");
        } else if (namedTag.contains("Radius")) {
            initialRadius = namedTag.getFloat("Radius");
        } else {
            initialRadius = 3;
        }
        dataProperties.putFloat(DATA_AREA_EFFECT_CLOUD_RADIUS, initialRadius);

        if (namedTag.contains("RadiusOnUse")) {
            radiusOnUse = namedTag.getFloat("RadiusOnUse");
        } else {
            radiusOnUse = -0.5f;
        }

        if (namedTag.contains("RadiusPerTick")) {
            radiusPerTick = namedTag.getFloat("RadiusPerTick");
        } else {
            radiusPerTick = -initialRadius / duration;
        }
        dataProperties.putFloat(DATA_AREA_EFFECT_CLOUD_CHANGE_RATE, radiusPerTick);

        if (namedTag.contains("RadiusChangeOnPickup")) {
            radiusChangeOnPickup = namedTag.getInt("RadiusChangeOnPickup");
        } else {
            radiusChangeOnPickup = -0.5f;
        }
        dataProperties.putFloat(DATA_AREA_EFFECT_CLOUD_CHANGE_ON_PICKUP, radiusChangeOnPickup);

        pickupCount = namedTag.getInt("PickupCount");
        dataProperties.putInt(DATA_AREA_EFFECT_CLOUD_PICKUP_COUNT, pickupCount);

        potionId = namedTag.getShort("PotionId");
        dataProperties.putShort(DATA_AUX_VALUE_DATA, potionId);

        ListTag<CompoundTag> mobEffects = namedTag.getList("mobEffects", CompoundTag.class);
        this.mobEffects = new Int2ObjectOpenHashMap<>(mobEffects.size());
        for (CompoundTag tag : mobEffects.getAll()) {
            Effect effect = Effect.load(tag);
            if (effect == null) {
                continue;
            }
            this.mobEffects.put(effect.getId(), effect);
        }

        if (namedTag.contains("ParticleColor")) {
            particleColor = namedTag.getInt("ParticleColor");
        } else {
            particleColor = Effect.calculateColor(this.mobEffects.values().toArray(new Effect[0]));
        }
        dataProperties.putInt(DATA_POTION_COLOR, particleColor);

        particleId = namedTag.getInt("ParticleId");
        if (particleId == 0) {
            particleId = Particle.MOB_SPELL_AMBIENT;
        }
        dataProperties.putInt(DATA_AREA_EFFECT_CLOUD_PARTICLE_ID, particleId);

        spawnTick = namedTag.getLong("SpawnTick");
        dataProperties.putLong(DATA_AREA_EFFECT_CLOUD_SPAWN_TIME, spawnTick);

        ownerId = namedTag.getLong("OwnerId");

        setDataFlag(DATA_FLAG_HAS_COLLISION, false, false);
        setDataFlag(DATA_FLAG_FIRE_IMMUNE, true, false);

        fireProof = true;
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        namedTag.putInt("Duration", duration);
        namedTag.putInt("DurationOnUse", durationOnUse);
        namedTag.putInt("ReapplicationDelay", reapplicationDelay);
        namedTag.putFloat("InitialRadius", initialRadius);
        namedTag.putFloat("RadiusOnUse", radiusOnUse);
        namedTag.putFloat("RadiusPerTick", radiusPerTick);
        namedTag.putFloat("RadiusChangeOnPickup", radiusChangeOnPickup);
        namedTag.putInt("PickupCount", pickupCount);
        namedTag.putShort("PotionId", potionId);
        namedTag.putInt("ParticleColor", particleColor);
        namedTag.putLong("SpawnTick", spawnTick);
        namedTag.putLong("OwnerId", ownerId);

//        AxisAlignedBB aabb = getBoundingBox();
//        namedTag.putFloat("Radius", (float) Math.max(Math.abs(aabb.getMaxX() - aabb.getMinX()), Math.abs(aabb.getMaxY() - aabb.getMinY())) * 0.5f);
        namedTag.putFloat("Radius", 0.9f);

        ListTag<CompoundTag> list = new ListTag<>("mobEffects");
        for (Effect effect : this.mobEffects.values()) {
            list.add(effect.save());
        }
        namedTag.putList(list);

        if (particleId != Particle.MOB_SPELL_AMBIENT) {
            namedTag.putInt("ParticleId", particleId);
        } else {
            namedTag.remove("ParticleId");
        }
    }

    @Override
    public void spawnTo(Player player) {
        if (getViewers().containsKey(player.getLoaderId())) {
            return;
        }

        player.dataPacket(createAddEntityPacket());

        super.spawnTo(player);
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (isClosed()) {
            return false;
        }

        int tickDiff = currentTick - lastUpdate;
        if (tickDiff <= 0 && !justCreated) {
            return true;
        }
        lastUpdate = currentTick;

        if (spawnTick == 0) {
            setSpawnTick(level.getCurrentTick());
            return true;
        }

        long tickCount = getTicksAlive();
        if (tickCount >= duration) {
            close();
            return false;
        }

        float radius = Math.max(0, getEffectiveRadius());
        if (radius < 0.5f) {
            close();
            return false;
        }

        if (tickCount % 5 != 0) {
            return true;
        }

        if (mobEffects.isEmpty()) {
            victims.clear();
            return true;
        }

        victims.long2LongEntrySet().removeIf(entry -> tickCount >= entry.getLongValue());

        long nextTick = tickCount + reapplicationDelay;
        float radiusSquare = Mth.square(radius);

        Entity[] entities = level.getNearbyEntities(getBoundingBox().grow(radius, radius * 0.5, radius), this);
        for (Entity entity : entities) {
            if (!(entity instanceof EntityLiving)) {
                continue;
            }

            if (!entity.isAlive()) {
                continue;
            }

            if (entity instanceof Player && ((Player) entity).isSpectator()) {
                continue;
            }

            long id = entity.getId();
            if (!affectOwner && (id == ownerId || entity == owner)) {
                continue;
            }

            if (radiusSquare >= distanceSquared(entity)) {
                continue;
            }

            if (victims.putIfAbsent(id, nextTick) != 0) {
                continue;
            }

//            Potion.getPotion(potionId).applyPotion(entity, 0.25f, 0.5f);
            for (Effect effect : mobEffects.values()) {
                switch (effect.getId()) {
                    case Effect.NO_EFFECT:
                        if (potionId == Potion.WATER && entity.isOnFire()) {
                            level.addLevelSoundEvent(entity.upVec(), LevelSoundEventPacket.SOUND_FIZZ);
                            level.addLevelEvent(entity, LevelEventPacket.EVENT_PARTICLE_FIZZ_EFFECT, 513);
                            entity.extinguish();
                        }
                        break;
                    case Effect.INSTANT_HEALTH:
                        if (entity instanceof EntitySmite) {
                            if (owner != null) {
                                entity.attack(new EntityDamageByEntityEvent(owner, entity, DamageCause.MAGIC, 0.5f * (6 << effect.getAmplifier())));
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
                        } else if (owner != null) {
                            entity.attack(new EntityDamageByEntityEvent(owner, entity, DamageCause.MAGIC, 0.5f * (6 << effect.getAmplifier())));
                        } else {
                            entity.attack(new EntityDamageEvent(entity, DamageCause.MAGIC, 0.5f * (6 << effect.getAmplifier())));
                        }
                        break;
                    case Effect.SATURATION:
                        if (entity instanceof Player player) {
                            int level = 1 + effect.getAmplifier();
                            player.getFoodData().addFoodLevel(level, level * 2);
                        }
                        break;
                    default:
                        entity.addEffect(effect.clone().setDuration(effect.getDuration() / 4));
                        break;
                }
            }

            if (radiusOnUse != 0) {
                radius += radiusOnUse;

                if (radius < 0.5f) {
                    close();
                    return false;
                }

                setInitialRadius(initialRadius + radiusOnUse);
            }

            if (durationOnUse != 0) {
                int newDuration = durationOnUse + duration;

                if (newDuration <= 0) {
                    close();
                    return false;
                }

                setDuration(duration);
            }
        }

        return true;
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if (item.getId() != Item.GLASS_BOTTLE) {
            return false;
        }
        if (particleId != Particle.DRAGON_BREATH) {
            return false;
        }

        Vector3 distanceToCloud = clickedPos.subtract(this);
        distanceToCloud.y = 0;
        if (Mth.square(getEffectiveRadius()) < distanceToCloud.lengthSquared()) {
            return false;
        }

        setPickupCount(pickupCount + 1);

        player.getInventory().addItem(Item.get(Item.DRAGON_BREATH));
        return true;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        return source.getCause() == DamageCause.VOID && super.attack(source);
    }

    @Override
    public boolean canCollide() {
        return false;
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return false;
    }

    @Override
    public boolean canBeMovedByCurrents() {
        return false;
    }

    public float getEffectiveRadius() {
        return initialRadius + getTicksAlive() * radiusPerTick + pickupCount * radiusChangeOnPickup;
    }

    private long getTicksAlive() {
        return Math.max(0, level.getCurrentTick() - spawnTick);
    }

    public void addMobEffect(Effect effect) {
        mobEffects.put(effect.getId(), effect);
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
        setDataProperty(new IntEntityData(DATA_AREA_EFFECT_CLOUD_DURATION, duration));
    }

    public int getDurationOnUse() {
        return durationOnUse;
    }

    public void setDurationOnUse(int durationOnUse) {
        this.durationOnUse = durationOnUse;
    }

    public int getReapplicationDelay() {
        return reapplicationDelay;
    }

    public void setReapplicationDelay(int reapplicationDelay) {
        this.reapplicationDelay = reapplicationDelay;
    }

    public float getInitialRadius() {
        return initialRadius;
    }

    public void setInitialRadius(float initialRadius) {
        this.initialRadius = initialRadius;
        setDataProperty(new FloatEntityData(DATA_AREA_EFFECT_CLOUD_RADIUS, initialRadius));
    }

    public float getRadiusOnUse() {
        return radiusOnUse;
    }

    public void setRadiusOnUse(float radiusOnUse) {
        this.radiusOnUse = radiusOnUse;
    }

    public float getRadiusPerTick() {
        return radiusPerTick;
    }

    public void setRadiusPerTick(float radiusPerTick) {
        this.radiusPerTick = radiusPerTick;
        setDataProperty(new FloatEntityData(DATA_AREA_EFFECT_CLOUD_CHANGE_RATE, radiusPerTick));
    }

    public float getRadiusChangeOnPickup() {
        return radiusChangeOnPickup;
    }

    public void setRadiusChangeOnPickup(float radiusChangeOnPickup) {
        this.radiusChangeOnPickup = radiusChangeOnPickup;
        setDataProperty(new FloatEntityData(DATA_AREA_EFFECT_CLOUD_CHANGE_ON_PICKUP, radiusChangeOnPickup));
    }

    public int getPickupCount() {
        return pickupCount;
    }

    public void setPickupCount(int pickupCount) {
        this.pickupCount = pickupCount;
        setDataProperty(new IntEntityData(DATA_AREA_EFFECT_CLOUD_PICKUP_COUNT, pickupCount));
    }

    public int getParticleColor() {
        return particleColor;
    }

    public void setParticleColor(int particleColor) {
        this.particleColor = particleColor;
        setDataProperty(new IntEntityData(DATA_POTION_COLOR, particleColor));
    }

    public int getParticleId() {
        return particleId;
    }

    public void setParticleId(int particleId) {
        this.particleId = particleId;
        setDataProperty(new IntEntityData(DATA_AREA_EFFECT_CLOUD_PARTICLE_ID, particleId));
    }

    public int getPotionId() {
        return potionId;
    }

    public void setPotionId(int potionId) {
        this.potionId = potionId;
        setDataProperty(new ShortEntityData(DATA_AUX_VALUE_DATA, potionId));
    }

    private void setSpawnTick(long spawnTick) {
        this.spawnTick = spawnTick;
        setDataProperty(new LongEntityData(DATA_AREA_EFFECT_CLOUD_SPAWN_TIME, spawnTick));
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    @Nullable
    public Entity getOwner() {
        return owner;
    }

    public void setOwner(@Nullable Entity owner) {
        this.owner = owner;
//        ownerId = owner == null ? 0 : owner.getId();
    }

    public void setAffectOwner(boolean shouldAffect) {
        this.affectOwner = shouldAffect;
    }
}
