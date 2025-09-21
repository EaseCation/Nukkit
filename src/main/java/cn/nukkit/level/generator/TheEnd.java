package cn.nukkit.level.generator;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.biome.Biomes;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.generator.noise.vanilla.f.NoiseGeneratorOctavesF;
import cn.nukkit.level.generator.noise.vanilla.f.NoiseGeneratorSimplexF;
import cn.nukkit.level.generator.populator.impl.*;
import cn.nukkit.level.generator.populator.impl.PopulatorSpike.EndSpike;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.Mth;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TheEnd implements Generator {
    private final Map<String, Object> options;
    private ChunkManager level;
    private NukkitRandom random;
    private final List<Populator> populators = new ArrayList<>();
    private NoiseGeneratorOctavesF lperlinNoise1;
    private NoiseGeneratorOctavesF lperlinNoise2;
    private NoiseGeneratorOctavesF perlinNoise1;
    private NoiseGeneratorSimplexF islandNoise;
    private final ThreadLocal<ThreadData> threadData = ThreadLocal.withInitial(ThreadData::create);

    public TheEnd() {
        this(new Object2ObjectOpenHashMap<>());
    }

    public TheEnd(Map<String, Object> options) {
        this.options = options;
    }

    @Override
    public Map<String, Object> getSettings() {
        return options;
    }

    @Override
    public ChunkManager getChunkManager() {
        return level;
    }

    @Override
    public int getId() {
        return GeneratorID.THE_END;
    }

    @Override
    public int getDimension() {
        return Level.DIMENSION_THE_END;
    }

    @Override
    public String getName() {
        return "the_end";
    }

    @Override
    public Vector3 getSpawn() {
        return new Vector3(100, 50, 0);
    }

    @Override
    public void init(ChunkManager level, NukkitRandom random, GeneratorOptions generatorOptions) {
        this.level = level;
        this.random = random;
        this.random.setSeed(level.getSeed());

        this.lperlinNoise1 = new NoiseGeneratorOctavesF(random, 16);
        this.lperlinNoise2 = new NoiseGeneratorOctavesF(random, 16);
        this.perlinNoise1 = new NoiseGeneratorOctavesF(random, 8);
        this.islandNoise = new NoiseGeneratorSimplexF(random);

//        this.populators.add(new PopulatorEndCity()); //TODO

        for (EndSpike spike : PopulatorSpike.getSpikesForLevel(level)) {
            this.populators.add(new PopulatorSpike(spike));
        }

        this.populators.add(new PopulatorEndIsland(this));

        this.populators.add(new PopulatorChorusFlower(this));

        this.populators.add(new PopulatorEndGateway(this));

        this.populators.add(new PopulatorEndPodium());

        this.populators.add(new PopulatorEndPlatform());
    }

    @Override
    public void generateChunk(int chunkX, int chunkZ) {
        BaseFullChunk chunk = level.getChunk(chunkX, chunkZ);
        chunk.fillBiome(BiomeID.THE_END);
        ThreadData threadData = this.threadData.get();
        prepareHeights(chunk, chunkX, chunkZ, threadData);
    }

    private void prepareHeights(BaseFullChunk chunk, int chunkX, int chunkZ, ThreadData threadData) {
        getHeights(chunkX << 1, 0, chunkZ << 1, threadData);
        float[] noiseBuffer = threadData.noiseBuffer;
        for (int xc = 0; xc < 2; ++xc) {
            for (int zc = 0; zc < 2; ++zc) {
                for (int yc = 0; yc < 32; ++yc) {
                    float s0 = noiseBuffer[99 * xc + 33 * zc + yc];
                    float s1 = noiseBuffer[99 * xc + 33 + 33 * zc + yc];
                    float s2 = noiseBuffer[99 * xc + 99 + 33 * zc + yc];
                    float s3 = noiseBuffer[99 * xc + 132 + 33 * zc + yc];
                    float s0a = (noiseBuffer[99 * xc + 1 + 33 * zc + yc] - s0) * 0.25f;
                    float s1a = (noiseBuffer[99 * xc + 34 + 33 * zc + yc] - s1) * 0.25f;
                    float s2a = (noiseBuffer[99 * xc + 100 + 33 * zc + yc] - s2) * 0.25f;
                    float s3a = (noiseBuffer[99 * xc + 133 + 33 * zc + yc] - s3) * 0.25f;
                    for (int y = 0; y < 4; ++y) {
                        float ss0 = s0;
                        float ss1 = s1;
                        for (int x = 0; x < 8; ++x) {
                            float val = ss0;
                            for (int z = 0; z < 8; ++z) {
                                if (val > 0) {
                                    chunk.setBlock(0, xc << 3 | x, yc << 2 | y, zc << 3 | z, Block.END_STONE);
                                }
                                val += (ss1 - ss0) * 0.125f;
                            }
                            ss0 += (s2 - s0) * 0.125f;
                            ss1 += (s3 - s1) * 0.125f;
                        }
                        s0 += s0a;
                        s1 += s1a;
                        s2 += s2a;
                        s3 += s3a;
                    }
                }
            }
        }
    }

    private void getHeights(int x, int y, int z, ThreadData threadData) {
        float[] noiseBuffer = threadData.noiseBuffer;
        float[] noiseRegionPrimary = perlinNoise1.generateNoiseOctaves(threadData.noiseRegionPrimary, x, y, z, 3, 33, 3, 1368.824f / 80, 684.41199f / 160, 1368.824f / 80);
        float[] noiseRegionA = lperlinNoise1.generateNoiseOctaves(threadData.noiseRegionA, x, y, z, 3, 33, 3, 1368.824f, 684.41199f, 1368.824f);
        float[] noiseRegionB = lperlinNoise2.generateNoiseOctaves(threadData.noiseRegionB, x, y, z, 3, 33, 3, 1368.824f, 684.41199f, 1368.824f);
        int p = 0;
        for (int xx = 0; xx < 3; ++xx) {
            for (int zz = 0; zz < 3; ++zz) {
                float doffs = getIslandHeightValue(x / 2, z / 2, xx, zz);
                for (int yy = 0; yy < 33; ++yy) {
                    float v = (noiseRegionPrimary[p] / 10 + 1) / 2;
                    float val;
                    if (v >= 0) {
                        if (v <= 1) {
                            float bb = noiseRegionA[p] / 512;
                            val = bb + (noiseRegionB[p] / 512 - bb) * v;
                        } else {
                            val = noiseRegionB[p] / 512;
                        }
                    } else {
                        val = noiseRegionA[p] / 512;
                    }
                    float vala = doffs + val - 8;
                    if (yy > 14) {
                        float slide = Mth.clamp((yy - 14) / 64f, 0, 1);
                        vala = vala * (1 - slide) + -3000 * slide;
                    } else if (yy < 8) {
                        float slide = (8 - yy) / (8 - 1f);
                        vala = vala * (1 - slide) + -30 * slide;
                    }
                    noiseBuffer[p++] = vala;
                }
            }
        }
    }

    public float getIslandHeightValue(int chunkX, int chunkZ, int subSectionX, int subSectionZ) {
        float doffs = Mth.clamp(100 - (float) Math.sqrt(Mth.square(subSectionX + (chunkX << 1)) + Mth.square(subSectionZ + (chunkZ << 1))) * 8, -100, 80);
        for (int xo = -12; xo <= 12; ++xo) {
            for (int zo = -12; zo <= 12; ++zo) {
                long totalChunkX = chunkX + xo;
                long totalChunkZ = chunkZ + zo;
                if (Mth.square(totalChunkX) + Mth.square(totalChunkZ) <= 4096) {
                    continue;
                }
                if (islandNoise.getValue(totalChunkX, totalChunkZ) >= -0.9) {
                    continue;
                }
                float newDoffs = Mth.clamp(100 - (float) Math.sqrt(Mth.square(subSectionX - (xo << 1)) + Mth.square(subSectionZ - (zo << 1))) * ((3439 * Math.abs(totalChunkX) + 147 * Math.abs(totalChunkZ)) % 13 + 9), -100, 80);
                if (newDoffs > doffs) {
                    doffs = newDoffs;
                }
            }
        }
        return doffs;
    }

    @Override
    public void populateChunk(int chunkX, int chunkZ) {
        BaseFullChunk chunk = level.getChunk(chunkX, chunkZ);
        random.setSeed(0xdeadbeef ^ (chunkX << 8) ^ chunkZ ^ level.getSeed());

        for (Populator populator : populators) {
            populator.populate(level, chunkX, chunkZ, random, chunk);
        }

        Biome biome = Biomes.get(chunk.getBiomeId(7, 7));
        biome.populateChunk(level, chunkX, chunkZ, random);
    }

    private record ThreadData(
            float[] noiseBuffer,
            float[] noiseRegionPrimary,
            float[] noiseRegionA,
            float[] noiseRegionB
    ) {
        static ThreadData create() {
            return new ThreadData(
                    new float[3 * 33 * 3],
                    new float[3 * 33 * 3],
                    new float[3 * 33 * 3],
                    new float[3 * 33 * 3]
            );
        }
    }
}
