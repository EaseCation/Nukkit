package cn.nukkit.blockentity;

import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entities;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;

import javax.annotation.Nullable;

public class BlockEntityMobSpawner extends BlockEntitySpawnable {
    /**
     * int, legacy.
     */
    public static final String TAG_ENTITY_ID = "EntityId";
    /**
     * string.
     */
    public static final String TAG_ENTITY_IDENTIFIER = "EntityIdentifier";
    /**
     * ticks.
     * short.
     */
    public static final String TAG_DELAY = "Delay";
    /**
     * ticks.
     * short.
     */
    public static final String TAG_MIN_SPAWN_DELAY = "MinSpawnDelay";
    /**
     * ticks.
     * short.
     */
    public static final String TAG_MAX_SPAWN_DELAY = "MaxSpawnDelay";
    /**
     * short.
     */
    public static final String TAG_SPAWN_COUNT = "SpawnCount";
    /**
     * short.
     */
    public static final String TAG_MAX_NEARBY_ENTITIES = "MaxNearbyEntities";
    /**
     * short.
     */
    public static final String TAG_REQUIRED_PLAYER_RANGE = "RequiredPlayerRange";
    /**
     * block range.
     * short.
     */
    public static final String TAG_SPAWN_RANGE = "SpawnRange";
    /**
     * float.
     */
    public static final String TAG_DISPLAY_ENTITY_WIDTH = "DisplayEntityWidth";
    /**
     * float.
     */
    public static final String TAG_DISPLAY_ENTITY_HEIGHT = "DisplayEntityHeight";
    /**
     * float.
     */
    public static final String TAG_DISPLAY_ENTITY_SCALE = "DisplayEntityScale";
    /**
     * compound, optional.
     */
    public static final String TAG_SPAWN_DATA = "SpawnData";
    /**
     * list(compound), optional.
     */
    public static final String TAG_SPAWN_POTENTIALS = "SpawnPotentials";

    public static final int DEFAULT_MIN_SPAWN_DELAY = 200;
    public static final int DEFAULT_MAX_SPAWN_DELAY = 800;
    public static final int DEFAULT_MAX_NEARBY_ENTITIES = 6;
    public static final int DEFAULT_SPAWN_RANGE = 4;
    public static final int DEFAULT_REQUIRED_PLAYER_RANGE = 16;

    /**
     * legacy entity type ID.
     */
    protected int entityType;
    protected String entityIdentifier;
    protected int delay;
    protected int minSpawnDelay;
    protected int maxSpawnDelay;
    protected int spawnCount;
    protected int maxNearbyEntities;
    protected int requiredPlayerRange;
    protected int spawnRange;
    protected float displayEntityWidth;
    protected float displayEntityHeight;
    protected float displayEntityScale;
    @Nullable
    protected CompoundTag spawnData; //TODO: deserialize me
    @Nullable
    protected ListTag<CompoundTag> spawnPotentials; //TODO: deserialize me

