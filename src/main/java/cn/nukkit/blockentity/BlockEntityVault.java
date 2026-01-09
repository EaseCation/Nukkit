package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockVault;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.loot.LootTableNames;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.LongTag;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

public class BlockEntityVault extends BlockEntitySpawnable {
    /**
     * compound.
     */
    private static final String CONFIG_TAG = "config";
    /**
     * compound.
     */
    private static final String DATA_TAG = "data";

    /**
     * compound. (Item)
     */
    private static final String DISPLAY_ITEM_TAG = "display_item";
    /**
     * float.
     */
    private static final String CONNECTED_PARTICLE_RANGE_TAG = "connected_particle_range";
    /**
     * long list. (EntityUniqueIDs)
     */
    private static final String CONNECTED_PLAYERS_TAG = "connected_players";

    private VaultConfig vaultConfig;
    private VaultData vaultData;

    private Item displayItem;
    private float connectedParticleRange;
    private LongSet connectedPlayers;

    public BlockEntityVault(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getTypeId() {
        return BlockEntityType.VAULT;
    }

    @Override
    protected void initBlockEntity() {
        vaultConfig = new VaultConfig();
        vaultData = new VaultData();
        connectedPlayers = new LongOpenHashSet();

        vaultConfig.load(namedTag.getCompound(CONFIG_TAG));
        vaultData.load(namedTag.getCompound(DATA_TAG));

        connectedParticleRange = namedTag.getFloat(CONNECTED_PARTICLE_RANGE_TAG, 4.5f);

        super.initBlockEntity();

//        scheduleUpdate();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        CompoundTag vaultConfig = new CompoundTag();
        this.vaultConfig.save(vaultConfig);
        namedTag.putCompound(CONFIG_TAG, vaultConfig);

        CompoundTag vaultData = new CompoundTag();
        this.vaultData.save(vaultData);
        namedTag.putCompound(DATA_TAG, vaultData);
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == Block.VAULT;
    }

    @Override
    public CompoundTag getSpawnCompound(boolean chunkData) {
        CompoundTag nbt = chunkData ? getDefaultCompound(this, VAULT) : new CompoundTag();

        nbt.putCompound(DISPLAY_ITEM_TAG, NBTIO.putItemHelper(displayItem));

        nbt.putFloat(CONNECTED_PARTICLE_RANGE_TAG, connectedParticleRange);

        ListTag<LongTag> players = new ListTag<>();
        for (long playerId : connectedPlayers) {
            players.addLong(playerId);
        }
        nbt.putList(CONNECTED_PLAYERS_TAG, players);

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

    public VaultConfig getVaultConfig() {
        return vaultConfig;
    }

    public VaultData getVaultData() {
        return vaultData;
    }

    public Item getDisplayItem() {
        return displayItem;
    }

    public void setDisplayItem(Item item) {
        displayItem = item;
    }

    public float getConnectedParticleRange() {
        return connectedParticleRange;
    }

    public void setConnectedParticleRange(float range) {
        connectedParticleRange = range;
    }

    public LongSet getConnectedPlayers() {
        return connectedPlayers;
    }

    @ToString
    public class VaultConfig {
        /**
         * float.
         */
        static final String ACTIVATION_RANGE_TAG = "activation_range";
        /**
         * float.
         */
        static final String DEACTIVATION_RANGE_TAG = "deactivation_range";
        /**
         * compound. (Item)
         */
        static final String KEY_ITEM_TAG = "key_item";
        /**
         * string. (LootTable)
         */
        static final String LOOT_TABLE_TAG = "loot_table";
        /**
         * string. (LootTable)
         */
        static final String OVERRIDE_LOOT_TABLE_TO_DISPLAY_TAG = "override_loot_table_to_display";

        private float activationRange = 4;
        private float deactivationRange = 4.5f;
        private Item keyItem;
        private String lootTable;
        private String overrideLootTableToDisplay = "";

        public void load(CompoundTag tag) {
            if (tag.contains(ACTIVATION_RANGE_TAG)) {
                activationRange = tag.getFloat(ACTIVATION_RANGE_TAG);
            }
            if (tag.contains(DEACTIVATION_RANGE_TAG)) {
                deactivationRange = tag.getFloat(DEACTIVATION_RANGE_TAG);
            }

            if (tag.contains(KEY_ITEM_TAG)) {
                keyItem = NBTIO.getItemHelper(tag.getCompound(KEY_ITEM_TAG));
            }

            if (tag.contains(LOOT_TABLE_TAG)) {
                lootTable = tag.getString(LOOT_TABLE_TAG);
            } else {
                Block block = getLevelBlock();
                if (block.is(Block.VAULT) && (block.getDamage() & BlockVault.OMINOUS_BIT) != 0) {
                    lootTable = LootTableNames.CHESTS_TRIAL_CHAMBERS_REWARD_OMINOUS;
                } else {
                    lootTable = LootTableNames.CHESTS_TRIAL_CHAMBERS_REWARD;
                }
            }
            if (tag.contains(OVERRIDE_LOOT_TABLE_TO_DISPLAY_TAG)) {
                overrideLootTableToDisplay = tag.getString(OVERRIDE_LOOT_TABLE_TO_DISPLAY_TAG);
            }
        }

        public void save(CompoundTag tag) {
            tag.putFloat(ACTIVATION_RANGE_TAG, activationRange);
            tag.putFloat(DEACTIVATION_RANGE_TAG, deactivationRange);

            tag.putCompound(KEY_ITEM_TAG, NBTIO.putItemHelper(keyItem));

            tag.putString(LOOT_TABLE_TAG, lootTable != null ? lootTable : "");
            tag.putString(OVERRIDE_LOOT_TABLE_TO_DISPLAY_TAG, overrideLootTableToDisplay);
        }

        public float getActivationRange() {
            return activationRange;
        }

        public void setActivationRange(float range) {
            activationRange = range;
        }

        public float getDeactivationRange() {
            return deactivationRange;
        }

        public void setDeactivationRange(float range) {
            deactivationRange = range;
        }

        public Item getKeyItem() {
            return keyItem;
        }

        public void setKeyItem(Item item) {
            keyItem = item;
        }

        public String getLootTable() {
            return lootTable;
        }

        public void setLootTable(String lootTable) {
            this.lootTable = lootTable;
        }

        public String getOverrideLootTableToDisplay() {
            return overrideLootTableToDisplay;
        }

        public void setOverrideLootTableToDisplay(String lootTable) {
            overrideLootTableToDisplay = lootTable;
        }
    }

    @ToString
    public static class VaultData {
        /**
         * compound. (Item)
         */
        static final String DISPLAY_ITEM_TAG = "display_item";
        /**
         * long. (Tick)
         */
        static final String STATE_UPDATING_RESUMES_AT_TAG = "state_updating_resumes_at";
        /**
         * compound list. (Items)
         */
        static final String ITEMS_TO_EJECT_TAG = "items_to_eject";
        /**
         * long list. (EntityUniqueIDs)
         */
        static final String REWARDED_PLAYERS_TAG = "rewarded_players";

        private Item displayItem;
        private long stateUpdatingResumesAt;
        private final List<Item> itemsToEject = new ArrayList<>();
        private final LongSet rewardedPlayers = new LongOpenHashSet();

        public void load(CompoundTag tag) {
            if (tag.contains(DISPLAY_ITEM_TAG)) {
                displayItem = NBTIO.getItemHelper(tag.getCompound(DISPLAY_ITEM_TAG));
            }

            if (tag.contains(STATE_UPDATING_RESUMES_AT_TAG)) {
                stateUpdatingResumesAt = tag.getLong(STATE_UPDATING_RESUMES_AT_TAG);
            }

            if (tag.contains(ITEMS_TO_EJECT_TAG)) {
                for (CompoundTag item : tag.getList(ITEMS_TO_EJECT_TAG, CompoundTag.class)) {
                    itemsToEject.add(NBTIO.getItemHelper(item));
                }
            }

            if (tag.contains(REWARDED_PLAYERS_TAG)) {
                for (LongTag entityUniqueId : tag.getList(REWARDED_PLAYERS_TAG, LongTag.class)) {
                    rewardedPlayers.add(entityUniqueId.data);
                }
            }
        }

        public void save(CompoundTag tag) {
            tag.putCompound(DISPLAY_ITEM_TAG, NBTIO.putItemHelper(displayItem));

            tag.putLong(STATE_UPDATING_RESUMES_AT_TAG, stateUpdatingResumesAt);

            ListTag<CompoundTag> items = new ListTag<>();
            for (Item item : itemsToEject) {
                items.add(NBTIO.putItemHelper(item));
            }
            tag.putList(ITEMS_TO_EJECT_TAG, items);

            ListTag<LongTag> players = new ListTag<>();
            for (long playerId : rewardedPlayers) {
                players.addLong(playerId);
            }
            tag.putList(REWARDED_PLAYERS_TAG, players);
        }

        public Item getDisplayItem() {
            return displayItem;
        }

        public void setDisplayItem(Item item) {
            displayItem = item;
        }

        public long getStateUpdatingResumesAt() {
            return stateUpdatingResumesAt;
        }

        public void setStateUpdatingResumesAt(long tick) {
            stateUpdatingResumesAt = tick;
        }

        public List<Item> getItemsToEject() {
            return itemsToEject;
        }

        public LongSet getRewardedPlayers() {
            return rewardedPlayers;
        }
    }
}
