package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.*;
import cn.nukkit.blockentity.BlockEntityPistonArm;
import cn.nukkit.entity.attribute.Attribute;
import cn.nukkit.entity.data.*;
import cn.nukkit.entity.property.EntityPropertiesInstance;
import cn.nukkit.entity.property.EntityProperty;
import cn.nukkit.entity.property.EntityPropertyRegistry;
import cn.nukkit.event.Event;
import cn.nukkit.event.entity.*;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.event.entity.EntityPortalEnterEvent.PortalType;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerInteractEvent.Action;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.*;
import cn.nukkit.metadata.MetadataValue;
import cn.nukkit.metadata.Metadatable;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.*;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.ChunkException;
import cn.nukkit.utils.Utils;
import com.google.common.collect.Iterables;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIntPair;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author MagicDroidX
 */
@Log4j2
public abstract class Entity extends Location implements Metadatable, EntityDataType, EntityDataID, EntityFlagID {

    public static final int NETWORK_ID = -1;

    public abstract int getNetworkId();

    public static long entityCount = 2; // 0 invalid, 1 for Synapse

    private static final EntityEntry[] BY_TYPE = new EntityEntry[256];
    private static final Map<String, EntityEntry> BY_NAME = new Object2ObjectOpenHashMap<>();
    private static final Map<Class<? extends Entity>, EntityEntry> BY_CLASS = new IdentityHashMap<>();

    //EaseCation 优化
    protected boolean needEntityBaseTick = true;

    protected Map<Integer, Player> hasSpawned = new ConcurrentHashMap<>();

    protected final Effect[] effects = new Effect[Effect.UNDEFINED];

    protected final long id;

    protected final EntityMetadata dataProperties = new EntityMetadata()
            .putLong(DATA_FLAGS, 0)
            .putLong(DATA_FLAGS_EXTENDED, 0)
            .putByte(DATA_COLOR, 0)
            .putShort(DATA_AIR, 300)
            .putShort(DATA_MAX_AIR, 300)
            .putString(DATA_NAMETAG, "")
            .putLong(DATA_LEAD_HOLDER_EID, -1)
            .putFloat(DATA_SCALE, 1f);

    private EntityPropertiesInstance properties;

    public final List<Entity> passengers = new ObjectArrayList<>();

    public Entity riding = null;

    public FullChunk chunk;

    protected EntityDamageEvent lastDamageCause = null;
    protected int lastDamageTick = 0;
    protected ObjectIntPair<EntityDamageByEntityEvent> lastEntityDamage;
    protected ObjectIntPair<EntityDamageByEntityEvent> lastPlayerDamage;

    public List<Block> blocksAround = new ObjectArrayList<>();
    public List<Block> collisionBlocks = new ObjectArrayList<>();

    public double lastX;
    public double lastY;
    public double lastZ;

    public boolean firstMove = true;

    public double motionX;
    public double motionY;
    public double motionZ;

    public Vector3 temporalVector;
    public double lastMotionX;
    public double lastMotionY;
    public double lastMotionZ;

    public double lastYaw;
    public double lastPitch;

    public double PitchDelta;
    public double YawDelta;

    public double entityCollisionReduction = 0; // Higher than 0.9 will result a fast collisions
    public AxisAlignedBB boundingBox;
    public boolean onGround;
    public boolean inBlock = false;
    public boolean positionChanged;
    public boolean motionChanged;
    public int deadTicks = 0;
    protected int age = 0;

    protected float health = 20;
    private int maxHealth = 20;

    protected float absorption = 0;

    protected float ySize = 0;
    public boolean keepMovement = false;

    public float fallDistance = 0;
    public int ticksLived = 0;
    public int lastUpdate;
    public int maxFireTicks;
    public int fireTicks = 0;
    public int inPortalTicks = 0;
    public int frozenTicks;
    public boolean freezing;

    public float scale = 1;

    public CompoundTag namedTag;

    protected boolean isStatic = false;

    public boolean isCollided;
    public boolean isCollidedHorizontally;
    public boolean isCollidedVertically;

    public int noDamageTicks;
    public boolean justCreated;
    public boolean fireProof;
    public boolean invulnerable;

    protected Server server;

    public double highestPosition;

    public boolean closed = false;

    protected boolean isPlayer = false;

    private volatile boolean initialized;
    private boolean fullyInitialized;

    public float getHeight() {
        return 0;
    }

    public float getY(float bbh) {
        return (float) y + getHeight() * bbh;
    }

    public float getEyeHeight() {
        return this.getHeight() * 0.85f;
    }

    public float getEyeY() {
        return (float) y + getEyeHeight();
    }

    public Vector3 getEyePosition() {
        return new Vector3(getX(), getEyeY(), getZ());
    }

    public float getWidth() {
        return 0;
    }

    public float getLength() {
        return getWidth();
    }

    protected double getStepHeight() {
        return 0;
    }

    public boolean canCollide() {
        return true;
    }

    protected float getGravity() {
        return 0;
    }

    protected float getDrag() {
        return 0;
    }

    protected float getBaseOffset() {
        return 0;
    }

    public Entity(FullChunk chunk, CompoundTag nbt) {
        this.id = Entity.entityCount++;

        if (this instanceof Player) {
            return;
        }

        this.init(chunk, nbt);
    }

    protected void initEntity() {
        if (this.namedTag.contains("ActiveEffects")) {
            ListTag<CompoundTag> effects = this.namedTag.getList("ActiveEffects", CompoundTag.class);
            for (CompoundTag e : effects.getAll()) {
                Effect effect = Effect.load(e);
                if (effect == null) {
                    continue;
                }
                this.addEffect(effect);
            }
        }

        if (this.namedTag.contains("CustomName")) {
            this.setNameTag(this.namedTag.getString("CustomName"));
            if (this.namedTag.contains("CustomNameVisible")) {
                this.setNameTagVisible(this.namedTag.getBoolean("CustomNameVisible"));
            }
            if (this.namedTag.contains("CustomNameAlwaysVisible")) {
                this.setNameTagAlwaysVisible(this.namedTag.getBoolean("CustomNameAlwaysVisible"));
            }
        }
        if (this.namedTag.contains("ScoreTag")) {
            this.setScoreTag(this.namedTag.getString("ScoreTag"));
        }

        this.setDataFlag(DATA_FLAG_HAS_COLLISION, true, false);

        float height = this.getHeight();
        float width = this.getWidth();
        //Some entities may have default bounding box (0)
        if (height > 0 && width > 0) {
            this.dataProperties.putFloat(DATA_BOUNDING_BOX_HEIGHT, height);
            this.dataProperties.putFloat(DATA_BOUNDING_BOX_WIDTH, width);
        }
        if (this.namedTag.contains("BoundingBoxWidth")) {
            this.setDataProperty(new FloatEntityData(DATA_BOUNDING_BOX_WIDTH, this.namedTag.getFloat("BoundingBoxWidth")), false);
        }
        if (this.namedTag.contains("BoundingBoxHeight")) {
            this.setDataProperty(new FloatEntityData(DATA_BOUNDING_BOX_HEIGHT, this.namedTag.getFloat("BoundingBoxHeight")), false);
        }

        this.dataProperties.putInt(DATA_HEALTH, (int) this.getHealth());

        this.scheduleUpdate();
    }

    protected final void init(FullChunk chunk, CompoundTag nbt) {
        if ((chunk == null || chunk.getProvider() == null)) {
            throw new ChunkException("Invalid garbage Chunk given to Entity");
        }

        if (this.initialized) {
            // We've already initialized this entity
            return;
        }
        this.initialized = true;

        this.isPlayer = this instanceof Player;
        this.temporalVector = new Vector3();

        this.justCreated = true;
        this.namedTag = nbt;

        this.chunk = chunk;
        this.setLevel(chunk.getProvider().getLevel());
        this.server = chunk.getProvider().getLevel().getServer();

        this.boundingBox = new SimpleAxisAlignedBB(0, 0, 0, 0, 0, 0);

        ListTag<DoubleTag> posList = this.namedTag.getList("Pos", DoubleTag.class);
        ListTag<FloatTag> rotationList = this.namedTag.getList("Rotation", FloatTag.class);
        ListTag<DoubleTag> motionList = this.namedTag.getList("Motion", DoubleTag.class);
        this.setPositionAndRotation(
                this.temporalVector.setComponents(
                        posList.get(0).data,
                        posList.get(1).data,
                        posList.get(2).data
                ),
                rotationList.get(0).data,
                rotationList.get(1).data
        );

        this.setMotion(this.temporalVector.setComponents(
                motionList.get(0).data,
                motionList.get(1).data,
                motionList.get(2).data
        ));

        if (!this.namedTag.contains("FallDistance")) {
            this.namedTag.putFloat("FallDistance", 0);
        }
        this.fallDistance = this.namedTag.getFloat("FallDistance");
        this.highestPosition = this.y + this.namedTag.getFloat("FallDistance");

        if (!this.namedTag.contains("Fire") || this.namedTag.getShort("Fire") > 32767) {
            this.namedTag.putShort("Fire", 0);
        }
        this.fireTicks = this.namedTag.getShort("Fire");

        if (!this.namedTag.contains("Air")) {
            this.namedTag.putShort("Air", 300);
        }
        this.setDataProperty(new ShortEntityData(DATA_AIR, Math.min(this.namedTag.getShort("Air"), 300)), false);

        if (!this.namedTag.contains("OnGround")) {
            this.namedTag.putBoolean("OnGround", false);
        }
        this.onGround = this.namedTag.getBoolean("OnGround");

        if (!this.namedTag.contains("Invulnerable")) {
            this.namedTag.putBoolean("Invulnerable", false);
        }
        this.invulnerable = this.namedTag.getBoolean("Invulnerable");

        if (!this.namedTag.contains("Scale")) {
            this.namedTag.putFloat("Scale", 1);
        }
        this.scale = this.namedTag.getFloat("Scale");
        this.setDataProperty(new FloatEntityData(DATA_SCALE, scale), false);

        this.loadProperties();

        this.chunk.addEntity(this);
        this.level.addEntity(this);

        this.initEntity();

        this.lastUpdate = this.server.getTick();
        this.server.getPluginManager().callEvent(new EntitySpawnEvent(this));

        this.scheduleUpdate();

        this.fullyInitialized = true;
    }

    protected void loadProperties() {
        this.properties = new EntityPropertiesInstance(EntityPropertyRegistry.getProperties(this));
        this.properties.load(this.namedTag.getCompound("properties"));
    }

    public boolean isInitialized() {
        return fullyInitialized;
    }

    public boolean hasCustomName() {
        return !this.getNameTag().isEmpty();
    }

    public String getNameTag() {
        return this.getDataPropertyString(DATA_NAMETAG);
    }

    public boolean isNameTagVisible() {
        return this.getDataFlag(DATA_FLAG_CAN_SHOW_NAMETAG);
    }

    public boolean isNameTagAlwaysVisible() {
        return this.getDataPropertyByte(DATA_ALWAYS_SHOW_NAMETAG) == 1;
    }

