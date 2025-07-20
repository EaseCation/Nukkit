package cn.nukkit.level.biome;

import cn.nukkit.GameVersion;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.level.biome.impl.beach.BeachBiome;
import cn.nukkit.level.biome.impl.beach.ColdBeachBiome;
import cn.nukkit.level.biome.impl.cave.DeepDarkBiome;
import cn.nukkit.level.biome.impl.cave.DripstoneCavesBiome;
import cn.nukkit.level.biome.impl.cave.LushCavesBiome;
import cn.nukkit.level.biome.impl.desert.DesertBiome;
import cn.nukkit.level.biome.impl.desert.DesertHillsBiome;
import cn.nukkit.level.biome.impl.desert.DesertMBiome;
import cn.nukkit.level.biome.impl.end.EndBiome;
import cn.nukkit.level.biome.impl.extremehills.*;
import cn.nukkit.level.biome.impl.forest.FlowerForestBiome;
import cn.nukkit.level.biome.impl.forest.ForestBiome;
import cn.nukkit.level.biome.impl.forest.ForestHillsBiome;
import cn.nukkit.level.biome.impl.iceplains.IcePlainsBiome;
import cn.nukkit.level.biome.impl.iceplains.IcePlainsHillsBiome;
import cn.nukkit.level.biome.impl.iceplains.IcePlainsSpikesBiome;
import cn.nukkit.level.biome.impl.jungle.*;
import cn.nukkit.level.biome.impl.mesa.*;
import cn.nukkit.level.biome.impl.mountain.*;
import cn.nukkit.level.biome.impl.mushroom.MushroomIslandBiome;
import cn.nukkit.level.biome.impl.mushroom.MushroomIslandShoreBiome;
import cn.nukkit.level.biome.impl.nether.*;
import cn.nukkit.level.biome.impl.ocean.*;
import cn.nukkit.level.biome.impl.plains.PlainsBiome;
import cn.nukkit.level.biome.impl.plains.SunflowerPlainsBiome;
import cn.nukkit.level.biome.impl.river.FrozenRiverBiome;
import cn.nukkit.level.biome.impl.river.RiverBiome;
import cn.nukkit.level.biome.impl.roofedforest.PaleGardenBiome;
import cn.nukkit.level.biome.impl.roofedforest.RoofedForestBiome;
import cn.nukkit.level.biome.impl.roofedforest.RoofedForestMBiome;
import cn.nukkit.level.biome.impl.savanna.SavannaBiome;
import cn.nukkit.level.biome.impl.savanna.SavannaMBiome;
import cn.nukkit.level.biome.impl.savanna.SavannaPlateauBiome;
import cn.nukkit.level.biome.impl.savanna.SavannaPlateauMBiome;
import cn.nukkit.level.biome.impl.swamp.MangroveSwampBiome;
import cn.nukkit.level.biome.impl.swamp.SwampBiome;
import cn.nukkit.level.biome.impl.swamp.SwamplandMBiome;
import cn.nukkit.level.biome.impl.taiga.*;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static cn.nukkit.GameVersion.*;

@Log4j2
public final class Biomes {
    private static final Map<String, BiomeData> REGISTRY = new HashMap<>(256);
    private static final BiomeData[] BY_ID = new BiomeData[Short.MAX_VALUE];
    private static final Map<String, BiomeData> BY_CUSTOM_NAME = new HashMap<>();
    private static final List<Biome> BIOMES = new ArrayList<>(256);
    private static final List<Biome> CUSTOM_BIOMES = new ArrayList<>();
    private static final AtomicInteger CUSTOM_BIOME_ID_ALLOCATOR = new AtomicInteger(30000);
    private static BiomeNetworkManager NETWORK_MANAGER;

