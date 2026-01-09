package cn.nukkit.blockentity;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockTrialSpawner;
import cn.nukkit.entity.Entities;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.loot.LootTableNames;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import it.unimi.dsi.fastutil.ints.IntObjectPair;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import lombok.ToString;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class BlockEntityTrialSpawner extends BlockEntitySpawnable {
    /**
     * compound.
     */
    private static final String NORMAL_CONFIG_TAG = "normal_config";
    /**
     * compound.
     */
    private static final String OMINOUS_CONFIG_TAG = "ominous_config";
    /**
     * long. (Tick)
     */
    private static final String COOLDOWN_END_AT_TAG = "cooldown_end_at";
    /**
     * long. (Tick)
     */
    private static final String NEXT_MOB_SPAWNS_AT_TAG = "next_mob_spawns_at";
    /**
     * compound.
     */
    private static final String SPAWN_DATA_TAG = "spawn_data";
    /**
     * compound list.
     */
    private static final String CURRENT_MOBS_TAG = "current_mobs";
    /**
     * string. (LootTable)
     */
    private static final String SELECTED_LOOT_TABLE_TAG = "selected_loot_table";
    /**
     * int.
     */
    private static final String REQUIRED_PLAYER_RANGE_TAG = "required_player_range";
    /**
     * compound list.
     */
    private static final String REGISTERED_PLAYERS_TAG = "registered_players";

    /**
     * long.
     */
    private static final String UUID_TAG = "uuid";

    private SpawnConfig normalConfig;
    private SpawnConfig ominousConfig;

    private long cooldownEndAt;
    private long nextMobSpawnsAt;
    @Nullable
    private SpawnData spawnData;
    private LongSet currentMobs;

    private String selectedLootTable;

    private int requiredPlayerRange;
    private LongSet registeredPlayers;

    public BlockEntityTrialSpawner(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getTypeId() {
        return BlockEntityType.TRIAL_SPAWNER;
    }

    @Override
    protected void initBlockEntity() {
        normalConfig = new SpawnConfig();
        ominousConfig = new SpawnConfig();
        currentMobs = new LongOpenHashSet();
        registeredPlayers = new LongOpenHashSet();

        normalConfig.load(namedTag.getCompound(NORMAL_CONFIG_TAG), false);
        ominousConfig.load(namedTag.getCompound(OMINOUS_CONFIG_TAG), true);

        cooldownEndAt = namedTag.getLong(COOLDOWN_END_AT_TAG);
        nextMobSpawnsAt = namedTag.getLong(NEXT_MOB_SPAWNS_AT_TAG);

        if (namedTag.contains(SPAWN_DATA_TAG)) {
            SpawnData spawnData = new SpawnData();
            if (spawnData.load(namedTag.getCompound(SPAWN_DATA_TAG))) {
                this.spawnData = spawnData;
            }
        }

        if (namedTag.contains(CURRENT_MOBS_TAG)) {
            ListTag<CompoundTag> mobs = namedTag.getList(CURRENT_MOBS_TAG, CompoundTag.class);
            for (CompoundTag mob : mobs) {
                currentMobs.add(mob.getLong(UUID_TAG));
            }
        }

        if (namedTag.contains(SELECTED_LOOT_TABLE_TAG)) {
            selectedLootTable = namedTag.getString(SELECTED_LOOT_TABLE_TAG);
        } else {
            Block block = getLevelBlock();
            if (block.is(Block.TRIAL_SPAWNER) && (block.getDamage() & BlockTrialSpawner.OMINOUS_BIT) != 0) {
                if (!ominousConfig.lootTablesToEject.isEmpty()) {
                    selectedLootTable = ominousConfig.lootTablesToEject.get(ThreadLocalRandom.current().nextInt(ominousConfig.lootTablesToEject.size())).right();
                } else {
                    selectedLootTable = LootTableNames.SPAWNERS_OMINOUS_TRIAL_CHAMBER_KEY;
                }
            } else if (!normalConfig.lootTablesToEject.isEmpty()) {
                selectedLootTable = normalConfig.lootTablesToEject.get(ThreadLocalRandom.current().nextInt(normalConfig.lootTablesToEject.size())).right();
            } else {
                selectedLootTable = LootTableNames.SPAWNERS_TRIAL_CHAMBER_KEY;
            }
        }

        requiredPlayerRange = namedTag.getInt(REQUIRED_PLAYER_RANGE_TAG, 14);

        if (namedTag.contains(REGISTERED_PLAYERS_TAG)) {
            ListTag<CompoundTag> players = namedTag.getList(REGISTERED_PLAYERS_TAG, CompoundTag.class);
            for (CompoundTag player : players) {
                registeredPlayers.add(player.getLong(UUID_TAG));
            }
        }

        super.initBlockEntity();

//        scheduleUpdate();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        CompoundTag normalConfig = new CompoundTag();
        this.normalConfig.save(normalConfig);
        namedTag.putCompound(NORMAL_CONFIG_TAG, normalConfig);

        CompoundTag ominousConfig = new CompoundTag();
        this.ominousConfig.save(ominousConfig);
        namedTag.putCompound(OMINOUS_CONFIG_TAG, ominousConfig);

        namedTag.putLong(COOLDOWN_END_AT_TAG, cooldownEndAt);
        namedTag.putLong(NEXT_MOB_SPAWNS_AT_TAG, nextMobSpawnsAt);

        if (spawnData != null) {
            CompoundTag spawnData = new CompoundTag();
            this.spawnData.save(spawnData);
            namedTag.putCompound(SPAWN_DATA_TAG, spawnData);
        } else  {
            namedTag.remove(SPAWN_DATA_TAG);
        }

        ListTag<CompoundTag> mobs = new ListTag<>();
        for (long mobId : currentMobs) {
            mobs.add(new CompoundTag().putLong(UUID_TAG, mobId));
        }
        namedTag.putList(CURRENT_MOBS_TAG, mobs);

        namedTag.putString(SELECTED_LOOT_TABLE_TAG, selectedLootTable);

        namedTag.putInt(REQUIRED_PLAYER_RANGE_TAG, requiredPlayerRange);

        ListTag<CompoundTag> players = new ListTag<>();
        for (long playerId : registeredPlayers) {
            players.add(new CompoundTag().putLong(UUID_TAG, playerId));
        }
        namedTag.putList(REGISTERED_PLAYERS_TAG, players);
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == Block.TRIAL_SPAWNER;
    }

    @Override
    public CompoundTag getSpawnCompound(boolean chunkData) {
        CompoundTag nbt = chunkData ? getDefaultCompound(this, TRIAL_SPAWNER) : new CompoundTag();

        if (spawnData != null) {
            CompoundTag spawnData = new CompoundTag();
            this.spawnData.save(spawnData);
            nbt.putCompound(SPAWN_DATA_TAG, spawnData);
        }

        return nbt;
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

    public SpawnConfig getNormalConfig() {
        return normalConfig;
    }

    public SpawnConfig getOminousConfig() {
        return ominousConfig;
    }

    public long getCooldownEndAt() {
        return cooldownEndAt;
    }

    public void setCooldownEndAt(long tick) {
        cooldownEndAt = tick;
    }

    public long getNextMobSpawnsAt() {
        return nextMobSpawnsAt;
    }

    public void setNextMobSpawnsAt(long tick) {
        nextMobSpawnsAt = tick;
    }

    @Nullable
    public SpawnData getSpawnData() {
        return spawnData;
    }

    public void setSpawnData(@Nullable SpawnData data) {
        spawnData = data;
    }

    public LongSet getCurrentMobs() {
        return currentMobs;
    }

    public String getSelectedLootTable() {
        return selectedLootTable;
    }

    public void setSelectedLootTable(String lootTable) {
        selectedLootTable = lootTable;
    }

    public int getRequiredPlayerRange() {
        return requiredPlayerRange;
    }

    public void setRequiredPlayerRange(int range) {
        requiredPlayerRange = range;
    }

    public LongSet getRegisteredPlayers() {
        return registeredPlayers;
    }

    @ToString
    public static class SpawnConfig {
        /**
         * int. (Tick)
         */
        static final String TARGET_COOLDOWN_LENGTH_TAG = "target_cooldown_length";
        /**
         * compound list.
         */
        static final String SPAWN_POTENTIALS_TAG = "spawn_potentials";
        /**
         * int.
         */
        static final String SPAWN_RANGE_TAG = "spawn_range";
        /**
         * int. (Tick)
         */
        static final String TICKS_BETWEEN_SPAWN_TAG = "ticks_between_spawn";
        /**
         * float.
         */
        static final String TOTAL_MOBS_TAG = "total_mobs";
        /**
         * float.
         */
        static final String TOTAL_MOBS_ADDED_PER_PLAYER_TAG = "total_mobs_added_per_player";
        /**
         * float.
         */
        static final String SIMULTANEOUS_MOBS_TAG = "simultaneous_mobs";
        /**
         * float.
         */
        static final String SIMULTANEOUS_MOBS_ADDED_PER_PLAYER_TAG = "simultaneous_mobs_added_per_player";
        /**
         * string. (LootTable)
         */
        static final String ITEMS_TO_DROP_WHEN_OMINOUS_TAG = "items_to_drop_when_ominous";
        /**
         * compound list.
         */
        static final String LOOT_TABLES_TO_EJECT_TAG = "loot_tables_to_eject";

        /**
         * int.
         */
        static final String WEIGHT_TAG = "weight";
        /**
         * string.
         */
        static final String DATA_TAG = "data";

        private int targetCooldownLength = 30 * 60 * Server.TPS;
        private final List<SpawnData> spawnPotentials = new ArrayList<>();
        private int spawnRange = 4;
        private int ticksBetweenSpawn = 20;
        private float totalMobs = 6;
        private float totalMobsAddedPerPlayer = 2;
        private float simultaneousMobs = 2;
        private float simultaneousMobsAddedPerPlayer = 1;
        private String lootTableToDropWhenOminous = LootTableNames.SPAWNERS_TRIAL_CHAMBER_ITEMS_TO_DROP_WHEN_OMINOUS;
        private final List<IntObjectPair<String>> lootTablesToEject = new ArrayList<>();

        public void load(CompoundTag tag, boolean ominous) {
            if (tag.contains(TARGET_COOLDOWN_LENGTH_TAG)) {
                targetCooldownLength = tag.getInt(TARGET_COOLDOWN_LENGTH_TAG);
            }

            if (tag.contains(SPAWN_POTENTIALS_TAG)) {
                ListTag<CompoundTag> potentials = tag.getList(SPAWN_POTENTIALS_TAG, CompoundTag.class);
                for (CompoundTag potential : potentials) {
                    SpawnData data = new SpawnData();
                    if (data.loadPotential(potential)) {
                        spawnPotentials.add(data);
                    }
                }
            }

            if (tag.contains(SPAWN_RANGE_TAG)) {
                spawnRange = tag.getInt(SPAWN_RANGE_TAG);
            }
            if (tag.contains(TICKS_BETWEEN_SPAWN_TAG)) {
                ticksBetweenSpawn = tag.getInt(TICKS_BETWEEN_SPAWN_TAG);
            }

            if (tag.contains(TOTAL_MOBS_TAG)) {
                totalMobs = tag.getFloat(TOTAL_MOBS_TAG);
            }
            if (tag.contains(TOTAL_MOBS_ADDED_PER_PLAYER_TAG)) {
                totalMobsAddedPerPlayer = tag.getFloat(TOTAL_MOBS_ADDED_PER_PLAYER_TAG);
            }

            if (tag.contains(SIMULTANEOUS_MOBS_TAG)) {
                simultaneousMobs = tag.getFloat(SIMULTANEOUS_MOBS_TAG);
            }
            if (tag.contains(SIMULTANEOUS_MOBS_ADDED_PER_PLAYER_TAG)) {
                simultaneousMobsAddedPerPlayer = tag.getFloat(SIMULTANEOUS_MOBS_ADDED_PER_PLAYER_TAG);
            }

            if (tag.contains(ITEMS_TO_DROP_WHEN_OMINOUS_TAG)) {
                lootTableToDropWhenOminous = tag.getString(ITEMS_TO_DROP_WHEN_OMINOUS_TAG);
            }

            if (tag.contains(LOOT_TABLES_TO_EJECT_TAG)) {
                ListTag<CompoundTag> lootTables = tag.getList(LOOT_TABLES_TO_EJECT_TAG, CompoundTag.class);
                for (CompoundTag lootTable : lootTables) {
                    lootTablesToEject.add(IntObjectPair.of(lootTable.getInt(WEIGHT_TAG, 1), lootTable.getString(DATA_TAG)));
                }
            } else if (ominous) {
                lootTablesToEject.add(IntObjectPair.of(1, LootTableNames.SPAWNERS_OMINOUS_TRIAL_CHAMBER_KEY));
                lootTablesToEject.add(IntObjectPair.of(1, LootTableNames.SPAWNERS_OMINOUS_TRIAL_CHAMBER_CONSUMABLES));
            } else {
                lootTablesToEject.add(IntObjectPair.of(1, LootTableNames.SPAWNERS_TRIAL_CHAMBER_KEY));
                lootTablesToEject.add(IntObjectPair.of(1, LootTableNames.SPAWNERS_TRIAL_CHAMBER_CONSUMABLES));
            }
        }

        public void save(CompoundTag tag) {
            tag.putInt(TARGET_COOLDOWN_LENGTH_TAG, targetCooldownLength);

            if (!spawnPotentials.isEmpty()) {
                ListTag<CompoundTag> potentials = new ListTag<>();
                for (SpawnData data : spawnPotentials) {
                    CompoundTag potential = new CompoundTag();
                    data.savePotential(potential);
                    potentials.add(potential);
                }
                tag.putList(SPAWN_POTENTIALS_TAG, potentials);
            } else {
                tag.remove(SPAWN_POTENTIALS_TAG);
            }

            tag.putInt(SPAWN_RANGE_TAG, spawnRange);

            tag.putInt(TICKS_BETWEEN_SPAWN_TAG, ticksBetweenSpawn);

            tag.putFloat(TOTAL_MOBS_TAG, totalMobs);
            tag.putFloat(TOTAL_MOBS_ADDED_PER_PLAYER_TAG, totalMobsAddedPerPlayer);

            tag.putFloat(SIMULTANEOUS_MOBS_TAG, simultaneousMobs);
            tag.putFloat(SIMULTANEOUS_MOBS_ADDED_PER_PLAYER_TAG, simultaneousMobsAddedPerPlayer);

            if (lootTableToDropWhenOminous != null) {
                tag.putString(ITEMS_TO_DROP_WHEN_OMINOUS_TAG, lootTableToDropWhenOminous);
            }

            ListTag<CompoundTag> lootTables = new ListTag<>();
            if (!lootTablesToEject.isEmpty()) {
                for (IntObjectPair<String> lootTable : lootTablesToEject) {
                    CompoundTag data = new CompoundTag();
                    data.putInt(WEIGHT_TAG, lootTable.leftInt());
                    data.putString(DATA_TAG, lootTable.right());
                    lootTables.add(data);
                }
            }
            tag.putList(LOOT_TABLES_TO_EJECT_TAG, lootTables);
        }

        public int getTargetCooldownLength() {
            return targetCooldownLength;
        }

        public void setTargetCooldownLength(int ticks) {
            targetCooldownLength = ticks;
        }

        public List<SpawnData> getSpawnPotentials() {
            return spawnPotentials;
        }

        public int getSpawnRange() {
            return spawnRange;
        }

        public void setSpawnRange(int range) {
            spawnRange = range;
        }

        public int getTicksBetweenSpawn() {
            return ticksBetweenSpawn;
        }

        public void setTicksBetweenSpawn(int ticks) {
            ticksBetweenSpawn = ticks;
        }

        public float getTotalMobs() {
            return totalMobs;
        }

        public void setTotalMobs(float num) {
            totalMobs = num;
        }

        public float getTotalMobsAddedPerPlayer() {
            return totalMobsAddedPerPlayer;
        }

        public void setTotalMobsAddedPerPlayer(float num) {
            totalMobsAddedPerPlayer = num;
        }

        public float getSimultaneousMobs() {
            return simultaneousMobs;
        }

        public void setSimultaneousMobs(float num) {
            simultaneousMobs = num;
        }

        public float getSimultaneousMobsAddedPerPlayer() {
            return simultaneousMobsAddedPerPlayer;
        }

        public void setSimultaneousMobsAddedPerPlayer(float num) {
            simultaneousMobsAddedPerPlayer = num;
        }

        public String getLootTableToDropWhenOminous() {
            return lootTableToDropWhenOminous;
        }

        public void setLootTableToDropWhenOminous(String lootTable) {
            lootTableToDropWhenOminous = lootTable;
        }

        public List<IntObjectPair<String>> getLootTablesToEject() {
            return lootTablesToEject;
        }
    }

    @ToString
    public static class SpawnData {
        /**
         * int.
         */
        static final String WEIGHT_TAG = "Weight";
        /**
         * string. (ActorIdentifier)
         */
        static final String TYPE_ID_TAG = "TypeId";
        /**
         * compound.
         */
        static final String EQUIPMENT_TAG = "equipment";

        /**
         * compound.
         */
        static final String DATA_TAG = "data";
        /**
         * compound.
         */
        static final String ENTITY_TAG = "entity";
        /**
         * string. (ActorIdentifier)
         */
        static final String ID_TAG = "id";

        private int weight = 1;
        private int entityType;
        private String entityIdentifier;
        @Nullable
        private SpawnEquipment equipment;

        public boolean load(CompoundTag tag) {
            if (tag.contains(WEIGHT_TAG)) {
                weight = tag.getInt(WEIGHT_TAG);
            }

            if (tag.contains(TYPE_ID_TAG)) {
                entityIdentifier = tag.getString(TYPE_ID_TAG);
                entityType = Entities.getTypeByIdentifier(entityIdentifier);
            }

            if (tag.contains(EQUIPMENT_TAG)) {
                equipment = new SpawnEquipment();
                equipment.load(tag.getCompound(EQUIPMENT_TAG));
            }

            return entityIdentifier != null;
        }

        public void save(CompoundTag tag) {
            tag.putInt(WEIGHT_TAG, weight);

            if (entityIdentifier != null) {
                tag.putString(TYPE_ID_TAG, entityIdentifier);
            }

            if (equipment != null) {
                CompoundTag equipment = new CompoundTag();
                this.equipment.save(equipment);
                tag.putCompound(EQUIPMENT_TAG, equipment);
            } else {
                tag.remove(EQUIPMENT_TAG);
            }
        }

        private boolean loadPotential(CompoundTag tag) {
            if (tag.contains(WEIGHT_TAG)) {
                weight = tag.getInt(WEIGHT_TAG);
            }

            if (tag.contains(DATA_TAG)) {
                CompoundTag data = tag.getCompound(DATA_TAG);
                if (data.contains(ENTITY_TAG)) {
                    CompoundTag entity = data.getCompound(ENTITY_TAG);
                    if (entity.contains(ID_TAG)) {
                        entityIdentifier = entity.getString(ID_TAG);
                        entityType = Entities.getTypeByIdentifier(entityIdentifier);
                    }
                }

                if (data.contains(EQUIPMENT_TAG)) {
                    equipment = new SpawnEquipment();
                    equipment.load(data.getCompound(EQUIPMENT_TAG));
                }
            }

            return entityIdentifier != null;
        }

        private void savePotential(CompoundTag tag) {
            tag.putInt(WEIGHT_TAG, weight);

            CompoundTag data = new CompoundTag();

            if (entityIdentifier != null) {
                data.putCompound(ENTITY_TAG, new CompoundTag()
                        .putString(ID_TAG, entityIdentifier));
            }

            if (equipment != null) {
                CompoundTag equipment = new CompoundTag();
                this.equipment.save(equipment);
                data.putCompound(EQUIPMENT_TAG, equipment);
            }

            tag.putCompound(DATA_TAG, data);
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

        public int getEntityType() {
            return entityType;
        }

        public void setEntityType(int entityType) {
            this.entityType = entityType;
            entityIdentifier = Entities.getIdentifierByType(entityType);
        }

        public String getEntityIdentifier() {
            return entityIdentifier;
        }

        public void setEntityIdentifier(String entityIdentifier) {
            this.entityIdentifier = entityIdentifier;
            entityType = Entities.getTypeByIdentifier(entityIdentifier);
        }

        @Nullable
        public SpawnEquipment getEquipment() {
            return equipment;
        }

        public void setEquipment(@Nullable SpawnEquipment equipment) {
            this.equipment = equipment;
        }

        @ToString
        public static class SpawnEquipment {
            /**
             * string. (LootTable)
             */
            static final String LOOT_TABLE_TAG = "loot_table";
            /**
             * compound.
             */
            static final String SLOT_DROP_CHANCES_TAG = "slot_drop_chances";

            private String equipmentLootTable;
            private final Map<EquipmentSlot, Float> slotDropChances = new EnumMap<>(EquipmentSlot.class);

            public void load(CompoundTag tag) {
                if (tag.contains(LOOT_TABLE_TAG)) {
                    equipmentLootTable = tag.getString(LOOT_TABLE_TAG);
                }

                if (tag.contains(SLOT_DROP_CHANCES_TAG)) {
                    CompoundTag chances = tag.getCompound(SLOT_DROP_CHANCES_TAG);
                    for (EquipmentSlot slot : EquipmentSlot.VALUES) {
                        if (chances.contains(slot.name)) {
                            slotDropChances.put(slot, chances.getFloat(slot.name));
                        }
                    }
                }
            }

            public void save(CompoundTag tag) {
                if (equipmentLootTable != null) {
                    tag.putString(LOOT_TABLE_TAG, equipmentLootTable);
                }

                if (!slotDropChances.isEmpty()) {
                    CompoundTag chances = new CompoundTag();
                    for (Map.Entry<EquipmentSlot, Float> entry : slotDropChances.entrySet()) {
                        chances.putFloat(entry.getKey().name, entry.getValue());
                    }
                    tag.putCompound(SLOT_DROP_CHANCES_TAG, chances);
                } else {
                    tag.remove(SLOT_DROP_CHANCES_TAG);
                }
            }

            public String getEquipmentLootTable() {
                return equipmentLootTable;
            }

            public void setEquipmentLootTable(String equipmentLootTable) {
                this.equipmentLootTable = equipmentLootTable;
            }

            public Map<EquipmentSlot, Float> getSlotDropChances() {
                return slotDropChances;
            }

            public enum EquipmentSlot {
                HEAD("head"),
                CHEST("chest"),
                LEGS("legs"),
                FEET("feet"),
                MAINHAND("mainhand"),
                OFFHAND("offhand"),
                ;

                private static final EquipmentSlot[] VALUES = values();

                private final String name;

                EquipmentSlot(String name) {
                    this.name = name;
                }
            }
        }
    }
}