    public boolean setNameTag(String name) {
        return this.setDataProperty(new StringEntityData(DATA_NAMETAG, name));
    }

    public boolean setNameTagVisible(boolean value) {
        return this.setDataFlag(DATA_FLAG_CAN_SHOW_NAMETAG, value);
    }

    public boolean setNameTagAlwaysVisible(boolean value) {
        return this.setDataProperty(new ByteEntityData(DATA_ALWAYS_SHOW_NAMETAG, value ? 1 : 0));
    }

    public boolean setScoreTag(String score) {
        return this.setDataProperty(new StringEntityData(DATA_SCORE_TAG, score));
    }

    public String getScoreTag() {
        return this.getDataPropertyString(DATA_SCORE_TAG);
    }

    public boolean isSneaking() {
        return this.getDataFlag(DATA_FLAG_SNEAKING);
    }

    public boolean setSneaking(boolean value) {
        return this.setDataFlag(DATA_FLAG_SNEAKING, value);
    }

    public boolean isSwimming() {
        return this.getDataFlag(DATA_FLAG_SWIMMING);
    }

    public boolean setSwimming(boolean value) {
        return this.setDataFlag(DATA_FLAG_SWIMMING, value);
    }

    public boolean isSprinting() {
        return this.getDataFlag(DATA_FLAG_SPRINTING);
    }

    public boolean setSprinting(boolean value) {
        return this.setDataFlag(DATA_FLAG_SPRINTING, value);
    }

    public boolean isGliding() {
        return this.getDataFlag(DATA_FLAG_GLIDING);
    }

    public boolean setGliding(boolean value) {
        return this.setDataFlag(DATA_FLAG_GLIDING, value);
    }

    public boolean isCrawling() {
        return this.getDataFlag(DATA_FLAG_CRAWLING);
    }

    public boolean setCrawling(boolean value) {
        return this.setDataFlag(DATA_FLAG_CRAWLING, value);
    }

    public boolean isImmobile() {
        return this.getDataFlag(DATA_FLAG_NO_AI);
    }

    public boolean setImmobile(boolean value) {
        return this.setDataFlag(DATA_FLAG_NO_AI, value);
    }

    public boolean canClimb() {
        return this.getDataFlag(DATA_FLAG_CAN_CLIMB);
    }

    public boolean setCanClimb(boolean value) {
        return this.setDataFlag(DATA_FLAG_CAN_CLIMB, value);
    }

    public boolean canClimbWalls() {
        return this.getDataFlag(DATA_FLAG_WALLCLIMBING);
    }

    public boolean setCanClimbWalls(boolean value) {
        return this.setDataFlag(DATA_FLAG_WALLCLIMBING, value);
    }

    public boolean setScale(float scale) {
        if (this.scale == scale) {
            return false;
        }
        this.scale = scale;
        this.setDataProperty(new FloatEntityData(DATA_SCALE, this.scale));
        this.recalculateBoundingBox();
        return true;
    }

    public float getScale() {
        return this.scale;
    }

    public List<Entity> getPassengers() {
        return passengers;
    }

    public Entity getPassenger() {
        return Iterables.getFirst(this.passengers, null);
    }

    public boolean isPassenger(Entity entity) {
        return this.passengers.contains(entity);
    }

    public boolean isControlling(Entity entity) {
        return this.passengers.indexOf(entity) == 0;
    }

    public boolean hasControllingPassenger() {
        return !this.passengers.isEmpty() && isControlling(this.passengers.get(0));
    }

    public Entity getRiding() {
        return riding;
    }

    public void setRiding(Entity riding) {
        this.riding = riding;
    }

    public boolean isRiding() {
        return riding != null;
    }

    public Effect[] getEffects() {
        return effects;
    }

    public boolean removeAllEffects() {
        boolean dirty = false;
        for (Effect effect : this.effects) {
            if (effect == null) {
                continue;
            }

            if (!effect.remove(this)) {
                continue;
            }

            effects[effect.getId()] = null;
            dirty = true;
        }
        if (!dirty) {
            return false;
        }

        this.recalculateEffectColor();
        return true;
    }

    public boolean removeEffect(int effectId) {
        Effect effect = this.effects[effectId];
        if (effect != null) {
            boolean removed = effect.remove(this);
            if (!removed) {
                return false;
            }

            effects[effectId] = null;

            this.recalculateEffectColor();
            return true;
        }
        return false;
    }

    public boolean removeEffect(int... effectIds) {
        if (effectIds == null) {
            return false;
        }

        boolean dirty = false;
        for (int effectId : effectIds) {
            Effect effect = this.effects[effectId];
            if (effect == null) {
                continue;
            }

            if (!effect.remove(this)) {
                continue;
            }

            effects[effectId] = null;
            dirty = true;
        }
        if (!dirty) {
            return false;
        }

        this.recalculateEffectColor();
        return true;
    }

    @Nullable
    public Effect getEffect(int effectId) {
        return this.effects[effectId];
    }

    public boolean hasEffect(int effectId) {
        return this.effects[effectId] != null;
    }

    public boolean hasEffect() {
        for (Effect effect : effects) {
            if (effect != null) {
                return true;
            }
        }
        return false;
    }

    public boolean canBeAffected(int effectId) {
        return false;
    }

    public boolean addEffect(Effect effect) {
        if (effect == null) {
            return false;
        }

        if (!canBeAffected(effect.getId())) {
            return false;
        }

        boolean added = effect.add(this);
        if (!added) {
            return false;
        }

        this.effects[effect.getId()] = effect;

        this.recalculateEffectColor();

        onEffectAdded(effect);
        return true;
    }

    public boolean addEffect(Effect... effects) {
        if (effects == null) {
            return false;
        }

        boolean dirty = false;
        for (Effect effect : effects) {
            if (effect == null) {
                continue;
            }

            if (!canBeAffected(effect.getId())) {
                continue;
            }

            if (!effect.add(this)) {
                continue;
            }

            this.effects[effect.getId()] = effect;
            dirty = true;

            onEffectAdded(effect);
        }
        if (!dirty) {
            return false;
        }

        this.recalculateEffectColor();
        return true;
    }

    private void onEffectAdded(Effect effect) {
        if (effect.getId() == Effect.HEALTH_BOOST) {
            this.setHealth(this.getHealth() + 4 * (effect.getAmplifier() + 1));
        }
    }

    protected void recalculateEffectColor() {
        int red = 0;
        int green = 0;
        int blue = 0;

        int count = 0;
        boolean ambient = true;

        int effectCount = 0;
        long visibleEffects = 0;

        for (Effect effect : this.effects) {
            if (effect == null) {
                continue;
            }

            if (effect.isVisible()) {
                red += effect.getRed() * (effect.getAmplifier() + 1);
                green += effect.getGreen() * (effect.getAmplifier() + 1);
                blue += effect.getBlue() * (effect.getAmplifier() + 1);

                count += effect.getAmplifier() + 1;

                if (!effect.isAmbient()) {
                    ambient = false;
                }

                if (effectCount >= 8) {
                    continue;
                }
                effectCount++;

                visibleEffects = (visibleEffects << 7) | ((effect.getId() & 0x3f) << 1);

                if (effect.isAmbient()) {
                    visibleEffects |= 1;
                }
            }
        }

        if (count > 0) {
            int r = (red / count) & 0xff;
            int g = (green / count) & 0xff;
            int b = (blue / count) & 0xff;

            this.setDataProperty(new IntEntityData(Entity.DATA_POTION_COLOR, (r << 16) | (g << 8) | b));
            this.setDataProperty(new ByteEntityData(Entity.DATA_POTION_AMBIENT, ambient ? 1 : 0));
        } else {
            this.setDataProperty(new IntEntityData(Entity.DATA_POTION_COLOR, 0));
            this.setDataProperty(new ByteEntityData(Entity.DATA_POTION_AMBIENT, 0));
        }

        this.setDataProperty(new LongEntityData(Entity.DATA_VISIBLE_MOB_EFFECTS, visibleEffects));
    }

    public void recalculateBoundingBox() {
        this.recalculateBoundingBox(true);
    }

    public void recalculateBoundingBox(boolean send) {
        float height = this.getHeight() * this.scale;
        double radius = (this.getWidth() * this.scale) / 2d;
        this.boundingBox.setBounds(x - radius, y, z - radius, x + radius, y + height, z + radius);

        EntityMetadata metadata = new EntityMetadata();

        if (this.getHeight() > 0) {
            FloatEntityData bbH = new FloatEntityData(DATA_BOUNDING_BOX_HEIGHT, this.getHeight());
            this.dataProperties.put(bbH);
            metadata.put(bbH);
        }
        if (this.getWidth() > 0) {
            FloatEntityData bbW = new FloatEntityData(DATA_BOUNDING_BOX_WIDTH, this.getWidth());
            this.dataProperties.put(bbW);
            metadata.put(bbW);
        }

        if (send && !metadata.isEmpty()) {
            sendData(this.hasSpawned.values().toArray(new Player[0]), metadata);
        }
    }

    public static Entity createEntity(String name, Position pos) {
        return createEntity(name, pos.getChunk(), getDefaultNBT(pos));
    }

    public static Entity createEntity(String name, Position pos, Object... args) {
        return createEntity(name, pos.getChunk(), getDefaultNBT(pos), args);
    }

    public static Entity createEntity(int type, Position pos) {
        return createEntity(type, pos.getChunk(), getDefaultNBT(pos));
    }

    public static Entity createEntity(int type, Position pos, Object... args) {
        return createEntity(type, pos.getChunk(), getDefaultNBT(pos), args);
    }

    public static Entity createEntity(String name, FullChunk chunk, CompoundTag nbt) {
        if (name.startsWith("minecraft:")) {
            name = name.substring(10);
        }

        EntityEntry entry = BY_NAME.get(name);
        if (entry == null) {
            return null;
        }

        try {
            return entry.factory.create(chunk, nbt);
        } catch (Exception e) {
            log.error("Failed to create entity: {}", name, e);
        }
        return null;
    }

    public static Entity createEntity(String name, FullChunk chunk, CompoundTag nbt, Object... args) {
        if (name.startsWith("minecraft:")) {
            name = name.substring(10);
        }

        EntityEntry entry = BY_NAME.get(name);
        if (entry == null) {
            return null;
        }

        boolean more = args != null && args.length != 0;
        int parameterCount = more ? 2 + args.length : 2;
        for (Constructor<?> constructor : entry.clazz.getConstructors()) {
            if (constructor.getParameterCount() != parameterCount) {
                continue;
            }

            try {
                if (!more) {
                    return (Entity) constructor.newInstance(chunk, nbt);
                }

                Object[] objects = new Object[parameterCount];
                objects[0] = chunk;
                objects[1] = nbt;
                System.arraycopy(args, 0, objects, 2, args.length);
                return (Entity) constructor.newInstance(objects);
            } catch (Exception e) {
                log.error("Failed to create entity: {}", name, e);
            }
        }
        return null;
    }

