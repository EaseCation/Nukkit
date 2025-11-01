package cn.nukkit.loot;

import javax.annotation.Nullable;

public interface Lootable {
    void unpackLootTable();

    @Nullable
    String getLootTable();

    void setLootTable(@Nullable String lootTable);

    int getLootTableSeed();

    void setLootTableSeed(int seed);
}
