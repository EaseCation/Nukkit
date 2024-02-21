package cn.nukkit.level.biome;

import cn.nukkit.GameVersion;
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

import javax.annotation.Nullable;

import static cn.nukkit.GameVersion.*;

/**
 * @author DaPorkchop_
 * <p>
 * A more effective way of accessing specific biomes (to prevent Biome.getBiome(Biome.OCEAN) and such)
 * Also just looks cleaner than listing everything as static final in {@link Biome}
 * </p>
 */
public enum EnumBiome {
    OCEAN(BiomeID.OCEAN, new OceanBiome()),
    PLAINS(BiomeID.PLAINS, new PlainsBiome()),
    DESERT(BiomeID.DESERT, new DesertBiome()),
    EXTREME_HILLS(BiomeID.EXTREME_HILLS, new ExtremeHillsBiome()),
    FOREST(BiomeID.FOREST, new ForestBiome()),
    TAIGA(BiomeID.TAIGA, new TaigaBiome()),
    SWAMP(BiomeID.SWAMPLAND, new SwampBiome()),
    RIVER(BiomeID.RIVER, new RiverBiome()),
    HELL(BiomeID.HELL, new HellBiome()),
    THE_END(BiomeID.THE_END, new EndBiome()),
    LEGACY_FROZEN_OCEAN(BiomeID.LEGACY_FROZEN_OCEAN, new FrozenOceanBiome()), //DOES NOT GENERATE NATUALLY
    FROZEN_RIVER(BiomeID.FROZEN_RIVER, new FrozenRiverBiome()),
    ICE_PLAINS(BiomeID.ICE_PLAINS, new IcePlainsBiome()),
    ICE_MOUNTAINS(BiomeID.ICE_MOUNTAINS, new IcePlainsHillsBiome()),
    MUSHROOM_ISLAND(BiomeID.MUSHROOM_ISLAND, new MushroomIslandBiome()),
    MUSHROOM_ISLAND_SHORE(BiomeID.MUSHROOM_ISLAND_SHORE, new MushroomIslandShoreBiome()),
    BEACH(BiomeID.BEACH, new BeachBiome()),
    DESERT_HILLS(BiomeID.DESERT_HILLS, new DesertHillsBiome()),
    FOREST_HILLS(BiomeID.FOREST_HILLS, new ForestHillsBiome()),
    TAIGA_HILLS(BiomeID.TAIGA_HILLS, new TaigaHillsBiome()),
    EXTREME_HILLS_EDGE(BiomeID.EXTREME_HILLS_EDGE, new ExtremeHillsEdgeBiome()), //DOES NOT GENERATE NATUALLY
    JUNGLE(BiomeID.JUNGLE, new JungleBiome()),
    JUNGLE_HILLS(BiomeID.JUNGLE_HILLS, new JungleHillsBiome()),
    JUNGLE_EDGE(BiomeID.JUNGLE_EDGE, new JungleEdgeBiome()),
    DEEP_OCEAN(BiomeID.DEEP_OCEAN, new DeepOceanBiome()),
    STONE_BEACH(BiomeID.STONE_BEACH, new StoneBeachBiome()),
    COLD_BEACH(BiomeID.COLD_BEACH, new ColdBeachBiome()),
    BIRCH_FOREST(BiomeID.BIRCH_FOREST, new ForestBiome(ForestBiome.TYPE_BIRCH)),
    BIRCH_FOREST_HILLS(BiomeID.BIRCH_FOREST_HILLS, new ForestHillsBiome(ForestHillsBiome.TYPE_BIRCH)),
    ROOFED_FOREST(BiomeID.ROOFED_FOREST, new RoofedForestBiome()),
    COLD_TAIGA(BiomeID.COLD_TAIGA, new ColdTaigaBiome()),
    COLD_TAIGA_HILLS(BiomeID.COLD_TAIGA_HILLS, new ColdTaigaHillsBiome()),
    MEGA_TAIGA(BiomeID.MEGA_TAIGA, new MegaTaigaBiome()),
    MEGA_TAIGA_HILLS(BiomeID.MEGA_TAIGA_HILLS, new MegaTaigaHillsBiome()),
    EXTREME_HILLS_PLUS(BiomeID.EXTREME_HILLS_PLUS_TREES, new ExtremeHillsPlusBiome()),
    SAVANNA(BiomeID.SAVANNA, new SavannaBiome()),
    SAVANNA_PLATEAU(BiomeID.SAVANNA_PLATEAU, new SavannaPlateauBiome()),
    MESA(BiomeID.MESA, new MesaBiome()),
    MESA_PLATEAU_F(BiomeID.MESA_PLATEAU_STONE, new MesaPlateauFBiome()),
    MESA_PLATEAU(BiomeID.MESA_PLATEAU, new MesaPlateauBiome()),
    WARM_OCEAN(BiomeID.WARM_OCEAN, new WarmOceanBiome(), V1_4_0),
    DEEP_WARM_OCEAN(BiomeID.DEEP_WARM_OCEAN, new WarmDeepOceanBiome(), V1_4_0),
    LUKEWARM_OCEAN(BiomeID.LUKEWARM_OCEAN, new LukewarmOceanBiome(), V1_4_0),
    DEEP_LUKEWARM_OCEAN(BiomeID.DEEP_LUKEWARM_OCEAN, new LukewarmDeepOceanBiome(), V1_4_0),
    COLD_OCEAN(BiomeID.COLD_OCEAN, new ColdOceanBiome(), V1_4_0),
    DEEP_COLD_OCEAN(BiomeID.DEEP_COLD_OCEAN, new ColdDeepOceanBiome(), V1_4_0),
    FROZEN_OCEAN(BiomeID.FROZEN_OCEAN, new NewFrozenOceanBiome(), V1_4_0),
    DEEP_FROZEN_OCEAN(BiomeID.DEEP_FROZEN_OCEAN, new FrozenDeepOceanBiome(), V1_4_0),
    BAMBOO_JUNGLE(BiomeID.BAMBOO_JUNGLE, new BambooJungleBiome(), V1_11_0),
    BAMBOO_JUNGLE_HILLS(BiomeID.BAMBOO_JUNGLE_HILLS, new BambooJungleHillsBiome(), V1_11_0),
    //    All biomes below this comment are mutated variants of existing biomes
    SUNFLOWER_PLAINS(BiomeID.SUNFLOWER_PLAINS, new SunflowerPlainsBiome()),
    DESERT_M(BiomeID.DESERT_MUTATED, new DesertMBiome()),
    EXTREME_HILLS_M(BiomeID.EXTREME_HILLS_MUTATED, new ExtremeHillsMBiome()),
    FLOWER_FOREST(BiomeID.FLOWER_FOREST, new FlowerForestBiome()),
    TAIGA_M(BiomeID.TAIGA_MUTATED, new TaigaMBiome()),
    SWAMPLAND_M(BiomeID.SWAMPLAND_MUTATED, new SwamplandMBiome()),
    //no, the following jumps in IDs are NOT mistakes
    ICE_PLAINS_SPIKES(BiomeID.ICE_PLAINS_SPIKES, new IcePlainsSpikesBiome()),
    JUNGLE_M(BiomeID.JUNGLE_MUTATED, new JungleMBiome()),
    JUNGLE_EDGE_M(BiomeID.JUNGLE_EDGE_MUTATED, new JungleEdgeMBiome()),
    BIRCH_FOREST_M(BiomeID.BIRCH_FOREST_MUTATED, new ForestBiome(ForestBiome.TYPE_BIRCH_TALL)),
    BIRCH_FOREST_HILLS_M(BiomeID.BIRCH_FOREST_HILLS_MUTATED, new ForestHillsBiome(ForestBiome.TYPE_BIRCH_TALL)),
    ROOFED_FOREST_M(BiomeID.ROOFED_FOREST_MUTATED, new RoofedForestMBiome()),
    COLD_TAIGA_M(BiomeID.COLD_TAIGA_MUTATED, new ColdTaigaMBiome()),
    MEGA_SPRUCE_TAIGA(BiomeID.REDWOOD_TAIGA_MUTATED, new MegaSpruceTaigaBiome()),
    MEGA_SPRUCE_TAIGA_HILLS(BiomeID.REDWOOD_TAIGA_HILLS_MUTATED, new MegaSpruceTaigaHillsBiome()),
    EXTREME_HILLS_PLUS_M(BiomeID.EXTREME_HILLS_PLUS_TREES_MUTATED, new ExtremeHillsPlusMBiome()),
    SAVANNA_M(BiomeID.SAVANNA_MUTATED, new SavannaMBiome()),
    SAVANNA_PLATEAU_M(BiomeID.SAVANNA_PLATEAU_MUTATED, new SavannaPlateauMBiome()),
    MESA_BRYCE(BiomeID.MESA_BRYCE, new MesaBryceBiome()),
    MESA_PLATEAU_F_M(BiomeID.MESA_PLATEAU_STONE_MUTATED, new MesaPlateauFMBiome()),
    MESA_PLATEAU_M(BiomeID.MESA_PLATEAU_MUTATED, new MesaPlateauMBiome()),
    SOULSAND_VALLEY(BiomeID.SOULSAND_VALLEY, new SoulsandValleyBiome(), V1_16_0),
    CRIMSON_FOREST(BiomeID.CRIMSON_FOREST, new CrimsonForestBiome(), V1_16_0),
    WARPED_FOREST(BiomeID.WARPED_FOREST, new WarpedForestBiome(), V1_16_0),
    BASALT_DELTAS(BiomeID.BASALT_DELTAS, new BasaltDeltasBiome(), V1_16_0),
    JAGGED_PEAKS(BiomeID.JAGGED_PEAKS, new JaggedPeaksBiome(), V1_18_0),
    FROZEN_PEAKS(BiomeID.FROZEN_PEAKS, new FrozenPeaksBiome(), V1_18_0),
    SNOWY_SLOPES(BiomeID.SNOWY_SLOPES, new SnowySlopesBiome(), V1_18_0),
    GROVE(BiomeID.GROVE, new GroveBiome(), V1_18_0),
    MEADOW(BiomeID.MEADOW, new MeadowBiome(), V1_18_0),
    LUSH_CAVES(BiomeID.LUSH_CAVES, new LushCavesBiome(), V1_18_0),
    DRIPSTONE_CAVES(BiomeID.DRIPSTONE_CAVES, new DripstoneCavesBiome(), V1_18_0),
    STONY_PEAKS(BiomeID.STONY_PEAKS, new StonyPeaksBiome(), V1_18_0),
    DEEP_DARK(BiomeID.DEEP_DARK, new DeepDarkBiome(), V1_19_0),
    MANGROVE_SWAMP(BiomeID.MANGROVE_SWAMP, new MangroveSwampBiome(), V1_19_0),
    CHERRY_GROVE(BiomeID.CHERRY_GROVE, new CherryGroveBiome(), V1_20_0),
    ;

    public final int id;
    public final Biome biome;

    EnumBiome(int id, Biome biome) {
        this(id, biome, null);
    }

    EnumBiome(int id, Biome biome, @Nullable GameVersion version) {
        Biome.register(id, biome, version);
        this.id = id;
        this.biome = biome;
    }

    /**
     * You really shouldn't use this method if you can help it, reference the biomes directly!
     *
     * @param id biome id
     * @return biome
     */
    @Deprecated
    public static Biome getBiome(int id) {
        return Biome.getBiome(id);
    }

    /**
     * You really shouldn't use this method if you can help it, reference the biomes directly!
     *
     * @param name biome name
     * @return biome
     */
    @Deprecated
    public static Biome getBiome(String name) {
        return Biome.getBiome(name);
    }
}
