package cn.nukkit.loot;

import cn.nukkit.Server;
import cn.nukkit.utils.JsonUtil;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LootTables {
    private static final Map<String, LootTable> REGISTRY = new HashMap<>();

    public static final LootTable EMPTY = register(LootTableNames.EMPTY);
    public static final LootTable CHESTS_SPAWN_BONUS_CHEST = register(LootTableNames.CHESTS_SPAWN_BONUS_CHEST);
    public static final LootTable GAMEPLAY_FISHING = register(LootTableNames.GAMEPLAY_FISHING);
    public static final LootTable GAMEPLAY_FISHING_FISH = register(LootTableNames.GAMEPLAY_FISHING_FISH);
    public static final LootTable GAMEPLAY_FISHING_JUNGLE_FISH = register(LootTableNames.GAMEPLAY_FISHING_JUNGLE_FISH);
    public static final LootTable GAMEPLAY_FISHING_JUNGLE_JUNK = register(LootTableNames.GAMEPLAY_FISHING_JUNGLE_JUNK);
    public static final LootTable GAMEPLAY_FISHING_JUNK = register(LootTableNames.GAMEPLAY_FISHING_JUNK);
    public static final LootTable GAMEPLAY_FISHING_TREASURE = register(LootTableNames.GAMEPLAY_FISHING_TREASURE);
    public static final LootTable GAMEPLAY_JUNGLE_FISHING = register(LootTableNames.GAMEPLAY_JUNGLE_FISHING);

    private static LootTable register(String name) {
        try (InputStream stream = Server.class.getClassLoader().getResourceAsStream(name)) {
            LootTable table = JsonUtil.TRUSTED_JSON_MAPPER.readValue(new InputStreamReader(stream), LootTable.class);
            return register(name, table);
        } catch (Exception e) {
            throw new RuntimeException("Unable to load loot table: " + name, e);
        }
    }

    public static LootTable register(String name, LootTable table) {
        return register(name, table, false);
    }

    public static LootTable register(String name, LootTable table, boolean override) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(table, "table");
        if (override) {
            REGISTRY.put(name, table);
        } else if (REGISTRY.putIfAbsent(name, table) != null) {
            throw new IllegalArgumentException("Duplicate loot table name: " + name);
        }
        return table;
    }

    @Nullable
    public static LootTable lookupByName(String name) {
        return REGISTRY.get(name);
    }

    public static void registerVanillaLootTables() {
    }
}
