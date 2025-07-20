package cn.nukkit.level.biome;

public interface BiomeNetworkManager {
    void registerCustomBiome(int id, String name, String fullName, CustomBiome biome);

    void rebuildCache();

    int toClientId(int id, boolean legacy);
}