    public static Entity createEntity(int type, FullChunk chunk, CompoundTag nbt) {
        if (type < 0 || type > 0xff) {
            return null;
        }

        EntityEntry entry = BY_TYPE[type];
        if (entry == null) {
            return null;
        }

        try {
            return entry.factory.create(chunk, nbt);
        } catch (Exception e) {
            log.error("Failed to create entity: {}", type, e);
        }
        return null;
    }

    public static Entity createEntity(int type, FullChunk chunk, CompoundTag nbt, Object... args) {
        if (type < 0 || type > 0xff) {
            return null;
        }

        EntityEntry entry = BY_TYPE[type];
        if (entry == null) {
            return null;
        }

        boolean more = args != null && args.length != 0;
        int parameterCount = more ? 2 + args.length : 2;
        for (Constructor<?> constructor : entry.clazz.getConstructors()) {
            if (constructor.getParameterCount() != parameterCount) {
                continue;
            }

            try {
                if (!more) {
                    return (Entity) constructor.newInstance(chunk, nbt);
                }

                Object[] objects = new Object[parameterCount];
                objects[0] = chunk;
                objects[1] = nbt;
                System.arraycopy(args, 0, objects, 2, args.length);
                return (Entity) constructor.newInstance(objects);
            } catch (Exception e) {
                log.error("Failed to create entity: {}", type, e);
            }
        }
        return null;
    }

    public static boolean registerEntity(String name, Class<? extends Entity> clazz, EntityFactory factory) {
        return registerEntity(name, clazz, factory, false);
    }

    public static boolean registerEntity(String identifier, String name, Class<? extends Entity> clazz, EntityFactory factory) {
        return registerEntity(identifier, name, clazz, factory, false);
    }

    public static boolean registerEntity(String name, Class<? extends Entity> clazz, EntityFactory factory, boolean force) {
        return registerEntity(name, name, clazz, factory, force);
    }

    public static boolean registerEntity(String identifier, String name, Class<? extends Entity> clazz, EntityFactory factory, boolean force) {
        return registerEntity(identifier, identifier, name, clazz, factory, force);
    }

    static boolean registerEntity(String identifier, String shortName, String name, Class<? extends Entity> clazz, EntityFactory factory, boolean force) {
        if (clazz == null) {
            return false;
        }

        int entityType;
        try {
            Field field = clazz.getField("NETWORK_ID");
            field.setAccessible(true);
            entityType = field.getInt(null);
        } catch (Exception e) {
            if (!force) {
                return false;
            }
            entityType = -1;
        }

        EntityEntry entry = new EntityEntry(clazz, entityType, identifier, shortName, name, factory);
        BY_NAME.put(name, entry);
        BY_NAME.put(shortName, entry);
        BY_NAME.put(identifier, entry);
        BY_CLASS.put(clazz, entry);
        if (entityType != -1) {
            BY_TYPE[entityType & 0xff] = entry;
        }
        return true;
    }

    @Nullable
    public static Class<? extends Entity> getClassByName(String name) {
        EntityEntry entry = BY_NAME.get(name);
        if (entry == null) {
            return null;
        }
        return entry.clazz;
    }

    public static CompoundTag getDefaultNBT(Vector3 pos) {
        return getDefaultNBT(pos, null);
    }

    public static CompoundTag getDefaultNBT(double x, double y, double z) {
        return getDefaultNBT(x, y, z, null);
    }

    public static CompoundTag getDefaultNBT(double x, double y, double z, @Nullable Vector3 motion) {
        if (motion == null) {
            return getDefaultNBT(x, y, z, 0, 0, 0);
        }
        return getDefaultNBT(x, y, z, motion.x, motion.y, motion.z);
    }

    public static CompoundTag getDefaultNBT(Vector3 pos, @Nullable Vector3 motion) {
        if (motion == null) {
            return getDefaultNBT(pos, 0, 0, 0);
        }
        return getDefaultNBT(pos, motion.x, motion.y, motion.z);
    }

    public static CompoundTag getDefaultNBT(Vector3 pos, double motionX, double motionY, double motionZ) {
        if (pos instanceof Location loc) {
            return getDefaultNBT(pos.x, pos.y, pos.z, motionX, motionY, motionZ, (float) loc.getYaw(), (float) loc.getPitch());
        }
        return getDefaultNBT(pos.x, pos.y, pos.z, motionX, motionY, motionZ);
    }

    public static CompoundTag getDefaultNBT(double x, double y, double z, double motionX, double motionY, double motionZ) {
        return getDefaultNBT(x, y, z, motionX, motionY, motionZ, 0, 0);
    }

    public static CompoundTag getDefaultNBT(Vector3 pos, double motionX, double motionY, double motionZ, float yaw, float pitch) {
        return getDefaultNBT(pos.x, pos.y, pos.z, motionX, motionY, motionZ, yaw, pitch);
    }

    public static CompoundTag getDefaultNBT(Vector3 pos, @Nullable Vector3 motion, float yaw, float pitch) {
        if (motion == null) {
            return getDefaultNBT(pos.x, pos.y, pos.z, 0, 0, 0, yaw, pitch);
        }
        return getDefaultNBT(pos.x, pos.y, pos.z, motion.x, motion.y, motion.z, yaw, pitch);
    }

    public static CompoundTag getDefaultNBT(double x, double y, double z, @Nullable Vector3 motion, float yaw, float pitch) {
        if (motion == null) {
            return getDefaultNBT(x, y, z, 0, 0, 0, yaw, pitch);
        }
        return getDefaultNBT(x, y, z, motion.x, motion.y, motion.z, yaw, pitch);
    }

    public static CompoundTag getDefaultNBT(double x, double y, double z, double motionX, double motionY, double motionZ, float yaw, float pitch) {
        return new CompoundTag()
                .putList(new ListTag<DoubleTag>("Pos")
                        .add(new DoubleTag("", x))
                        .add(new DoubleTag("", y))
                        .add(new DoubleTag("", z)))
                .putList(new ListTag<DoubleTag>("Motion")
                        .add(new DoubleTag("", motionX))
                        .add(new DoubleTag("", motionY))
                        .add(new DoubleTag("", motionZ)))
                .putList(new ListTag<FloatTag>("Rotation")
                        .add(new FloatTag("", yaw))
                        .add(new FloatTag("", pitch)));
    }

    public void saveNBT() {
        this.namedTag.setName(null);

        if (!(this instanceof Player)) {
            this.namedTag.putString("id", this.getSaveId()); // remove?
            this.namedTag.putString("identifier", this.getIdentifier());
            /*EntityEntry entry = BY_CLASS.get(this.getClass());
            if (entry != null) {
                this.namedTag.putString("id", entry.name); // remove?
                this.namedTag.putString("identifier", entry.identifier);
            } else {
                this.namedTag.putString("id", "");
                this.namedTag.putString("identifier", ":");
            }*/

            if (!this.getNameTag().isEmpty()) {
                this.namedTag.putString("CustomName", this.getNameTag());
                this.namedTag.putBoolean("CustomNameVisible", this.isNameTagVisible());
                this.namedTag.putBoolean("CustomNameAlwaysVisible", this.isNameTagAlwaysVisible());
            } else {
                this.namedTag.remove("CustomName");
                this.namedTag.remove("CustomNameVisible");
                this.namedTag.remove("CustomNameAlwaysVisible");
            }
            if (!this.getScoreTag().isEmpty()) {
                this.namedTag.putString("ScoreTag", this.getScoreTag());
            } else {
                this.namedTag.remove("ScoreTag");
            }
        }

        this.namedTag.putList(new ListTag<DoubleTag>("Pos")
                .add(new DoubleTag("", this.x))
                .add(new DoubleTag("", this.y))
                .add(new DoubleTag("", this.z))
        );

        this.namedTag.putList(new ListTag<DoubleTag>("Motion")
                .add(new DoubleTag("", this.motionX))
                .add(new DoubleTag("", this.motionY))
                .add(new DoubleTag("", this.motionZ))
        );

        this.namedTag.putList(new ListTag<FloatTag>("Rotation")
                .add(new FloatTag("", (float) this.yaw))
                .add(new FloatTag("", (float) this.pitch))
        );

        this.namedTag.putFloat("FallDistance", this.fallDistance);
        this.namedTag.putShort("Fire", this.fireTicks);
        this.namedTag.putShort("Air", this.getDataPropertyShort(DATA_AIR));
        this.namedTag.putBoolean("OnGround", this.onGround);
        this.namedTag.putBoolean("Invulnerable", this.invulnerable);
        this.namedTag.putFloat("Scale", this.scale);

        if (this.getDataProperties().exists(DATA_BOUNDING_BOX_WIDTH)) {
            this.namedTag.putFloat("BoundingBoxWidth", this.getDataPropertyFloat(DATA_BOUNDING_BOX_WIDTH));
        }
        if (this.getDataProperties().exists(DATA_BOUNDING_BOX_HEIGHT)) {
            this.namedTag.putFloat("BoundingBoxHeight", this.getDataPropertyFloat(DATA_BOUNDING_BOX_HEIGHT));
        }

        ListTag<CompoundTag> list = new ListTag<>("ActiveEffects");
        for (Effect effect : this.effects) {
            if (effect == null) {
                continue;
            }
            list.add(effect.save());
        }
        if (!list.isEmpty()) {
            this.namedTag.putList(list);
        } else {
            this.namedTag.remove("ActiveEffects");
        }

        this.namedTag.putCompound("properties", this.properties.save());
    }

    public String getName() {
        if (this.hasCustomName()) {
            return this.getNameTag();
        } else {
            return this.getSaveId();
        }
    }

    public String getIdentifier() {
        EntityEntry entry = BY_CLASS.get(this.getClass());
        if (entry == null) {
            return ":";
        }
        return entry.identifier;
    }

    public String getShortIdentifier() {
        EntityEntry entry = BY_CLASS.get(this.getClass());
        if (entry == null) {
            return ":";
        }
        return entry.shortName;
    }

    public final String getSaveId() {
        EntityEntry entry = BY_CLASS.get(this.getClass());
        if (entry == null) {
            return "";
        }
        return entry.name;
    }

    public void spawnTo(Player player) {
        if (!this.hasSpawned.containsKey(player.getLoaderId()) && this.chunk != null && player.usedChunks.containsKey(Level.chunkHash(this.getChunkX(), this.getChunkZ()))) {
            this.hasSpawned.put(player.getLoaderId(), player);
//            player.dataPacket(createAddEntityPacket());
        }
        /*
        if (this.riding != null) {
            this.riding.spawnTo(player);

            SetEntityLinkPacket pkk = new SetEntityLinkPacket();
            pkk.vehicleUniqueId = this.riding.getId();
            pkk.riderUniqueId = this.getId();
            pkk.type = 1;
            pkk.immediate = 1;

            player.dataPacket(pkk);
        }*/
    }