    public static final Biome OCEAN = registerBiome(BiomeID.OCEAN, BiomeNames.OCEAN, new OceanBiome());
    public static final Biome PLAINS = registerBiome(BiomeID.PLAINS, BiomeNames.PLAINS, new PlainsBiome());
    public static final Biome DESERT = registerBiome(BiomeID.DESERT, BiomeNames.DESERT, new DesertBiome());
    public static final Biome EXTREME_HILLS = registerBiome(BiomeID.EXTREME_HILLS, BiomeNames.EXTREME_HILLS, new ExtremeHillsBiome());
    public static final Biome FOREST = registerBiome(BiomeID.FOREST, BiomeNames.FOREST, new ForestBiome());
    public static final Biome TAIGA = registerBiome(BiomeID.TAIGA, BiomeNames.TAIGA, new TaigaBiome());
    public static final Biome SWAMPLAND = registerBiome(BiomeID.SWAMPLAND, BiomeNames.SWAMPLAND, new SwampBiome());
    public static final Biome RIVER = registerBiome(BiomeID.RIVER, BiomeNames.RIVER, new RiverBiome());
    public static final Biome HELL = registerBiome(BiomeID.HELL, BiomeNames.HELL, new HellBiome());
    public static final Biome THE_END = registerBiome(BiomeID.THE_END, BiomeNames.THE_END, new EndBiome());
    public static final Biome LEGACY_FROZEN_OCEAN = registerBiome(BiomeID.LEGACY_FROZEN_OCEAN, BiomeNames.LEGACY_FROZEN_OCEAN, new FrozenOceanBiome());
    public static final Biome FROZEN_RIVER = registerBiome(BiomeID.FROZEN_RIVER, BiomeNames.FROZEN_RIVER, new FrozenRiverBiome());
    public static final Biome ICE_PLAINS = registerBiome(BiomeID.ICE_PLAINS, BiomeNames.ICE_PLAINS, new IcePlainsBiome());
    public static final Biome ICE_MOUNTAINS = registerBiome(BiomeID.ICE_MOUNTAINS, BiomeNames.ICE_MOUNTAINS, new IcePlainsHillsBiome());
    public static final Biome MUSHROOM_ISLAND = registerBiome(BiomeID.MUSHROOM_ISLAND, BiomeNames.MUSHROOM_ISLAND, new MushroomIslandBiome());
    public static final Biome MUSHROOM_ISLAND_SHORE = registerBiome(BiomeID.MUSHROOM_ISLAND_SHORE, BiomeNames.MUSHROOM_ISLAND_SHORE, new MushroomIslandShoreBiome());
    public static final Biome BEACH = registerBiome(BiomeID.BEACH, BiomeNames.BEACH, new BeachBiome());
    public static final Biome DESERT_HILLS = registerBiome(BiomeID.DESERT_HILLS, BiomeNames.DESERT_HILLS, new DesertHillsBiome());
    public static final Biome FOREST_HILLS = registerBiome(BiomeID.FOREST_HILLS, BiomeNames.FOREST_HILLS, new ForestHillsBiome());
    public static final Biome TAIGA_HILLS = registerBiome(BiomeID.TAIGA_HILLS, BiomeNames.TAIGA_HILLS, new TaigaHillsBiome());
    public static final Biome EXTREME_HILLS_EDGE = registerBiome(BiomeID.EXTREME_HILLS_EDGE, BiomeNames.EXTREME_HILLS_EDGE, new ExtremeHillsEdgeBiome());
    public static final Biome JUNGLE = registerBiome(BiomeID.JUNGLE, BiomeNames.JUNGLE, new JungleBiome());
    public static final Biome JUNGLE_HILLS = registerBiome(BiomeID.JUNGLE_HILLS, BiomeNames.JUNGLE_HILLS, new JungleHillsBiome());
    public static final Biome JUNGLE_EDGE = registerBiome(BiomeID.JUNGLE_EDGE, BiomeNames.JUNGLE_EDGE, new JungleEdgeBiome());
    public static final Biome DEEP_OCEAN = registerBiome(BiomeID.DEEP_OCEAN, BiomeNames.DEEP_OCEAN, new DeepOceanBiome());
    public static final Biome STONE_BEACH = registerBiome(BiomeID.STONE_BEACH, BiomeNames.STONE_BEACH, new StoneBeachBiome());
    public static final Biome COLD_BEACH = registerBiome(BiomeID.COLD_BEACH, BiomeNames.COLD_BEACH, new ColdBeachBiome());
    public static final Biome BIRCH_FOREST = registerBiome(BiomeID.BIRCH_FOREST, BiomeNames.BIRCH_FOREST, new ForestBiome(ForestBiome.TYPE_BIRCH));
    public static final Biome BIRCH_FOREST_HILLS = registerBiome(BiomeID.BIRCH_FOREST_HILLS, BiomeNames.BIRCH_FOREST_HILLS, new ForestHillsBiome(ForestHillsBiome.TYPE_BIRCH));
    public static final Biome ROOFED_FOREST = registerBiome(BiomeID.ROOFED_FOREST, BiomeNames.ROOFED_FOREST, new RoofedForestBiome());
    public static final Biome COLD_TAIGA = registerBiome(BiomeID.COLD_TAIGA, BiomeNames.COLD_TAIGA, new ColdTaigaBiome());
    public static final Biome COLD_TAIGA_HILLS = registerBiome(BiomeID.COLD_TAIGA_HILLS, BiomeNames.COLD_TAIGA_HILLS, new ColdTaigaHillsBiome());
    public static final Biome MEGA_TAIGA = registerBiome(BiomeID.MEGA_TAIGA, BiomeNames.MEGA_TAIGA, new MegaTaigaBiome());
    public static final Biome MEGA_TAIGA_HILLS = registerBiome(BiomeID.MEGA_TAIGA_HILLS, BiomeNames.MEGA_TAIGA_HILLS, new MegaTaigaHillsBiome());
    public static final Biome EXTREME_HILLS_PLUS_TREES = registerBiome(BiomeID.EXTREME_HILLS_PLUS_TREES, BiomeNames.EXTREME_HILLS_PLUS_TREES, new ExtremeHillsPlusBiome());
    public static final Biome SAVANNA = registerBiome(BiomeID.SAVANNA, BiomeNames.SAVANNA, new SavannaBiome());
    public static final Biome SAVANNA_PLATEAU = registerBiome(BiomeID.SAVANNA_PLATEAU, BiomeNames.SAVANNA_PLATEAU, new SavannaPlateauBiome());
    public static final Biome MESA = registerBiome(BiomeID.MESA, BiomeNames.MESA, new MesaBiome());
    public static final Biome MESA_PLATEAU_STONE = registerBiome(BiomeID.MESA_PLATEAU_STONE, BiomeNames.MESA_PLATEAU_STONE, new MesaPlateauFBiome());
    public static final Biome MESA_PLATEAU = registerBiome(BiomeID.MESA_PLATEAU, BiomeNames.MESA_PLATEAU, new MesaPlateauBiome());
    public static final Biome WARM_OCEAN = registerBiome(BiomeID.WARM_OCEAN, BiomeNames.WARM_OCEAN, new WarmOceanBiome(), V1_4_0);
    public static final Biome DEEP_WARM_OCEAN = registerBiome(BiomeID.DEEP_WARM_OCEAN, BiomeNames.DEEP_WARM_OCEAN, new WarmDeepOceanBiome(), V1_4_0);
    public static final Biome LUKEWARM_OCEAN = registerBiome(BiomeID.LUKEWARM_OCEAN, BiomeNames.LUKEWARM_OCEAN, new LukewarmOceanBiome(), V1_4_0);
    public static final Biome DEEP_LUKEWARM_OCEAN = registerBiome(BiomeID.DEEP_LUKEWARM_OCEAN, BiomeNames.DEEP_LUKEWARM_OCEAN, new LukewarmDeepOceanBiome(), V1_4_0);
    public static final Biome COLD_OCEAN = registerBiome(BiomeID.COLD_OCEAN, BiomeNames.COLD_OCEAN, new ColdOceanBiome(), V1_4_0);
    public static final Biome DEEP_COLD_OCEAN = registerBiome(BiomeID.DEEP_COLD_OCEAN, BiomeNames.DEEP_COLD_OCEAN, new ColdDeepOceanBiome(), V1_4_0);
    public static final Biome FROZEN_OCEAN = registerBiome(BiomeID.FROZEN_OCEAN, BiomeNames.FROZEN_OCEAN, new NewFrozenOceanBiome(), V1_4_0);
    public static final Biome DEEP_FROZEN_OCEAN = registerBiome(BiomeID.DEEP_FROZEN_OCEAN, BiomeNames.DEEP_FROZEN_OCEAN, new FrozenDeepOceanBiome(), V1_4_0);
    public static final Biome BAMBOO_JUNGLE = registerBiome(BiomeID.BAMBOO_JUNGLE, BiomeNames.BAMBOO_JUNGLE, new BambooJungleBiome(), V1_11_0);
    public static final Biome BAMBOO_JUNGLE_HILLS = registerBiome(BiomeID.BAMBOO_JUNGLE_HILLS, BiomeNames.BAMBOO_JUNGLE_HILLS, new BambooJungleHillsBiome(), V1_11_0);

