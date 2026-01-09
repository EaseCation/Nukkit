package cn.nukkit.level.biome;

import cn.nukkit.level.generator.noise.nukkit.f.SimplexF;
import cn.nukkit.math.RandomSource;

/**
 * @author DaPorkchop_
 * Nukkit Project
 */
public class BiomeSelector {

    private final SimplexF temperature;
    private final SimplexF rainfall;
    private final SimplexF river;
    private final SimplexF ocean;
    private final SimplexF hills;

    public BiomeSelector(RandomSource random) {
        this.temperature = new SimplexF(random, 2F, 1F / 8F, 1F / 2048f);
        this.rainfall = new SimplexF(random, 2F, 1F / 8F, 1F / 2048f);
        this.river = new SimplexF(random, 6f, 2 / 4f, 1 / 1024f);
        this.ocean = new SimplexF(random, 6f, 2 / 4f, 1 / 2048f);
        this.hills = new SimplexF(random, 2f, 2 / 4f, 1 / 2048f);
    }

    public Biome pickBiome(int x, int z) {
        float noiseOcean = ocean.noise2D(x, z, true);
        float noiseRiver = river.noise2D(x, z, true);
        float temperature = this.temperature.noise2D(x, z, true);
        float rainfall = this.rainfall.noise2D(x, z, true);
        Biome biome;
        if (noiseOcean < -0.15f)    {
            if (noiseOcean < -0.91f)    {
                if (noiseOcean < -0.92f) {
                    biome = Biomes.MUSHROOM_ISLAND;
                } else {
                    biome = Biomes.MUSHROOM_ISLAND_SHORE;
                }
            } else {
                if (rainfall < 0f)  {
                    if (temperature < -0.45f) {
                        biome = Biomes.FROZEN_OCEAN;
                    } else if (temperature < -0.15f) {
                        biome = Biomes.COLD_OCEAN;
                    } else if (temperature < 0.2f) {
                        biome = Biomes.OCEAN;
                    } else if (temperature < 0.55f) {
                        biome = Biomes.LUKEWARM_OCEAN;
                    } else {
                        biome = Biomes.WARM_OCEAN;
                    }
                } else {
                    if (temperature < -0.45f) {
                        biome = Biomes.DEEP_FROZEN_OCEAN;
                    } else if (temperature < -0.15f) {
                        biome = Biomes.DEEP_COLD_OCEAN;
                    } else if (temperature < 0.2f) {
                        biome = Biomes.DEEP_OCEAN;
                    } else if (temperature < 0.55f) {
                        biome = Biomes.DEEP_LUKEWARM_OCEAN;
                    } else {
                        biome = Biomes.WARM_OCEAN;
                    }
                }
            }
        } else if (Math.abs(noiseRiver) < 0.04f) {
            if (temperature < -0.3f)    {
                biome = Biomes.FROZEN_RIVER;
            } else {
                biome = Biomes.RIVER;
            }
        } else {
            float hills = this.hills.noise2D(x, z, true);
            if (temperature < -0.379f) {
                //freezing
                if (noiseOcean < -0.12f) {
                    biome = Biomes.COLD_BEACH;
                } else if (rainfall < 0f) {
                    if (hills < -0.1f) {
                        biome = Biomes.COLD_TAIGA;
                    } else if (hills < 0.5f) {
                        biome = Biomes.COLD_TAIGA_HILLS;
                    } else {
                        biome = Biomes.COLD_TAIGA_MUTATED;
                    }
                } else {
                    if (hills < 0) {
                        biome = Biomes.ICE_MOUNTAINS;
                    } else if (hills < 0.7f) {
                        biome = Biomes.ICE_PLAINS;
                    } else {
                        biome = Biomes.ICE_PLAINS_SPIKES;
                    }
                }
            } else if (noiseOcean < -0.12f) {
                if (hills < -0.2225f) {
                    biome = Biomes.STONE_BEACH;
                } else {
                    biome = Biomes.BEACH;
                }
            } else if (temperature < 0f)    {
                //cold
                if (hills < 0.2f)    {
                    if (rainfall < -0.5f)   {
                        biome = Biomes.EXTREME_HILLS_MUTATED;
                    } else if (rainfall > 0.5f) {
                        biome = Biomes.EXTREME_HILLS_PLUS_TREES_MUTATED;
                    } else if (rainfall < 0f)   {
                        biome = Biomes.EXTREME_HILLS;
                    } else {
                        biome = Biomes.EXTREME_HILLS_PLUS_TREES;
                    }
                } else {
                    if (rainfall < -0.6f)    {
                        if (hills < 0.6f) {
                            biome = Biomes.MEGA_TAIGA_HILLS;
                        } else {
                            biome = Biomes.MEGA_TAIGA;
                        }
                    } else if (rainfall > 0.6f)   {
                        if (hills < 0.6f) {
                            biome = Biomes.REDWOOD_TAIGA_HILLS_MUTATED;
                        } else {
                            biome = Biomes.REDWOOD_TAIGA_MUTATED;
                        }
                    } else if (rainfall < 0.2f)  {
                        if (hills < 0.6f) {
                            biome = Biomes.TAIGA_HILLS;
                        } else {
                            biome = Biomes.TAIGA;
                        }
                    } else {
                        biome = Biomes.TAIGA_MUTATED;
                    }
                }
            } else if (temperature < 0.5f)  {
                //normal
                if (temperature < 0.25f) {
                    if (rainfall < 0f)  {
                        if (noiseOcean < 0f){
                            biome = Biomes.SUNFLOWER_PLAINS;
                        } else {
                            biome = Biomes.PLAINS;
                        }
                    } else if (rainfall < 0.25f)    {
                        if (noiseOcean < 0f)    {
                            biome = Biomes.FLOWER_FOREST;
                        } else {
                            if (hills < 0) {
                                biome = Biomes.FOREST_HILLS;
                            } else {
                                biome = Biomes.FOREST;
                            }
                        }
                    } else {
                        if (noiseOcean < 0f)    {
                            if (hills < 0) {
                                biome = Biomes.BIRCH_FOREST_HILLS_MUTATED;
                            } else {
                                biome = Biomes.BIRCH_FOREST_MUTATED;
                            }
                        } else {
                            if (hills < 0) {
                                biome = Biomes.BIRCH_FOREST_HILLS;
                            } else {
                                biome = Biomes.BIRCH_FOREST;
                            }
                        }
                    }
                } else {
                    if (rainfall < -0.2f)   {
                        if (noiseOcean < 0f)    {
                            biome = Biomes.SWAMPLAND_MUTATED;
                        } else {
                            biome = Biomes.SWAMPLAND;
                        }
                    } else if (rainfall > 0.1f) {
                        if (noiseOcean < 0.155f)  {
                            biome = Biomes.JUNGLE_MUTATED;
                        } else {
                            if (hills < 0) {
                                if (rainfall < 0.2f) {
                                    biome = Biomes.BAMBOO_JUNGLE_HILLS;
                                } else {
                                    biome = Biomes.JUNGLE_HILLS;
                                }
                            } else {
                                if (rainfall < 0.2f) {
                                    biome = Biomes.BAMBOO_JUNGLE;
                                } else {
                                    biome = Biomes.JUNGLE;
                                }
                            }
                        }
                    } else {
                        if (noiseOcean < 0f)    {
                            biome = Biomes.ROOFED_FOREST_MUTATED;
                        } else {
                            biome = Biomes.ROOFED_FOREST;
                        }
                    }
                }
            } else {
                //hot
                if (rainfall < 0f)  {
                    if (noiseOcean < 0f)    {
                        biome = Biomes.DESERT_MUTATED;
                    } else if (hills < 0f)    {
                        biome = Biomes.DESERT_HILLS;
                    } else {
                        biome = Biomes.DESERT;
                    }
                } else if (rainfall > 0.4f)   {
                    if (noiseOcean < 0.155f)    {
                        if (hills < 0f) {
                            biome = Biomes.SAVANNA_PLATEAU_MUTATED;
                        } else {
                            biome = Biomes.SAVANNA_MUTATED;
                        }
                    } else {
                        if (hills < 0f) {
                            biome = Biomes.SAVANNA_PLATEAU;
                        } else {
                            biome = Biomes.SAVANNA;
                        }
                    }
                } else {
                    if (noiseOcean < 0f)    {
                        if (hills < 0f) {
                            biome = Biomes.MESA_PLATEAU_STONE;
                        } else {
                            biome = Biomes.MESA_PLATEAU_STONE_MUTATED;
                        }
                    } else if (hills < 0f)  {
                        if (noiseOcean < 0.2f)    {
                            biome = Biomes.MESA_PLATEAU_MUTATED;
                        } else {
                            biome = Biomes.MESA_PLATEAU;
                        }
                    } else {
                        if (noiseOcean < 0.1f)  {
                            biome = Biomes.MESA_BRYCE;
                        } else {
                            biome = Biomes.MESA;
                        }
                    }
                }
            }
        }

        return biome;
    }
}