    public BlockEntityMobSpawner(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getTypeId() {
        return BlockEntityType.MOB_SPAWNER;
    }

    @Override
    protected void initBlockEntity() {
        if (namedTag.contains(TAG_ENTITY_ID)) {
            Tag tag = namedTag.get(TAG_ENTITY_ID);
            if (tag instanceof IntTag) {
                entityType = namedTag.getInt(TAG_ENTITY_ID);
            }
            String identifier = Entities.getIdentifierByType(entityType);
            if (identifier == null) {
                entityIdentifier = ":";
                entityType = 0;
            } else {
                entityIdentifier = identifier;
            }
        } else if (namedTag.contains(TAG_ENTITY_IDENTIFIER)) {
            entityIdentifier = namedTag.getString(TAG_ENTITY_IDENTIFIER);
            entityType = Entities.getTypeByIdentifier(entityIdentifier);
        } else {
            entityIdentifier = ":";
            entityType = 0;
        }

        if (namedTag.contains(TAG_MIN_SPAWN_DELAY)) {
            minSpawnDelay = namedTag.getShort(TAG_MIN_SPAWN_DELAY);
        } else {
            minSpawnDelay = DEFAULT_MIN_SPAWN_DELAY;
        }
        if (namedTag.contains(TAG_MAX_SPAWN_DELAY)) {
            maxSpawnDelay = namedTag.getShort(TAG_MAX_SPAWN_DELAY);
        } else {
            maxSpawnDelay = DEFAULT_MAX_SPAWN_DELAY;
        }
        if (namedTag.contains(TAG_DELAY)) {
            delay = namedTag.getShort(TAG_DELAY);
        } else {
            delay = maxSpawnDelay;
        }
        if (namedTag.contains(TAG_SPAWN_COUNT)) {
            spawnCount = namedTag.getShort(TAG_SPAWN_COUNT);
        } else {
            spawnCount = 1;
        }
        if (namedTag.contains(TAG_MAX_NEARBY_ENTITIES)) {
            maxNearbyEntities = namedTag.getShort(TAG_MAX_NEARBY_ENTITIES);
        } else {
            maxNearbyEntities = DEFAULT_MAX_NEARBY_ENTITIES;
        }
        if (namedTag.contains(TAG_REQUIRED_PLAYER_RANGE)) {
            requiredPlayerRange = namedTag.getShort(TAG_REQUIRED_PLAYER_RANGE);
        } else {
            requiredPlayerRange = DEFAULT_REQUIRED_PLAYER_RANGE;
        }
        if (namedTag.contains(TAG_SPAWN_RANGE)) {
            spawnRange = namedTag.getShort(TAG_SPAWN_RANGE);
        } else {
            spawnRange = DEFAULT_SPAWN_RANGE;
        }
        if (namedTag.contains(TAG_DISPLAY_ENTITY_WIDTH)) {
            displayEntityWidth = namedTag.getFloat(TAG_DISPLAY_ENTITY_WIDTH);
        } else {
            displayEntityWidth = 1;
        }
        if (namedTag.contains(TAG_DISPLAY_ENTITY_HEIGHT)) {
            displayEntityHeight = namedTag.getFloat(TAG_DISPLAY_ENTITY_HEIGHT);
        } else {
            displayEntityHeight = 1;
        }
        if (namedTag.contains(TAG_DISPLAY_ENTITY_SCALE)) {
            displayEntityScale = namedTag.getFloat(TAG_DISPLAY_ENTITY_SCALE);
        } else {
            displayEntityScale = 1;
        }
        if (namedTag.contains(TAG_SPAWN_DATA)) {
            spawnData = namedTag.getCompound(TAG_SPAWN_DATA);
        } else {
            spawnData = null;
        }
        if (namedTag.contains(TAG_SPAWN_POTENTIALS)) {
            spawnPotentials = namedTag.getList(TAG_SPAWN_POTENTIALS, CompoundTag.class);
        } else {
            spawnPotentials = null;
        }

        super.initBlockEntity();

//        scheduleUpdate();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        namedTag.putString(TAG_ENTITY_IDENTIFIER, entityIdentifier);
        namedTag.putShort(TAG_DELAY, delay);
        namedTag.putShort(TAG_MIN_SPAWN_DELAY, minSpawnDelay);
        namedTag.putShort(TAG_MAX_SPAWN_DELAY, maxSpawnDelay);
        namedTag.putShort(TAG_SPAWN_COUNT, spawnCount);
        namedTag.putShort(TAG_MAX_NEARBY_ENTITIES, maxNearbyEntities);
        namedTag.putShort(TAG_REQUIRED_PLAYER_RANGE, requiredPlayerRange);
        namedTag.putShort(TAG_SPAWN_RANGE, spawnRange);
        namedTag.putFloat(TAG_DISPLAY_ENTITY_WIDTH, displayEntityWidth);
        namedTag.putFloat(TAG_DISPLAY_ENTITY_HEIGHT, displayEntityHeight);
        namedTag.putFloat(TAG_DISPLAY_ENTITY_SCALE, displayEntityScale);
        if (spawnData != null) {
            namedTag.putCompound(TAG_SPAWN_DATA, spawnData);
        }
        if (spawnPotentials != null) {
            namedTag.putList(spawnPotentials);
        }
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == BlockID.MOB_SPAWNER;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag nbt = getDefaultCompound(this, MOB_SPAWNER)
                .putString(TAG_ENTITY_IDENTIFIER, entityIdentifier)
                .putShort(TAG_DELAY, delay)
                .putShort(TAG_MIN_SPAWN_DELAY, minSpawnDelay)
                .putShort(TAG_MAX_SPAWN_DELAY, maxSpawnDelay)
                .putShort(TAG_SPAWN_COUNT, spawnCount)
                .putShort(TAG_MAX_NEARBY_ENTITIES, maxNearbyEntities)
                .putShort(TAG_REQUIRED_PLAYER_RANGE, requiredPlayerRange)
                .putShort(TAG_SPAWN_RANGE, spawnRange)
                .putFloat(TAG_DISPLAY_ENTITY_WIDTH, displayEntityWidth)
                .putFloat(TAG_DISPLAY_ENTITY_HEIGHT, displayEntityHeight)
                .putFloat(TAG_DISPLAY_ENTITY_SCALE, displayEntityScale);
        if (spawnData != null) {
            nbt.putCompound(TAG_SPAWN_DATA, spawnData);
        }
        if (spawnPotentials != null) {
            nbt.putList(spawnPotentials);
        }
        return nbt;
    }

    public boolean setEntityType(int type) {
        if (type == entityType) {
            return true;
        }
        boolean success;

        String identifier = Entities.getIdentifierByType(type);
        if (identifier == null) {
            entityIdentifier = ":";
            entityType = 0;
            success = false;
        } else {
            entityIdentifier = identifier;
            entityType = type;
            success = true;
        }

        setDirty();
        return success;
    }

    @Override
    public boolean onUpdate() {
        if (isClosed()) {
            return false;
        }

        int currentTick = server.getTick();
        int tickDiff = currentTick - lastUpdate;
        if (tickDiff <= 0) {
            return true;
        }
        lastUpdate = currentTick;

        //TODO
        return true;
    }
}