    public static final Biome SUNFLOWER_PLAINS = registerBiome(BiomeID.SUNFLOWER_PLAINS, BiomeNames.SUNFLOWER_PLAINS, new SunflowerPlainsBiome());
    public static final Biome DESERT_MUTATED = registerBiome(BiomeID.DESERT_MUTATED, BiomeNames.DESERT_MUTATED, new DesertMBiome());
    public static final Biome EXTREME_HILLS_MUTATED = registerBiome(BiomeID.EXTREME_HILLS_MUTATED, BiomeNames.EXTREME_HILLS_MUTATED, new ExtremeHillsMBiome());
    public static final Biome FLOWER_FOREST = registerBiome(BiomeID.FLOWER_FOREST, BiomeNames.FLOWER_FOREST, new FlowerForestBiome());
    public static final Biome TAIGA_MUTATED = registerBiome(BiomeID.TAIGA_MUTATED, BiomeNames.TAIGA_MUTATED, new TaigaMBiome());
    public static final Biome SWAMPLAND_MUTATED = registerBiome(BiomeID.SWAMPLAND_MUTATED, BiomeNames.SWAMPLAND_MUTATED, new SwamplandMBiome());

    public static final Biome ICE_PLAINS_SPIKES = registerBiome(BiomeID.ICE_PLAINS_SPIKES, BiomeNames.ICE_PLAINS_SPIKES, new IcePlainsSpikesBiome());