    protected DataPacket createAddEntityPacket() {
        AddEntityPacket addEntity = new AddEntityPacket();
        addEntity.type = this.getNetworkId();
        addEntity.entityUniqueId = this.getId();
        addEntity.entityRuntimeId = this.getId();
        addEntity.yaw = (float) this.yaw;
        addEntity.headYaw = (float) this.yaw;
        addEntity.pitch = (float) this.pitch;
        addEntity.x = (float) this.x;
        addEntity.y = (float) this.y + this.getBaseOffset();
        addEntity.z = (float) this.z;
        addEntity.speedX = (float) this.motionX;
        addEntity.speedY = (float) this.motionY;
        addEntity.speedZ = (float) this.motionZ;
        addEntity.metadata = this.dataProperties;
        Pair<Int2IntMap, Int2FloatMap> propertyValues = getProperties().getValues();
        if (propertyValues != null) {
            addEntity.intProperties = propertyValues.left();
            addEntity.floatProperties = propertyValues.right();
        }

//        addEntity.links = new EntityLink[this.passengers.size()];
//        for (int i = 0; i < addEntity.links.length; i++) {
//            addEntity.links[i] = new EntityLink(this.getId(), this.passengers.get(i).getId(), i == 0 ? EntityLink.TYPE_RIDER : EntityLink.TYPE_PASSENGER, false, false);
//        }

        return addEntity;
    }

    public Map<Integer, Player> getViewers() {
        return hasSpawned;
    }

    public void sendPotionEffects(Player player) {
        for (Effect effect : this.effects) {
            if (effect == null) {
                continue;
            }

            MobEffectPacket pk = new MobEffectPacket();
            pk.eid = this.getId();
            pk.effectId = effect.getId();
            pk.amplifier = effect.getAmplifier();
            pk.particles = effect.isVisible();
            pk.duration = effect.isInfinite() ? -1 : effect.getDuration();
            pk.eventId = MobEffectPacket.EVENT_ADD;
            player.dataPacket(pk);
        }
    }

    public void sendData(Player player) {
        this.sendData(player, null);
    }

    public void sendData(Player player, EntityMetadata data) {
        SetEntityDataPacket pk = new SetEntityDataPacket();
        pk.eid = this.getId();
        if (data == null) {
            pk.metadata = this.dataProperties.copy();

            Pair<Int2IntMap, Int2FloatMap> propertyValues = getProperties().getValues();
            if (propertyValues != null) {
                pk.intProperties = propertyValues.left();
                pk.floatProperties = propertyValues.right();
            }
        } else {
            pk.metadata = data;

            EntityData<?> nukkitFlags = this.dataProperties.get(DATA_NUKKIT_FLAGS);
            if (nukkitFlags != null) {
                data.put(nukkitFlags);
            }
        }
        player.dataPacket(pk);
    }

    public void sendData(Player[] players) {
        this.sendData(players, null);
    }

    public void sendData(Player[] players, EntityMetadata data) {
        SetEntityDataPacket pk = new SetEntityDataPacket();
        pk.eid = this.getId();
        if (data == null) {
            pk.metadata = this.dataProperties.copy();

            Pair<Int2IntMap, Int2FloatMap> propertyValues = getProperties().getValues();
            if (propertyValues != null) {
                pk.intProperties = propertyValues.left();
                pk.floatProperties = propertyValues.right();
            }
        } else {
            pk.metadata = data;

            EntityData<?> nukkitFlags = this.dataProperties.get(DATA_NUKKIT_FLAGS);
            if (nukkitFlags != null) {
                data.put(nukkitFlags);
            }
        }

        for (Player player : players) {
            if (player == this) {
                continue;
            }
            player.dataPacket(pk.clone());
        }
        if (this instanceof Player) {
            ((Player) this).dataPacket(pk);
        }
    }

    public void despawnFrom(Player player) {
        if (player.riding == this && !dismountEntity(player)) {
            log.warn("Please remove entity links before despawning", new Throwable("debug trace"));
            Utils.pauseInIde();
        }

        if (this.hasSpawned.remove(player.getLoaderId()) != null) {
            RemoveEntityPacket pk = new RemoveEntityPacket();
            pk.eid = this.getId();
            player.dataPacket(pk);
        }
    }

    public boolean attack(EntityDamageEvent source) {
        if ((source.getCause() == DamageCause.FIRE || source.getCause() == DamageCause.FIRE_TICK
                || source.getCause() == DamageCause.CAMPFIRE || source.getCause() == DamageCause.SOUL_CAMPFIRE
                || source.getCause() == DamageCause.LAVA || source.getCause() == DamageCause.MAGMA)
                && (fireProof || hasEffect(Effect.FIRE_RESISTANCE))) {
            return false;
        }

        getServer().getPluginManager().callEvent(source);
        if (source.isCancelled()) {
            return false;
        }

        if (source.getCause() != DamageCause.SUICIDE) {
            // Make fire aspect to set the target in fire before dealing any damage so the target is in fire on death even if killed by the first hit
            if (source instanceof EntityDamageByEntityEvent) {
                Enchantment[] enchantments = ((EntityDamageByEntityEvent) source).getWeaponEnchantments();
                if (enchantments != null) {
                    for (Enchantment enchantment : enchantments) {
                        enchantment.doAttack(((EntityDamageByEntityEvent) source).getDamager(), this);
                    }
                }
            }

            if (this.absorption > 0) {  // Damage Absorption
                this.setAbsorption(Math.max(0, this.getAbsorption() + source.getDamage(EntityDamageEvent.DamageModifier.ABSORPTION)));
            }
        }

        setLastDamageCause(source);
        setHealth(getHealth() - source.getFinalDamage());
        return true;
    }

    public boolean attack(float damage) {
        return this.attack(new EntityDamageEvent(this, DamageCause.CUSTOM, damage));
    }

    protected void onAttackSuccess(EntityDamageByEntityEvent source) {
    }

    public void heal(EntityRegainHealthEvent source) {
        this.server.getPluginManager().callEvent(source);
        if (source.isCancelled()) {
            return;
        }
        this.setHealth(this.getHealth() + source.getAmount());
    }

    public void heal(float amount) {
        this.heal(new EntityRegainHealthEvent(this, amount, EntityRegainHealthEvent.CAUSE_REGEN));
    }

    public float getHealth() {
        return health;
    }

    public boolean isAlive() {
        return this.health >= 1;
    }

    public boolean setHealth(float health) {
        if (this.health == health) {
            return false;
        }

        if (health < 1) {
            if (this.isAlive()) {
                this.kill();
            }
        } else if (health <= this.getMaxHealth() || health < this.health) {
            this.health = health;
        } else {
            this.health = this.getMaxHealth();
        }
        return true;
    }

    public void setLastDamageCause(EntityDamageEvent type) {
        this.lastDamageCause = type;
        lastDamageTick = server.getTick();

        if (type instanceof EntityDamageByEntityEvent event) {
            ObjectIntPair<EntityDamageByEntityEvent> record = ObjectIntPair.of(event, lastDamageTick);
            lastEntityDamage = record;

            if (event.getDamager() instanceof Player) {
                lastPlayerDamage = record;
            }
        }
    }

    @Nullable
    public EntityDamageEvent getLastDamageCause() {
        return lastDamageCause;
    }

    public int getLastDamageTick() {
        return lastDamageTick;
    }

    /**
     * @return damage source and tick time
     */
    @Nullable
    public ObjectIntPair<EntityDamageByEntityEvent> getLastEntityDamageCause() {
        return lastEntityDamage;
    }

    /**
     * @param ticks time limit
     * @return damage source and tick time
     */
    @Nullable
    public ObjectIntPair<EntityDamageByEntityEvent> getLastEntityDamageCause(int ticks) {
        if (lastEntityDamage == null) {
            return null;
        }
        if (lastEntityDamage.rightInt() + ticks < server.getTick()) {
            return null;
        }
        return lastEntityDamage;
    }

    /**
     * @return damage source and tick time
     */
    @Nullable
    public ObjectIntPair<EntityDamageByEntityEvent> getLastPlayerDamageCause() {
        return lastPlayerDamage;
    }

    /**
     * @param ticks time limit
     * @return damage source and tick time
     */
    @Nullable
    public ObjectIntPair<EntityDamageByEntityEvent> getLastPlayerDamageCause(int ticks) {
        if (lastPlayerDamage == null) {
            return null;
        }
        if (lastPlayerDamage.rightInt() + ticks < server.getTick()) {
            return null;
        }
        return lastPlayerDamage;
    }

