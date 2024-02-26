package cn.nukkit.level.biome;

import cn.nukkit.GameVersion;
import cn.nukkit.Server;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.NukkitRandom;
import com.google.gson.Gson;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class Biome implements BlockID {
    public static final int MAX_BIOMES = 256;
    public static final Biome[] biomes = new Biome[MAX_BIOMES];
    public static final List<Biome> unorderedBiomes = new ArrayList<>();

    private static final Object2IntMap<String> nameToId;
    private static final String[] idToName = new String[MAX_BIOMES];
    private static final boolean[] VALID_BIOMES = new boolean[MAX_BIOMES];

    private final List<Populator> populators = new ArrayList<>();
    private int id;
    private float baseHeight = 0.1f;
    private float heightVariation = 0.2f;

    protected static boolean register(int id, Biome biome, @Nullable GameVersion version) {
        biome.setId(id);
        biomes[id] = biome;
        unorderedBiomes.add(biome);
        if (version != null && !version.isAvailable()) {
            return false;
        }
        VALID_BIOMES[id] = true;
        return true;
    }

    public static Biome getBiome(int id) {
        Biome biome = biomes[id];
        return biome != null ? biome : EnumBiome.OCEAN.biome;
    }

    /**
     * Get Biome by name.
     *
     * @param name Name of biome. Name could contain symbol "_" instead of space
     * @return Biome. Null - when biome was not found
     */
    public static Biome getBiome(String name) {
        for (Biome biome : biomes) {
            if (biome != null) {
                if (biome.getName().equalsIgnoreCase(name.replace("_", " "))) return biome;
            }
        }
        return null;
    }

    public void clearPopulators() {
        this.populators.clear();
    }

    public void addPopulator(Populator populator) {
        this.populators.add(populator);
    }

    public void populateChunk(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random) {
        FullChunk chunk = level.getChunk(chunkX, chunkZ);
        for (Populator populator : populators) {
            populator.populate(level, chunkX, chunkZ, random, chunk);
        }
    }

    public List<Populator> getPopulators() {
        return populators;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public abstract String getName();

    public void setBaseHeight(float baseHeight) {
        this.baseHeight = baseHeight;
    }

    public void setHeightVariation(float heightVariation)   {
        this.heightVariation = heightVariation;
    }

    public float getBaseHeight() {
        return baseHeight;
    }

    public float getHeightVariation() {
        return heightVariation;
    }

    @Override
    public int hashCode() {
        return getId();
    }

    @Override
    public boolean equals(Object obj) {
        return hashCode() == obj.hashCode();
    }

    //whether or not water should freeze into ice on generation
    public boolean isFreezing() {
        return false;
    }

    /**
     * Whether or not overhangs should generate in this biome (places where solid blocks generate over air)
     *
     * This should probably be used with a custom max elevation or things can look stupid
     *
     * @return overhang
     */
    public boolean doesOverhang()   {
        return false;
    }

    /**
     * How much offset should be added to the min/max heights at this position
     *
     * @param x x
     * @param z z
     * @return height offset
     */
    public int getHeightOffset(int x, int z)    {
        return 0;
    }

    public boolean canRain() {
        return true;
    }

    public boolean canSnow() {
        return false;
    }

    public static int getIdByName(String name) {
        return nameToId.getInt(name);
    }

    @Nullable
    public static String getNameById(int id) {
        return idToName[id];
    }

    public static boolean isValidBiome(int id) {
        return VALID_BIOMES[id];
    }

    public static int toValidBiome(int id) {
        return !isValidBiome(id) ? BiomeID.OCEAN : id;
    }

    static {
        try (InputStream stream = Server.class.getClassLoader().getResourceAsStream("biome_id_map.json");
             InputStreamReader reader = new InputStreamReader(stream)) {
            nameToId = new Gson().fromJson(reader, Object2IntOpenHashMap.class);
        } catch (NullPointerException | IOException e) {
            throw new AssertionError("Unable to load block_id_map.json", e);
        }
        nameToId.forEach((name, id) -> idToName[id] = name);
        nameToId.defaultReturnValue(-1);
    }
}