    public static final Biome JUNGLE_MUTATED = registerBiome(BiomeID.JUNGLE_MUTATED, BiomeNames.JUNGLE_MUTATED, new JungleMBiome());

    public static final Biome JUNGLE_EDGE_MUTATED = registerBiome(BiomeID.JUNGLE_EDGE_MUTATED, BiomeNames.JUNGLE_EDGE_MUTATED, new JungleEdgeMBiome());

    public static final Biome BIRCH_FOREST_MUTATED = registerBiome(BiomeID.BIRCH_FOREST_MUTATED, BiomeNames.BIRCH_FOREST_MUTATED, new ForestBiome(ForestBiome.TYPE_BIRCH_TALL));
    public static final Biome BIRCH_FOREST_HILLS_MUTATED = registerBiome(BiomeID.BIRCH_FOREST_HILLS_MUTATED, BiomeNames.BIRCH_FOREST_HILLS_MUTATED, new ForestHillsBiome(ForestBiome.TYPE_BIRCH_TALL));
    public static final Biome ROOFED_FOREST_MUTATED = registerBiome(BiomeID.ROOFED_FOREST_MUTATED, BiomeNames.ROOFED_FOREST_MUTATED, new RoofedForestMBiome());
    public static final Biome COLD_TAIGA_MUTATED = registerBiome(BiomeID.COLD_TAIGA_MUTATED, BiomeNames.COLD_TAIGA_MUTATED, new ColdTaigaMBiome());

    public static final Biome REDWOOD_TAIGA_MUTATED = registerBiome(BiomeID.REDWOOD_TAIGA_MUTATED, BiomeNames.REDWOOD_TAIGA_MUTATED, new MegaSpruceTaigaBiome());
    public static final Biome REDWOOD_TAIGA_HILLS_MUTATED = registerBiome(BiomeID.REDWOOD_TAIGA_HILLS_MUTATED, BiomeNames.REDWOOD_TAIGA_HILLS_MUTATED, new MegaSpruceTaigaHillsBiome());
    public static final Biome EXTREME_HILLS_PLUS_TREES_MUTATED = registerBiome(BiomeID.EXTREME_HILLS_PLUS_TREES_MUTATED, BiomeNames.EXTREME_HILLS_PLUS_TREES_MUTATED, new ExtremeHillsPlusMBiome());
    public static final Biome SAVANNA_MUTATED = registerBiome(BiomeID.SAVANNA_MUTATED, BiomeNames.SAVANNA_MUTATED, new SavannaMBiome());
    public static final Biome SAVANNA_PLATEAU_MUTATED = registerBiome(BiomeID.SAVANNA_PLATEAU_MUTATED, BiomeNames.SAVANNA_PLATEAU_MUTATED, new SavannaPlateauMBiome());
    public static final Biome MESA_BRYCE = registerBiome(BiomeID.MESA_BRYCE, BiomeNames.MESA_BRYCE, new MesaBryceBiome());
    public static final Biome MESA_PLATEAU_STONE_MUTATED = registerBiome(BiomeID.MESA_PLATEAU_STONE_MUTATED, BiomeNames.MESA_PLATEAU_STONE_MUTATED, new MesaPlateauFMBiome());
    public static final Biome MESA_PLATEAU_MUTATED = registerBiome(BiomeID.MESA_PLATEAU_MUTATED, BiomeNames.MESA_PLATEAU_MUTATED, new MesaPlateauMBiome());