    public int getMaxHealth() {
        Effect healthBoost = this.getEffect(Effect.HEALTH_BOOST);
        return maxHealth + (healthBoost != null ? 4 * (healthBoost.getAmplifier() + 1) : 0);
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public boolean canCollideWith(Entity entity) {
        if (entity instanceof Player && ((Player) entity).isSpectator()) {
            return false;
        }
        return !this.justCreated && this != entity;
    }

    protected boolean checkObstruction(double x, double y, double z) {
        if (this.level.getCollisionCubes(this, this.getBoundingBox(), false).length == 0) {
            return false;
        }

        int i = Mth.floor(x);
        int j = Mth.floor(y);
        int k = Mth.floor(z);

        double diffX = x - i;
        double diffY = y - j;
        double diffZ = z - k;

        if (!Block.transparent[this.level.getBlock(i, j, k).getId()]) {
            boolean flag = Block.transparent[this.level.getBlock(i - 1, j, k).getId()];
            boolean flag1 = Block.transparent[this.level.getBlock(i + 1, j, k).getId()];
            boolean flag2 = Block.transparent[this.level.getBlock(i, j - 1, k).getId()];
            boolean flag3 = Block.transparent[this.level.getBlock(i, j + 1, k).getId()];
            boolean flag4 = Block.transparent[this.level.getBlock(i, j, k - 1).getId()];
            boolean flag5 = Block.transparent[this.level.getBlock(i, j, k + 1).getId()];

            int direction = -1;
            double limit = 9999;

            if (flag) {
                limit = diffX;
                direction = 0;
            }

            if (flag1 && 1 - diffX < limit) {
                limit = 1 - diffX;
                direction = 1;
            }

            if (flag2 && diffY < limit) {
                limit = diffY;
                direction = 2;
            }

            if (flag3 && 1 - diffY < limit) {
                limit = 1 - diffY;
                direction = 3;
            }

            if (flag4 && diffZ < limit) {
                limit = diffZ;
                direction = 4;
            }

            if (flag5 && 1 - diffZ < limit) {
                direction = 5;
            }

            float force = ThreadLocalRandom.current().nextFloat() * 0.2f + 0.1f;

            if (direction == 0) {
                this.motionX = -force;

                return true;
            }

            if (direction == 1) {
                this.motionX = force;

                return true;
            }

            if (direction == 2) {
                this.motionY = -force;

                return true;
            }

            if (direction == 3) {
                this.motionY = force;

                return true;
            }

            if (direction == 4) {
                this.motionZ = -force;

                return true;
            }

            if (direction == 5) {
                this.motionZ = force;

                return true;
            }
        }

        return false;
    }

    public boolean entityBaseTick() {
        return this.entityBaseTick(1);
    }

    public boolean entityBaseTick(int tickDiff) {
        if (!this.isPlayer) {
            this.blocksAround = null;
            this.collisionBlocks = null;
        }
        this.justCreated = false;

        if (!this.isAlive()) {
            this.removeAllEffects();
            this.despawnFromAll();
            if (!this.isPlayer) {
                this.close();
            }
            return false;
        }

        if (riding != null && (!riding.isAlive() || riding.isClosed())) {
            riding.dismountEntity(this);
        }

        updatePassengers();

        for (Effect effect : this.effects) {
            if (effect == null) {
                continue;
            }

            if (effect.canTick()) {
                effect.applyEffect(this);
            }

            if (effect.isInfinite()) {
                continue;
            }

            effect.setDuration(effect.getDuration() - tickDiff);

            if (effect.getDuration() <= 0) {
                this.removeEffect(effect.getId());
            }
        }

        boolean hasUpdate = false;

        if (this.needEntityBaseTick) {
            this.checkBlockCollision();

            if (this.y < level.getHeightRange().getMinY() - 18 && this.isAlive()) {
                this.attack(new EntityDamageEvent(this, DamageCause.VOID, 4));
                hasUpdate = true;
            }

            if (this.fireTicks > 0) {
                if (this.fireProof) {
                    this.fireTicks -= 4 * tickDiff;
                    if (this.fireTicks < 0) {
                        this.fireTicks = 0;
                    }
                } else {
                    if (!this.hasEffect(Effect.FIRE_RESISTANCE) && ((this.fireTicks % 20) == 0 || tickDiff > 20) && (!isPlayer || level.gameRules.getBoolean(GameRule.FIRE_DAMAGE))) {
                        this.attack(new EntityDamageEvent(this, DamageCause.FIRE_TICK, 1));
                    }
                    this.fireTicks -= tickDiff;
                }
                if (this.fireTicks <= 0) {
                    this.extinguish();
                } else {
                    this.setDataFlag(DATA_FLAG_ONFIRE, true);
                    hasUpdate = true;
                }
            }
        }

        if (this.noDamageTicks > 0) {
            this.noDamageTicks -= tickDiff;
            if (this.noDamageTicks < 0) {
                this.noDamageTicks = 0;
            }
        }

        if (this.inPortalTicks == 80) {
            EntityPortalEnterEvent ev = new EntityPortalEnterEvent(this, PortalType.NETHER);
            getServer().getPluginManager().callEvent(ev);

            //TODO: teleport
        }

        this.age += tickDiff;
        this.ticksLived += tickDiff;

        return hasUpdate;
    }

    public void updateMovement() {
        double diffPosition = (this.x - this.lastX) * (this.x - this.lastX) + (this.y - this.lastY) * (this.y - this.lastY) + (this.z - this.lastZ) * (this.z - this.lastZ);
        double diffRotation = (this.yaw - this.lastYaw) * (this.yaw - this.lastYaw) + (this.pitch - this.lastPitch) * (this.pitch - this.lastPitch);

        double diffMotion = (this.motionX - this.lastMotionX) * (this.motionX - this.lastMotionX) + (this.motionY - this.lastMotionY) * (this.motionY - this.lastMotionY) + (this.motionZ - this.lastMotionZ) * (this.motionZ - this.lastMotionZ);

        if (diffPosition > 0.0001 || diffRotation > 1.0) { //0.2 ** 2, 1.5 ** 2
            this.lastX = this.x;
            this.lastY = this.y;
            this.lastZ = this.z;

            this.lastYaw = this.yaw;
            this.lastPitch = this.pitch;

            this.addMovement(this.x, this.y + this.getBaseOffset(), this.z, this.yaw, this.pitch, this.yaw);
        }

        if (diffMotion > 0.0025 || (diffMotion > 0.0001 && this.getMotion().lengthSquared() <= 0.0001)) { //0.05 ** 2
            this.lastMotionX = this.motionX;
            this.lastMotionY = this.motionY;
            this.lastMotionZ = this.motionZ;

            this.addMotion(this.motionX, this.motionY, this.motionZ);
        }
    }

    public void addMovement(double x, double y, double z, double yaw, double pitch, double headYaw) {
        MoveEntityPacket pk = new MoveEntityPacket();
        pk.eid = this.getId();
        pk.x = (float) x;
        pk.y = (float) y;
        pk.z = (float) z;
        pk.yaw = (float) yaw;
        pk.headYaw = (float) headYaw;
        pk.pitch = (float) pitch;
        pk.onGround = this.onGround;
        pk.setChannel(DataPacket.CHANNEL_MOVING);
        Server.broadcastPacket(this.getViewers().values(), pk);
        //this.level.addEntityMovement(this.chunk.getX(), this.chunk.getZ(), this.id, x, y, z, yaw, pitch, headYaw);
    }

    public void addMotion(double motionX, double motionY, double motionZ) {
        int chunkX = this.getFloorX() >> 4;
        int chunkZ = this.getFloorZ() >> 4;
        SetEntityMotionPacket pk = new SetEntityMotionPacket();
        pk.eid = this.getId();
        pk.motionX = (float) motionX;
        pk.motionY = (float) motionY;
        pk.motionZ = (float) motionZ;
        this.level.addChunkPacket(chunkX, chunkZ, pk);
    }

    public void broadcastMotionPredictionHints() {
        broadcastMotionPredictionHints(onGround);
    }

    public void broadcastMotionPredictionHints(boolean onGround) {
        broadcastMotionPredictionHints((float) motionX, (float) motionY, (float) motionZ, onGround);
    }

    public void broadcastMotionPredictionHints(float motionX, float motionY, float motionZ) {
        broadcastMotionPredictionHints(motionX, motionY, motionZ, onGround);
    }

    public void broadcastMotionPredictionHints(float motionX, float motionY, float motionZ, boolean onGround) {
        for (Player player : getViewers().values()) {
            player.sendMotionPredictionHints(getId(), motionX, motionY, motionZ, onGround);
        }
    }

    public Vector2 getDirectionPlane() {
        return new Vector2(-Mth.cos(Math.toRadians(this.yaw) - Math.PI / 2), -Mth.sin(Math.toRadians(this.yaw) - Math.PI / 2)).normalize();
    }

    public BlockFace getHorizontalFacing() {
        return BlockFace.fromHorizontalIndex(Mth.floor((this.yaw * 4.0F / 360.0F) + 0.5D) & 3);
    }

    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        if (!this.isAlive()) {
            ++this.deadTicks;
            if (this.deadTicks >= 10) {
                this.despawnFromAll();
                if (!this.isPlayer) {
                    this.close();
                }
            }
            return this.deadTicks < 10;
        }

        int tickDiff = currentTick - this.lastUpdate;

        if (tickDiff <= 0) {
            return false;
        }

        this.lastUpdate = currentTick;

        boolean hasUpdate = this.entityBaseTick(tickDiff);

        this.updateMovement();

        return hasUpdate;
    }

    public boolean mountEntity(Entity entity) {
        return mountEntity(entity, SetEntityLinkPacket.TYPE_RIDE);
    }

    public boolean mountEntity(Entity entity, byte mode) {
        return mountEntity(entity, mode, false);
    }

    public boolean mountEntity(Entity entity, boolean riderInitiated) {
        return mountEntity(entity, SetEntityLinkPacket.TYPE_RIDE, riderInitiated);
    }

    /**
     * Mount an Entity from a/into vehicle
     *
     * @param entity The target Entity
     * @return {@code true} if the mounting successful
     */
    public boolean mountEntity(Entity entity, byte mode, boolean riderInitiated) {
        Objects.requireNonNull(entity, "The target of the mounting entity can't be null");

        if (isPassenger(entity) || entity.riding != null && !entity.riding.dismountEntity(entity, false)) {
            return false;
        }

        // Entity entering a vehicle
        EntityVehicleEnterEvent ev = new EntityVehicleEnterEvent(entity, this);
        server.getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return false;
        }

        entity.setSeatPosition(getMountedOffset(entity));
        this.onMountEntity(entity);

        broadcastLinkPacket(entity, mode, riderInitiated);

        // Add variables to entity
        entity.riding = this;
        passengers.add(entity);