    public static final Biome SOULSAND_VALLEY = registerBiome(BiomeID.SOULSAND_VALLEY, BiomeNames.SOULSAND_VALLEY, new SoulsandValleyBiome(), V1_16_0);
    public static final Biome CRIMSON_FOREST = registerBiome(BiomeID.CRIMSON_FOREST, BiomeNames.CRIMSON_FOREST, new CrimsonForestBiome(), V1_16_0);
    public static final Biome WARPED_FOREST = registerBiome(BiomeID.WARPED_FOREST, BiomeNames.WARPED_FOREST, new WarpedForestBiome(), V1_16_0);
    public static final Biome BASALT_DELTAS = registerBiome(BiomeID.BASALT_DELTAS, BiomeNames.BASALT_DELTAS, new BasaltDeltasBiome(), V1_16_0);
    public static final Biome JAGGED_PEAKS = registerBiome(BiomeID.JAGGED_PEAKS, BiomeNames.JAGGED_PEAKS, new JaggedPeaksBiome(), V1_18_0);
    public static final Biome FROZEN_PEAKS = registerBiome(BiomeID.FROZEN_PEAKS, BiomeNames.FROZEN_PEAKS, new FrozenPeaksBiome(), V1_18_0);
    public static final Biome SNOWY_SLOPES = registerBiome(BiomeID.SNOWY_SLOPES, BiomeNames.SNOWY_SLOPES, new SnowySlopesBiome(), V1_18_0);
    public static final Biome GROVE = registerBiome(BiomeID.GROVE, BiomeNames.GROVE, new GroveBiome(), V1_18_0);
    public static final Biome MEADOW = registerBiome(BiomeID.MEADOW, BiomeNames.MEADOW, new MeadowBiome(), V1_18_0);
    public static final Biome LUSH_CAVES = registerBiome(BiomeID.LUSH_CAVES, BiomeNames.LUSH_CAVES, new LushCavesBiome(), V1_18_0);
    public static final Biome DRIPSTONE_CAVES = registerBiome(BiomeID.DRIPSTONE_CAVES, BiomeNames.DRIPSTONE_CAVES, new DripstoneCavesBiome(), V1_18_0);
    public static final Biome STONY_PEAKS = registerBiome(BiomeID.STONY_PEAKS, BiomeNames.STONY_PEAKS, new StonyPeaksBiome(), V1_18_0);
    public static final Biome DEEP_DARK = registerBiome(BiomeID.DEEP_DARK, BiomeNames.DEEP_DARK, new DeepDarkBiome(), V1_19_0);
    public static final Biome MANGROVE_SWAMP = registerBiome(BiomeID.MANGROVE_SWAMP, BiomeNames.MANGROVE_SWAMP, new MangroveSwampBiome(), V1_19_0);
    public static final Biome CHERRY_GROVE = registerBiome(BiomeID.CHERRY_GROVE, BiomeNames.CHERRY_GROVE, new CherryGroveBiome(), V1_20_0);
    public static final Biome PALE_GARDEN = registerBiome(BiomeID.PALE_GARDEN, BiomeNames.PALE_GARDEN, new PaleGardenBiome(), V1_21_50);

    private static Biome registerBiome(int id, String name, Biome biome) {
        return registerBiome(id, name, "minecraft:" + name, biome);
    }

    /**
     * @param version min required base game version
     */
    private static Biome registerBiome(int id, String name, Biome biome, GameVersion version) {
        if (!version.isAvailable()) {
            return null;
        }
        return registerBiome(id, name, biome);
    }

    private static Biome registerBiome(int id, String name, String fullName, Biome biome) {
        BiomeData data = new BiomeData(id, name, fullName, biome);
        if (BY_ID[id] != null) {
            throw new IllegalArgumentException("Duplicate biome id: " + id);
        }
        if (REGISTRY.containsKey(fullName)) {
            throw new IllegalArgumentException("Duplicate biome full name: " + fullName);
        }
        if (REGISTRY.putIfAbsent(name, data) != null) {
            throw new IllegalArgumentException("Duplicate biome name: " + name);
        }
        REGISTRY.put(fullName, data);
        BY_CUSTOM_NAME.put(name, data);
        BY_CUSTOM_NAME.put(fullName, data);
        BY_ID[id] = data;
        BIOMES.add(biome);
        if (!biome.isVanilla()) {
            CUSTOM_BIOMES.add(biome);
        }
        biome.setId(id);
        return biome;
    }

    /**
     * @return biome id
     */
    public static int registerCustomBiome(String fullName, CustomBiome biome) {
        Objects.requireNonNull(biome, "biome");
        String[] split = fullName.split(":");
        if (split.length != 2) {
            throw new IllegalArgumentException("Invalid namespaced identifier: " + fullName);
        }
        if (split[0].equalsIgnoreCase("minecraft")) {
            throw new IllegalArgumentException("Invalid identifier: " + fullName);
        }
        String name = split[1];

        int id = CUSTOM_BIOME_ID_ALLOCATOR.getAndIncrement();
        if (id >= BY_ID.length) {
            throw new AssertionError("Custom biome ID overflow. Please increase the capacity");
        }

        log.trace("Register custom biome: {} ({})", fullName, id);
        registerBiome(id, name, fullName, biome);

        NETWORK_MANAGER.registerCustomBiome(id, name, fullName, biome);

        CommandEnum.ENUM_BIOME.getValues().put(name, Collections.emptySet());
        CommandEnum.ENUM_BIOME.getValues().put(fullName, Collections.emptySet());

        return id;
    }

    @Nullable
    private static BiomeData getInternal(String name) {
        if (name.startsWith("minecraft:")) {
            name = name.substring(10);
        }
        return REGISTRY.get(name);
    }

    @Nullable
    private static BiomeData getInternal(int id) {
        if (id < 0 || id >= BY_ID.length) {
            return null;
        }
        return BY_ID[id];
    }

    @Nullable
    public static Biome getNullable(String name) {
        BiomeData data = getInternal(name);
        if (data == null) {
            return null;
        }
        return data.biome;
    }

    public static Biome get(String name) {
        Biome biome = getNullable(name);
        if (biome == null) {
            return OCEAN;
        }
        return biome;
    }

    @Nullable
    public static Biome getNullable(int id) {
        BiomeData data = getInternal(id);
        if (data == null) {
            return null;
        }
        return data.biome;
    }

    public static Biome get(int id) {
        Biome biome = getNullable(id);
        if (biome == null) {
            return OCEAN;
        }
        return biome;
    }

    public static int getIdByName(String name) {
        BiomeData data = getInternal(name);
        if (data == null) {
            return -1;
        }
        return data.id;
    }

    public static int getValidIdByName(String name) {
        int data = getIdByName(name);
        if (data == -1) {
            return BiomeID.OCEAN;
        }
        return data;
    }

    public static int getIdByCustomName(String name) {
        BiomeData data = BY_CUSTOM_NAME.get(name);
        if (data == null) {
            return -1;
        }
        return data.id;
    }

    public static int getValidIdByCustomName(String name) {
        int data = getIdByCustomName(name);
        if (data == -1) {
            return BiomeID.OCEAN;
        }
        return data;
    }

    @Nullable
    public static String getNameByIdNullable(int id) {
        BiomeData data = getInternal(id);
        if (data == null) {
            return null;
        }
        return data.name;
    }

    public static String getNameById(int id) {
        String name = getNameByIdNullable(id);
        if (name == null) {
            return BiomeNames.OCEAN;
        }
        return name;
    }

    @Nullable
    public static String getFullNameByIdNullable(int id) {
        BiomeData data = getInternal(id);
        if (data == null) {
            return null;
        }
        return data.fullName;
    }

    public static String getFullNameById(int id) {
        String fullName = getFullNameByIdNullable(id);
        if (fullName == null) {
            return BiomeFullNames.OCEAN;
        }
        return fullName;
    }

    public static boolean isValid(int id) {
        return getInternal(id) != null;
    }

    public static int toValid(int id) {
        return !isValid(id) ? BiomeID.OCEAN : id;
    }

    public static List<Biome> getBiomes() {
        return BIOMES;
    }

    public static List<Biome> getCustomBiomes() {
        return CUSTOM_BIOMES;
    }

    public static void setNetworkManager(BiomeNetworkManager manager) {
        NETWORK_MANAGER = Objects.requireNonNull(manager, "manager");
    }

    public static void rebuildNetworkCache() {
        NETWORK_MANAGER.rebuildCache();
    }

    public static int toClientId(int id, boolean legacy) {
        return NETWORK_MANAGER.toClientId(id, legacy);
    }

    public static void registerVanillaBiomes() {
    }

    private Biomes() {
        throw new IllegalStateException();
    }

    private record BiomeData(int id, String name, String fullName, Biome biome) {
    }
}