        updatePassengerPosition(entity);
        return true;
    }

    protected void onMountEntity(Entity entity) {
        entity.setDataFlag(DATA_FLAG_RIDING, true);
    }

    public boolean dismountEntity(Entity entity) {
        return this.dismountEntity(entity, true);
    }

    public boolean dismountEntity(Entity entity, boolean sendLinks) {
        // Run the events
        EntityVehicleExitEvent ev = new EntityVehicleExitEvent(entity, this);
        server.getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            int seatIndex = this.passengers.indexOf(entity);
            if (seatIndex == 0) {
                this.broadcastLinkPacket(entity, SetEntityLinkPacket.TYPE_RIDE);
            } else if (seatIndex != -1) {
                this.broadcastLinkPacket(entity, SetEntityLinkPacket.TYPE_PASSENGER);
            }
            return false;
        }

        if (sendLinks) {
            broadcastLinkPacket(entity, SetEntityLinkPacket.TYPE_REMOVE);
        }

        // Refurbish the entity
        entity.riding = null;
        this.onDismountEntity(entity);
        passengers.remove(entity);

        entity.setSeatPosition(new Vector3f());
        updatePassengerPosition(entity);

        return true;
    }

    protected void onDismountEntity(Entity entity) {
        entity.setDataFlag(DATA_FLAG_RIDING, false);
    }

    public void broadcastLinkPacket(Entity rider, byte type) {
        broadcastLinkPacket(rider, type, false);
    }

    public void broadcastLinkPacket(Entity rider, byte type, boolean riderInitiated) {
        SetEntityLinkPacket pk = new SetEntityLinkPacket();
        pk.vehicleUniqueId = getId();         // To the?
        pk.riderUniqueId = rider.getId(); // From who?
        pk.type = type;
        pk.riderInitiated = riderInitiated;

        Server.broadcastPacket(this.hasSpawned.values(), pk);
    }

    public void updatePassengers() {
        if (this.passengers.isEmpty()) {
            return;
        }

        for (Entity passenger : new ObjectArrayList<>(this.passengers)) {
            if (!passenger.isAlive()) {
                dismountEntity(passenger);
                continue;
            }

            updatePassengerPosition(passenger);
        }
    }

    protected void updatePassengerPosition(Entity passenger) {
        passenger.setPosition(this.add(passenger.getSeatPosition().asVector3()));
    }

    public boolean setSeatPosition(Vector3f pos) {
        return this.setDataProperty(new Vector3fEntityData(DATA_SEAT_OFFSET, pos));
    }

    public Vector3f getSeatPosition() {
        return this.getDataPropertyVector3f(DATA_SEAT_OFFSET);
    }

    public Vector3f getMountedOffset(Entity entity) {
        return new Vector3f(0, getHeight() * 0.75f, 0);
    }

    public final void scheduleUpdate() {
        this.level.updateEntities.put(this.id, this);
    }

    public boolean isOnFire() {
        return this.fireTicks > 0;
    }

    public void setOnFire(int seconds) {
        if (seconds > 0 && (hasEffect(Effect.FIRE_RESISTANCE) || !isAlive())) {
            extinguish();
            return;
        }

        int ticks = seconds * 20;
        if (ticks > this.fireTicks) {
            this.fireTicks = ticks;
        }
    }

    public float getAbsorption() {
        return absorption;
    }

    public void setAbsorption(float absorption) {
        absorption = Mth.clamp(absorption, 0, 16);
        if (absorption != this.absorption) {
            this.absorption = absorption;
            if (this instanceof Player) {
                Attribute attribute = Attribute.getAttribute(Attribute.ABSORPTION);
                if (absorption > attribute.getMaxValue()) {
                    attribute.setMaxValue(absorption);
                }
                ((Player) this).setAttribute(attribute.setValue(absorption));
            }
        }
    }

    public boolean canBePushed() {
        return true;
    }

    public BlockFace getDirection() {
        double rotation = this.yaw % 360;
        if (rotation < 0) {
            rotation += 360.0;
        }
        if ((0 <= rotation && rotation < 45) || (315 <= rotation && rotation < 360)) {
            return BlockFace.SOUTH;
        } else if (45 <= rotation && rotation < 135) {
            return BlockFace.WEST;
        } else if (135 <= rotation && rotation < 225) {
            return BlockFace.NORTH;
        } else if (225 <= rotation && rotation < 315) {
            return BlockFace.EAST;
        } else {
            return null;
        }
    }

    public void extinguish() {
        this.fireTicks = 0;
        this.setDataFlag(DATA_FLAG_ONFIRE, false);
    }

    public boolean canTriggerWalking() {
        return true;
    }

    public void resetFallDistance() {
        this.highestPosition = level.getHeightRange().getMinY();
    }

    protected void updateFallState(boolean onGround) {
        if (isRiding()) {
            resetFallDistance();
            return;
        }

        if (onGround) {
            fallDistance = (float) (this.highestPosition - this.y);

            if (fallDistance > 0) {
                if (this instanceof EntityLiving && !this.isInsideOfWater(false)) {
                    this.fall(fallDistance);
                }
                this.resetFallDistance();
            }
        }
    }

    public AxisAlignedBB getBoundingBox() {
        return this.boundingBox;
    }

    public void fall(float fallDistance) {
        if (this.hasEffect(Effect.SLOW_FALLING)) {
            return;
        }

        Block down = null;
        if (fallDistance > 0.75f) {
            down = this.level.getBlock(this.floor().down());
            if (down.getId() == Item.FARMLAND) {
                Event ev;
                if (this instanceof Player) {
                    ev = new PlayerInteractEvent((Player) this, null, down, null, Action.PHYSICAL);
                } else {
                    ev = new EntityInteractEvent(this, down);
                }
                ev.call();
                if (!ev.isCancelled()) {
                    this.level.setBlock(down, Block.get(BlockID.DIRT), true);
                }
            }
        }

        if (isPlayer && !level.gameRules.getBoolean(GameRule.FALL_DAMAGE)) {
            return;
        }

        DamageCause cause;
        float multiplier;
        Block half;
        if (fallDistance > 0.75f && (half = level.getBlock(subtract(0, 0.49f, 0))).getId() == Block.POINTED_DRIPSTONE && half.getDamage() == BlockDripstonePointed.THICKNESS_TIP) {
            cause = DamageCause.STALAGMITE;
            multiplier = 2;
            fallDistance += 2 + 0.25f;
        } else {
            cause = DamageCause.FALL;
            multiplier = 1;
        }

        int jumpBoost;
        Effect effect = this.getEffect(Effect.JUMP_BOOST);
        if (effect != null) {
            jumpBoost = effect.getAmplifier() + 1;
        } else {
            jumpBoost = 0;
        }

        float damage = Mth.ceil((fallDistance - 3 - jumpBoost) * multiplier);
        if (damage <= 0) {
            return;
        }

        Block block = this.level.getBlock(this);
        if (block.getId() == BlockID.BLOCK_BED) {
            damage -= damage * 0.5f;
        } else {
            if (down == null) {
                down = block.down();
            }
            int id = down.getId();
            if (id == BlockID.HAY_BLOCK || id == BlockID.HONEY_BLOCK) {
                damage -= damage * 0.8f;
            }
        }

        this.attack(new EntityDamageEvent(this, cause, damage));
    }

    public void handleLavaMovement() {
        //todo
    }

    public void moveFlying(float strafe, float forward, float friction) {
        // This is special for Nukkit! :)
        float speed = strafe * strafe + forward * forward;
        if (speed >= 1.0E-4F) {
            speed = (float) Math.sqrt(speed);
            if (speed < 1.0F) {
                speed = 1.0F;
            }
            speed = friction / speed;
            strafe *= speed;
            forward *= speed;
            float nest = Mth.sin((float) (this.yaw * 3.1415927F / 180.0F));
            float place = Mth.cos((float) (this.yaw * 3.1415927F / 180.0F));
            this.motionX += strafe * place - forward * nest;
            this.motionZ += forward * place + strafe * nest;
        }
    }

    public void onCollideWithPlayer(EntityHuman entityPlayer) {

    }

    public void applyEntityCollision(Entity entity) {
        if (entity.riding != this && !entity.passengers.contains(this)) {
            double dx = entity.x - this.x;
            double dy = entity.z - this.z;
            double dz = Math.max(Math.abs(dx), Math.abs(dy));

            if (dz >= 0.009999999776482582D) {
                dz = (float) Math.sqrt((float) dz);
                dx /= dz;
                dy /= dz;
                double d3 = 1.0D / dz;

                if (d3 > 1.0D) {
                    d3 = 1.0D;
                }

                dx *= d3;
                dy *= d3;
                dx *= 0.05000000074505806;
                dy *= 0.05000000074505806;
                dx *= 1F + entityCollisionReduction;

                if (this.riding == null) {
                    motionX -= dx;
                    motionZ -= dy;
                }
            }
        }
    }

    public void onStruckByLightning(Entity entity) {
        if (this.attack(new EntityDamageByEntityEvent(entity, this, DamageCause.LIGHTNING, 5))) {
            if (this.fireTicks < 8 * 20) {
                this.setOnFire(8);
            }
        }
    }

    public void onPushByPiston(BlockEntityPistonArm piston) {

    }

    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        return onInteract(player, item);
    }

    public boolean onInteract(Player player, Item item) {
        return false;
    }

    protected boolean switchLevel(Level targetLevel) {
        if (this.closed) {
            return false;
        }

        if (this.isValid()) {
            EntityLevelChangeEvent ev = new EntityLevelChangeEvent(this, this.level, targetLevel);
            this.server.getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return false;
            }

            this.level.removeEntity(this);
            if (this.chunk != null) {
                this.chunk.removeEntity(this);
            }
            this.despawnFromAll();
        }

        this.setLevel(targetLevel);
        this.level.addEntity(this);
        this.chunk = null;

        return true;
    }

    public Position getPosition() {
        return new Position(this.x, this.y, this.z, this.level);
    }

    public Location getLocation() {
        return new Location(this.x, this.y, this.z, this.yaw, this.pitch, this.level);
    }

    public boolean isInsideOfWater() {
        return this.isInsideOfWater(true);
    }

    public boolean isInsideOfWater(boolean eyeHeight) {
        return isInsideOfWater(eyeHeight ? this.getEyeHeight() : 0);
    }

    public boolean isInsideOfWater(float heightOffset) {
        double y = this.y + heightOffset;
        Block block = this.level.getBlock(Mth.floor(this.x), Mth.floor(y), Mth.floor(this.z));
        if (!block.isWater() && !block.isAir() && block.canContainWater()) {
            block = level.getExtraBlock(block);
        }

        if (block.isWater()) {
            double f = (block.y + 1) - (((BlockWater) block).getFluidHeightPercent() - 0.1111111);
            return y < f;
        }

        return false;
    }

    public boolean isInsideOfLiquid() {
        Block block = this.level.getBlock(this);
        if (!block.isLiquid() && !block.isAir() && block.canContainWater()) {
            block = level.getExtraBlock(block);
        }

        if (!block.isLiquid()) {
            return false;
        }

        return this.y < block.y + 1 - (((BlockLiquid) block).getFluidHeightPercent() - 0.1111111);
    }

    public boolean isInsideOfSolid() {
        double y = this.y + this.getEyeHeight();
        Block block = this.level.getBlock(Mth.floor(this.x), Mth.floor(y), Mth.floor(this.z));

        AxisAlignedBB bb = block.getBoundingBox();

        return bb != null && block.isSolid() && !block.isTransparent() && bb.intersectsWith(this.getBoundingBox());
    }

    public boolean isInsideOfFire() {
        for (Block block : this.getCollisionBlocks()) {
            if (block.isFire()) {
                return true;
            }
        }

        return false;
    }

    public boolean isOnLadder() {
        Block b = this.getLevelBlock();

        return b.getId() == Block.LADDER;
    }

    public boolean fastMove(double dx, double dy, double dz) {
        if (dx == 0 && dy == 0 && dz == 0) {
            return true;
        }

        AxisAlignedBB newBB = this.boundingBox.getOffsetBoundingBox(dx, dy, dz);

        if (server.getAllowFlight() || !this.level.hasCollision(this, newBB, false)) {
            this.boundingBox = newBB;
        }

        this.x = (this.boundingBox.getMinX() + this.boundingBox.getMaxX()) / 2;
        this.y = this.boundingBox.getMinY() - this.ySize;
        this.z = (this.boundingBox.getMinZ() + this.boundingBox.getMaxZ()) / 2;

        this.checkChunks();

        if (!this.onGround || dy != 0) {
            AxisAlignedBB bb = this.boundingBox.clone();
            bb.setMinY(bb.getMinY() - 0.75);

            this.onGround = this.level.getCollisionBlocks(bb, true).length > 0;
        }
        this.isCollided = this.onGround;
        this.updateFallState(this.onGround);
        /*this.motionX = dx;
        this.motionY = dy;
        this.motionZ = dz;*/
        return true;
    }

    public boolean move(double dx, double dy, double dz) {
        if (dx == 0 && dz == 0 && dy == 0) {
            return true;
        }

        if (this.keepMovement) {
            this.boundingBox.offset(dx, dy, dz);
            this.setPosition(this.temporalVector.setComponents((this.boundingBox.getMinX() + this.boundingBox.getMaxX()) / 2, this.boundingBox.getMinY(), (this.boundingBox.getMinZ() + this.boundingBox.getMaxZ()) / 2));
            this.onGround = this.isPlayer;
            return true;
        } else {
            this.ySize *= 0.4;

            double movX = dx;
            double movY = dy;
            double movZ = dz;

            AxisAlignedBB axisalignedbb = this.boundingBox.clone();

            AxisAlignedBB[] list = this.level.getCollisionCubes(this, this.boundingBox.addCoord(dx, dy, dz), false);

            for (AxisAlignedBB bb : list) {
                dy = bb.calculateYOffset(this.boundingBox, dy);
            }

            this.boundingBox.offset(0, dy, 0);

            boolean fallingFlag = (this.onGround || (dy != movY && movY < 0));

            for (AxisAlignedBB bb : list) {
                dx = bb.calculateXOffset(this.boundingBox, dx);
            }

            this.boundingBox.offset(dx, 0, 0);

            for (AxisAlignedBB bb : list) {
                dz = bb.calculateZOffset(this.boundingBox, dz);
            }

            this.boundingBox.offset(0, 0, dz);

            if (this.getStepHeight() > 0 && fallingFlag && this.ySize < 0.05 && (movX != dx || movZ != dz)) {
                double cx = dx;
                double cy = dy;
                double cz = dz;
                dx = movX;
                dy = this.getStepHeight();
                dz = movZ;

                AxisAlignedBB axisalignedbb1 = this.boundingBox.clone();

                this.boundingBox.setBB(axisalignedbb);

                list = this.level.getCollisionCubes(this, this.boundingBox.addCoord(dx, dy, dz), false);

                for (AxisAlignedBB bb : list) {
                    dy = bb.calculateYOffset(this.boundingBox, dy);
                }

                this.boundingBox.offset(0, dy, 0);

                for (AxisAlignedBB bb : list) {
                    dx = bb.calculateXOffset(this.boundingBox, dx);
                }

                this.boundingBox.offset(dx, 0, 0);

                for (AxisAlignedBB bb : list) {
                    dz = bb.calculateZOffset(this.boundingBox, dz);
                }

                this.boundingBox.offset(0, 0, dz);

                this.boundingBox.offset(0, 0, dz);

                if ((cx * cx + cz * cz) >= (dx * dx + dz * dz)) {
                    dx = cx;
                    dy = cy;
                    dz = cz;
                    this.boundingBox.setBB(axisalignedbb1);
                } else {
                    this.ySize += 0.5;
                }

            }

            this.x = (this.boundingBox.getMinX() + this.boundingBox.getMaxX()) / 2;
            this.y = this.boundingBox.getMinY() - this.ySize;
            this.z = (this.boundingBox.getMinZ() + this.boundingBox.getMaxZ()) / 2;

            this.checkChunks();

            this.checkGroundState(movX, movY, movZ, dx, dy, dz);
            this.updateFallState(this.onGround);

            if (movX != dx) {
                this.motionX = 0;
            }

            if (movY != dy) {
                this.motionY = 0;
            }

            if (movZ != dz) {
                this.motionZ = 0;
            }

            //TODO: vehicle collision events (first we need to spawn them!)
            return true;
        }
    }

    protected void checkGroundState(double movX, double movY, double movZ, double dx, double dy, double dz) {
        this.isCollidedVertically = movY != dy;
        this.isCollidedHorizontally = (movX != dx || movZ != dz);
        this.isCollided = (this.isCollidedHorizontally || this.isCollidedVertically);
        this.onGround = (movY != dy && movY < 0);
    }

    public List<Block> getBlocksAround() {
        if (this.blocksAround == null) {
            int minX = Mth.floor(this.boundingBox.getMinX());
            int minY = Mth.floor(this.boundingBox.getMinY());
            int minZ = Mth.floor(this.boundingBox.getMinZ());
            int maxX = Mth.floor(this.boundingBox.getMaxX());
            int maxY = Mth.floor(this.boundingBox.getMaxY());
            int maxZ = Mth.floor(this.boundingBox.getMaxZ());

            this.blocksAround = new ObjectArrayList<>();

            for (int z = minZ; z <= maxZ; ++z) {
                for (int x = minX; x <= maxX; ++x) {
                    for (int y = minY; y <= maxY; ++y) {
                        Block block = this.level.getBlock(x, y, z);
                        this.blocksAround.add(block);

                        if (!block.isAir() && (block.canContainWater() || block.canContainSnow())) {
                            Block extraBlock = level.getExtraBlock(x, y, z);
                            if (!extraBlock.isAir()) {
                                blocksAround.add(extraBlock);
                            }
                        }
                    }
                }
            }
        }

        return this.blocksAround;
    }

    public List<Block> getCollisionBlocks() {
        if (this.collisionBlocks == null) {
            this.collisionBlocks = new ObjectArrayList<>();

            for (Block b : getBlocksAround()) {
                if (b.collidesWithBB(this.getBoundingBox(), true)) {
                    this.collisionBlocks.add(b);
                }
            }
        }

        return this.collisionBlocks;
    }

    /**
     * Returns whether this entity can be moved by currents in liquids.
     *
     * @return boolean
     */
    public boolean canBeMovedByCurrents() {
        return true;
    }

    protected void checkBlockCollision() {
        Vector3 vector = new Vector3(0, 0, 0);
        boolean portal = false;

        for (Block block : this.getCollisionBlocks()) {
            if (block.getId() == Block.PORTAL) {
                portal = true;
                continue;
            }

            block.onEntityCollide(this);
            block.addVelocityToEntity(this, vector);
        }

        if (portal) {
            if (this.inPortalTicks < 80) {
                this.inPortalTicks = 80;
            } else {
                this.inPortalTicks++;
            }
        } else {
            this.inPortalTicks = 0;
        }

        if (vector.lengthSquared() > 0) {
            vector = vector.normalize();
            double d = 0.014d;
            this.motionX += vector.x * d;
            this.motionY += vector.y * d;
            this.motionZ += vector.z * d;
        }
    }

    public boolean setPositionAndRotation(Vector3 pos, double yaw, double pitch) {
        if (this.setPosition(pos)) {
            this.setRotation(yaw, pitch);
            return true;
        }

        return false;
    }

    public void setRotation(double yaw, double pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    /**
     * used for bat only
     */
    public boolean doesTriggerPressurePlate() {
        return true;
    }

    public boolean canPassThrough() {
        return true;
    }

    protected void checkChunks() {
        if (this.chunk == null || (this.chunk.getX() != ((int) this.x >> 4)) || this.chunk.getZ() != ((int) this.z >> 4)) {
            if (this.chunk != null) {
                this.chunk.removeEntity(this);
            }
            this.chunk = this.level.getChunk((int) this.x >> 4, (int) this.z >> 4, true);

            if (!this.justCreated) {
                Int2ObjectMap<Player> newChunk = this.level.getChunkPlayers((int) this.x >> 4, (int) this.z >> 4);
                for (Player player : new ObjectArrayList<>(this.hasSpawned.values())) {
                    if (!newChunk.containsKey(player.getLoaderId())) {
                        this.despawnFrom(player);
                    } else {
                        newChunk.remove(player.getLoaderId());
                    }
                }

                for (Player player : newChunk.values()) {
                    this.spawnTo(player);
                }
            }

            if (this.chunk == null) {
                return;
            }

            this.chunk.addEntity(this);
        }
    }

    public boolean setPosition(Vector3 pos) {
        if (this.closed) {
            return false;
        }

        if (pos instanceof Position && ((Position) pos).level != null && ((Position) pos).level != this.level) {
            if (!this.switchLevel(((Position) pos).getLevel())) {
                return false;
            }
        }

        this.x = pos.x;
        this.y = pos.y;
        this.z = pos.z;

        this.recalculateBoundingBox(false); // Don't need to send BB height/width to client on position change

        this.checkChunks();

        return true;
    }

    public Vector3 getMotion() {
        return new Vector3(this.motionX, this.motionY, this.motionZ);
    }

    public boolean setMotion(Vector3 motion) {
        if (!this.justCreated) {
            EntityMotionEvent ev = new EntityMotionEvent(this, motion);
            this.server.getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return false;
            }
        }

        this.motionX = motion.x;
        this.motionY = motion.y;
        this.motionZ = motion.z;

        if (!this.justCreated) {
            this.updateMovement();
        }

        return true;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void kill() {
        this.health = 0;
        this.scheduleUpdate();

//        for (Entity passenger : new ObjectArrayList<>(this.passengers)) {
//            dismountEntity(passenger);
//        }
    }

    public boolean teleport(Vector3 pos) {
        return this.teleport(pos, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    public boolean teleport(Vector3 pos, PlayerTeleportEvent.TeleportCause cause) {
        return this.teleport(Location.fromObject(pos, this.level, this.yaw, this.pitch), cause);
    }

    public boolean teleport(Position pos) {
        return this.teleport(pos, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    public boolean teleport(Position pos, PlayerTeleportEvent.TeleportCause cause) {
        return this.teleport(Location.fromObject(pos, pos.level, this.yaw, this.pitch), cause);
    }

    public boolean teleport(Location location) {
        return this.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    public boolean teleport(Location location, PlayerTeleportEvent.TeleportCause cause) {
        double yaw = location.yaw;
        double pitch = location.pitch;

        Location from = this.getLocation();
        Location to = location;
        if (cause != null) {
            EntityTeleportEvent ev = new EntityTeleportEvent(this, from, to);
            this.server.getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return false;
            }
            to = ev.getTo();
        }

        if (this.riding != null && !this.riding.dismountEntity(this)) {
            Utils.pauseInIde();
            return false;
        }

        this.ySize = 0;

        this.setMotion(this.temporalVector.setComponents(0, 0, 0));

        if (this.setPositionAndRotation(to, yaw, pitch)) {
            this.resetFallDistance();
            this.onGround = true;

            this.updateMovement();

            return true;
        }

        return false;
    }

    public final long getId() {
        return this.id;
    }

    public void respawnToAll() {
        for (Player player : this.hasSpawned.values()) {
            this.spawnTo(player);
        }
        //this.hasSpawned = new HashMap<>();
    }

    public void spawnToAll() {
        if (this.chunk == null || this.closed) {
            return;
        }

        for (Player player : this.level.getChunkPlayers(this.getChunkX(), this.getChunkZ()).values()) {
            if (player.isOnline()) {
                this.spawnTo(player);
            }
        }
    }

    public void despawnFromAll() {
        for (Player player : new ObjectArrayList<>(this.hasSpawned.values())) {
            this.despawnFrom(player);
        }
    }

    public void close() {
        if (!this.closed) {
            this.closed = true;
            this.server.getPluginManager().callEvent(new EntityDespawnEvent(this));
            this.despawnFromAll();
            if (this.chunk != null) {
                this.chunk.removeEntity(this);
            }

            if (this.level != null) {
                this.level.removeEntity(this);
            }
        }
    }

    public boolean setDataProperty(EntityData data) {
        return setDataProperty(data, true);
    }

    public boolean setDataProperty(EntityData data, boolean send) {
        if (Objects.equals(data, this.dataProperties.get(data.getId()))) {
            return false;
        }

        this.dataProperties.put(data);

        if (send) {
            EntityMetadata metadata = new EntityMetadata();
            metadata.put(data);
            if (data.getId() == DATA_FLAGS_EXTENDED) {
                EntityData<?> flags1 = this.dataProperties.get(DATA_FLAGS);
                if (flags1 != null) {
                    metadata.put(flags1);
                }
            }
            if (data.getId() == DATA_FLAGS) {
                // for multi-version conversion
                EntityData<?> flags2 = this.dataProperties.get(DATA_FLAGS_EXTENDED);
                if (flags2 != null) {
                    metadata.put(flags2);
                }
            }
            this.sendData(this.getViewers().values().toArray(new Player[0]), metadata);
        }

        return true;
    }

    public EntityMetadata getDataProperties() {
        return this.dataProperties;
    }

    public EntityData getDataProperty(int id) {
        return this.getDataProperties().get(id);
    }

    public int getDataPropertyInt(int id) {
        return this.getDataProperties().getInt(id);
    }

    public int getDataPropertyShort(int id) {
        return this.getDataProperties().getShort(id);
    }

    public int getDataPropertyByte(int id) {
        return this.getDataProperties().getByte(id);
    }

    public boolean getDataPropertyBoolean(int id) {
        return this.getDataProperties().getBoolean(id);
    }

    public long getDataPropertyLong(int id) {
        return this.getDataProperties().getLong(id);
    }

    public String getDataPropertyString(int id) {
        return this.getDataProperties().getString(id);
    }

    public float getDataPropertyFloat(int id) {
        return this.getDataProperties().getFloat(id);
    }

    public Item getDataPropertySlot(int id) {
        return this.getDataProperties().getSlot(id);
    }

    public CompoundTag getDataPropertyNBT(int id) {
        return this.getDataProperties().getNBT(id);
    }

    public BlockVector3 getDataPropertyPos(int id) {
        return this.getDataProperties().getPosition(id);
    }

    public Vector3f getDataPropertyVector3f(int id) {
        return this.getDataProperties().getFloatPosition(id);
    }

    public int getDataPropertyType(int id) {
        return this.getDataProperties().exists(id) ? this.getDataProperty(id).getType() : -1;
    }

    private static boolean getFlagBit(long flags, int flagId) {
        return (flags & (1L << flagId)) != 0;
    }

    private static boolean getFlagBit(int flags, int flagId) {
        return (flags & (1 << flagId)) != 0;
    }

    private static boolean getFlagBit(byte flags, int flagId) {
        return (flags & (1 << flagId)) != 0;
    }

    public boolean setDataFlag(int flagId, boolean value) {
        return this.setDataFlag(flagId, value, true);
    }

    public boolean setDataFlag(int flagId, boolean value, boolean send) {
        int propertyId = getFlagDataId(flagId);
        long flags = this.getDataPropertyLong(propertyId);
        if (getFlagBit(flags, flagId) == value) {
            return false;
        }
        flags ^= 1L << flagId;
        this.setDataProperty(new LongEntityData(propertyId, flags), send);
        return true;
    }

    public boolean getDataFlag(int flagId) {
        return getFlagBit(this.getDataPropertyLong(getFlagDataId(flagId)), flagId);
    }

    public static int getFlagDataId(int flagId) {
        return flagId >= 64 ? DATA_FLAGS_EXTENDED : DATA_FLAGS;
    }

    public boolean setPlayerFlag(int playerFlagId, boolean value) {
        return this.setPlayerFlag(playerFlagId, value, true);
    }

    public boolean setPlayerFlag(int playerFlagId, boolean value, boolean send) {
        byte flags = (byte) this.getDataPropertyByte(DATA_PLAYER_FLAGS);
        if (getFlagBit(flags, playerFlagId) == value) {
            return false;
        }
        flags ^= 1 << playerFlagId;
        this.setDataProperty(new ByteEntityData(DATA_PLAYER_FLAGS, flags), send);
        return true;
    }

    public boolean getPlayerFlag(int playerFlagId) {
        return getFlagBit(this.getDataPropertyByte(DATA_PLAYER_FLAGS), playerFlagId);
    }

    public EntityPropertiesInstance getProperties() {
        return properties;
    }

    public int getPropertyInt(int index) {
        return properties.getInt(index);
    }

    public int getPropertyInt(String name) {
        return properties.getInt(name);
    }

    public int getPropertyInt(EntityProperty property) {
        return properties.getInt(property);
    }

    public float getPropertyFloat(int index) {
        return properties.getFloat(index);
    }

    public float getPropertyFloat(String name) {
        return properties.getFloat(name);
    }

    public float getPropertyFloat(EntityProperty property) {
        return properties.getFloat(property);
    }

    public boolean getPropertyBoolean(int index) {
        return properties.getBoolean(index);
    }

    public boolean getPropertyBoolean(String name) {
        return properties.getBoolean(name);
    }

    public boolean getPropertyBoolean(EntityProperty property) {
        return properties.getBoolean(property);
    }

    public String getPropertyEnum(int index) {
        return properties.getEnum(index);
    }

    public String getPropertyEnum(String name) {
        return properties.getEnum(name);
    }

    public String getPropertyEnum(EntityProperty property) {
        return properties.getEnum(property);
    }

    public void setPropertyInt(int index, int value) {
        IntIntPair result = properties.setInt(index, value);
        if (result != null) {
            sendProperty(result.leftInt(), result.rightInt());
        }
    }

    public void setPropertyInt(String name, int value) {
        IntIntPair result = properties.setInt(name, value);
        if (result != null) {
            sendProperty(result.leftInt(), result.rightInt());
        }
    }

    public void setPropertyInt(EntityProperty property, int value) {
        IntIntPair result = properties.setInt(property, value);
        if (result != null) {
            sendProperty(result.leftInt(), result.rightInt());
        }
    }

    public void setPropertyFloat(int index, float value) {
        IntFloatPair result = properties.setFloat(index, value);
        if (result != null) {
            sendProperty(result.leftInt(), result.rightFloat());
        }
    }

    public void setPropertyFloat(String name, float value) {
        IntFloatPair result = properties.setFloat(name, value);
        if (result != null) {
            sendProperty(result.leftInt(), result.rightFloat());
        }
    }

    public void setPropertyFloat(EntityProperty property, float value) {
        IntFloatPair result = properties.setFloat(property, value);
        if (result != null) {
            sendProperty(result.leftInt(), result.rightFloat());
        }
    }

    public void setPropertyBoolean(int index, boolean value) {
        IntIntPair result = properties.setBoolean(index, value);
        if (result != null) {
            sendProperty(result.leftInt(), result.rightInt());
        }
    }

    public void setPropertyBoolean(String name, boolean value) {
        IntIntPair result = properties.setBoolean(name, value);
        if (result != null) {
            sendProperty(result.leftInt(), result.rightInt());
        }
    }

    public void setPropertyBoolean(EntityProperty property, boolean value) {
        IntIntPair result = properties.setBoolean(property, value);
        if (result != null) {
            sendProperty(result.leftInt(), result.rightInt());
        }
    }

    public void setPropertyEnum(int index, String value) {
        IntIntPair result = properties.setEnum(index, value);
        if (result != null) {
            sendProperty(result.leftInt(), result.rightInt());
        }
    }

    public void setPropertyEnum(String name, String value) {
        IntIntPair result = properties.setEnum(name, value);
        if (result != null) {
            sendProperty(result.leftInt(), result.rightInt());
        }
    }

    public void setPropertyEnum(EntityProperty property, String value) {
        IntIntPair result = properties.setEnum(property, value);
        if (result != null) {
            sendProperty(result.leftInt(), result.rightInt());
        }
    }

    private void sendProperty(int index, int value) {
        SetEntityDataPacket packet = new SetEntityDataPacket();
        packet.eid = getId();
        packet.metadata = new EntityMetadata();
        packet.intProperties.put(index, value);
        Server.broadcastPacket(getViewers().values(), packet);
        if (this instanceof Player player) {
            player.dataPacket(packet);
        }
    }

    private void sendProperty(int index, float value) {
        SetEntityDataPacket packet = new SetEntityDataPacket();
        packet.eid = getId();
        packet.metadata = new EntityMetadata();
        packet.floatProperties.put(index, value);
        Server.broadcastPacket(getViewers().values(), packet);
        if (this instanceof Player player) {
            player.dataPacket(packet);
        }
    }

    @Override
    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
        this.server.getEntityMetadata().setMetadata(this, metadataKey, newMetadataValue);
    }

    @Override
    public List<MetadataValue> getMetadata(String metadataKey) {
        return this.server.getEntityMetadata().getMetadata(this, metadataKey);
    }

    @Override
    public boolean hasMetadata(String metadataKey) {
        return this.server.getEntityMetadata().hasMetadata(this, metadataKey);
    }

    @Override
    public void removeMetadata(String metadataKey, Plugin owningPlugin) {
        this.server.getEntityMetadata().removeMetadata(this, metadataKey, owningPlugin);
    }

    public Server getServer() {
        return server;
    }

    public boolean isClosed() {
        return closed;
    }

    public boolean canPassThroughBarrier() {
        return false;
    }

    /**
     * @return tick count
     */
    public int getAge() {
        return age;
    }

    public boolean isBlocking() {
        return false;
    }

    public void blockedByShield(Entity blocker) {
    }

    public boolean canDisableShield() {
        return false;
    }

    public void resetFrozenState() {
        if (frozenTicks == 0) {
            return;
        }
        frozenTicks = 0;
        setDataProperty(new FloatEntityData(DATA_FREEZING_EFFECT_STRENGTH, 0));
    }

    public float getRidingOffset() {
        return 0;
    }

    public boolean isVanilla() {
        return true;
    }

    public void lookAt(Vector3 target) {
        lookAt(target.getX(), target.getY(), target.getZ());
    }

    public void lookAt(double x, double y, double z) {
        double deltaX = x - getX();
        double deltaZ = z - getZ();
        setRotation(Math.toDegrees(Mth.atan2(deltaZ, deltaX)) - 90, -Math.toDegrees(Mth.atan2(y - getEyeY(), Math.sqrt(deltaX * deltaX + deltaZ * deltaZ))));
    }

    public void broadcastEntityEvent(int event) {
        broadcastEntityEvent(event, 0);
    }

    public void broadcastEntityEvent(int event, Player... players) {
        broadcastEntityEvent(event, 0, players);
    }

    public void broadcastEntityEvent(int event, Collection<Player> players) {
        broadcastEntityEvent(event, 0, players);
    }

    public void broadcastEntityEvent(int event, int data) {
        level.broadcastEntityEvent(this, event, data);
    }

    public void broadcastEntityEvent(int event, int data, Player... players) {
        broadcastEntityEvent(event, data, Arrays.asList(players));
    }

    public void broadcastEntityEvent(int event, int data, Collection<Player> players) {
        EntityEventPacket packet = new EntityEventPacket();
        packet.event = event;
        packet.data = data;
        packet.eid = getId();
        Server.broadcastPacket(players, packet);
    }

    public boolean isChemistryFeature() {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Entity other = (Entity) obj;
        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(this.id);
    }

    @AllArgsConstructor
    @Value
    private static class EntityEntry {
        Class<? extends Entity> clazz;
        int type;
        String identifier;
        String shortName;
        String name;
        EntityFactory factory;
    }
}
