package cn.nukkit.level.generator;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.biome.Biomes;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.generator.noise.vanilla.f.NoiseGeneratorOctavesF;
import cn.nukkit.level.generator.object.ore.OreType;
import cn.nukkit.level.generator.populator.impl.*;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.Mth;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Pre 1.16
 */
public class LegacyNether implements Generator {
    private static final int HEIGHT = 128;
    private static final int SEA_LEVEL = 32;

    private final Map<String, Object> options;
    private ChunkManager level;
    private NukkitRandom random;
    private long localSeed1;
    private long localSeed2;
    private final List<Populator> populators = new ArrayList<>();
    private NoiseGeneratorOctavesF lperlinNoise1;
    private NoiseGeneratorOctavesF lperlinNoise2;
    private NoiseGeneratorOctavesF perlinNoise1;
    private NoiseGeneratorOctavesF perlinNoise3;
    private NoiseGeneratorOctavesF materialNoise;
    private final ThreadLocal<ThreadData> threadData = ThreadLocal.withInitial(ThreadData::create);

    public LegacyNether() {
        this(new Object2ObjectOpenHashMap<>());
    }

    public LegacyNether(Map<String, Object> options) {
        this.options = options;
    }

    @Override
    public int getId() {
        return GeneratorID.NETHER;
    }

    @Override
    public int getDimension() {
        return Level.DIMENSION_NETHER;
    }

    @Override
    public String getName() {
        return "nether";
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
    public void init(ChunkManager level, NukkitRandom random, GeneratorOptions generatorOptions) {
        this.level = level;
        this.random = random;
        this.random.setSeed(level.getSeed());
        Random localRandom = ThreadLocalRandom.current();
        this.localSeed1 = localRandom.nextLong();
        this.localSeed2 = localRandom.nextLong();

        this.lperlinNoise1 = new NoiseGeneratorOctavesF(random, 16);
        this.lperlinNoise2 = new NoiseGeneratorOctavesF(random, 16);
        this.perlinNoise1 = new NoiseGeneratorOctavesF(random, 8);
        this.perlinNoise3 = new NoiseGeneratorOctavesF(random, 4);
        this.materialNoise = new NoiseGeneratorOctavesF(random, 4);

        this.populators.add(new PopulatorCavesHell());

//        this.populators.add(new PopulatorNetherFortress()); //TODO

        PopulatorLava spring = new PopulatorLava();
        spring.setBaseAmount(1);
        spring.setRandomAmount(2);
        //spring.setBaseAmount(8);
        this.populators.add(spring);

        PopulatorGroundFire fire = new PopulatorGroundFire();
        fire.setBaseAmount(1);
        fire.setRandomAmount(10);
        this.populators.add(fire);

        PopulatorMushroomHell brownMushroom = new PopulatorMushroomHell(Block.BROWN_MUSHROOM);
        brownMushroom.setRandomAmount(1);
        this.populators.add(brownMushroom);

        PopulatorMushroomHell redMushroom = new PopulatorMushroomHell(Block.RED_MUSHROOM);
        redMushroom.setRandomAmount(1);
        this.populators.add(redMushroom);

        PopulatorGlowStone glowStone = new PopulatorGlowStone();
        glowStone.setBaseAmount(10);
        glowStone.setRandomAmount(10);
        this.populators.add(glowStone);

        this.populators.add(new PopulatorOre(new OreType(Block.get(BlockID.QUARTZ_ORE), 16, 14, 10, 118, NETHERRACK)));

        PopulatorLavaTrap lavaTrap = new PopulatorLavaTrap();
        lavaTrap.setBaseAmount(16);
        this.populators.add(lavaTrap);

        this.populators.add(new PopulatorOre(new OreType(Block.get(BlockID.MAGMA), 9, 28, 23, 37, NETHERRACK)));
    }

    @Override
    public void generateChunk(int chunkX, int chunkZ) {
        random.setSeed(chunkX * localSeed1 ^ chunkZ * localSeed2 ^ level.getSeed());
        BaseFullChunk chunk = level.getChunk(chunkX, chunkZ);
        chunk.fillBiome(BiomeID.HELL);
        ThreadData threadData = this.threadData.get();
        prepareHeights(chunk, chunkX, chunkZ, threadData);
        buildSurfaces(chunk, chunkX, chunkZ, threadData);
    }

    private void prepareHeights(BaseFullChunk chunk, int chunkX, int chunkZ, ThreadData threadData) {
        getHeights(chunkX << 2, 0, chunkZ << 2, threadData);
        float[] noiseBuffer = threadData.noiseBuffer;
        for (int xc = 0; xc < 4; ++xc) {
            for (int zc = 0; zc < 4; ++zc) {
                for (int yc = 0; yc < HEIGHT >> 3; ++yc) {
                    float s0 = noiseBuffer[85 * xc + 17 * zc + yc];
                    float s1 = noiseBuffer[85 * xc + 17 + 17 * zc + yc];
                    float s2 = noiseBuffer[85 * xc + 85 + 17 * zc + yc];
                    float s3 = noiseBuffer[85 * xc + 102 + 17 * zc + yc];
                    float s0a = (noiseBuffer[85 * xc + 1 + 17 * zc + yc] - s0) * 0.125f;
                    float s1a = (noiseBuffer[85 * xc + 18 + 17 * zc + yc] - s1) * 0.125f;
                    float s2a = (noiseBuffer[85 * xc + 86 + 17 * zc + yc] - s2) * 0.125f;
                    float s3a = (noiseBuffer[85 * xc + 103 + 17 * zc + yc] - s3) * 0.125f;
                    for (int y = 0; y < 8; ++y) {
                        float ss0 = s0;
                        float ss1 = s1;
                        for (int x = 0; x < 4; ++x) {
                            float val = ss0;
                            for (int z = 0; z < 4; ++z) {
                                int worldY = yc << 3 | y;
                                int block = Block.AIR;
                                if (val > 0) {
                                    block = Block.NETHERRACK;
                                } else if (worldY < SEA_LEVEL) {
                                    block = Block.LAVA;
                                }
                                if (block != Block.AIR) {
                                    chunk.setBlock(0, xc << 2 | x, worldY, zc << 2 | z, block);
                                }
                                val += (ss1 - ss0) * 0.25f;
                            }
                            ss0 += (s2 - s0) * 0.25f;
                            ss1 += (s3 - s1) * 0.25f;
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
        float[] noiseRegionPrimary = perlinNoise1.generateNoiseOctaves(threadData.noiseRegionPrimary, x, y, z, 5, 17, 5, 684.41199f / 80, 2053.2358f / 60, 684.41199f / 80);
        float[] noiseRegionA = lperlinNoise1.generateNoiseOctaves(threadData.noiseRegionA, x, y, z, 5, 17, 5, 684.41199f, 2053.2358f, 684.41199f);
        float[] noiseRegionB = lperlinNoise2.generateNoiseOctaves(threadData.noiseRegionB, x, y, z, 5, 17, 5, 684.41199f, 2053.2358f, 684.41199f);
        float[] yoffs = threadData.yoffs;
        for (int yy = 0; yy < 17; ++yy) {
            yoffs[yy] = Mth.cos(yy * Mth.PI * 6 / 17) * 2;
            float dd = yy;
            if (yy > 8) {
                dd = 16 - yy;
            }
            if (dd < 4) {
                yoffs[yy] -= Mth.cube(4 - dd) * 10;
            }
        }
        int p = 0;
        for (int xx = 0; xx < 5; ++xx) {
            for (int zz = 0; zz < 5; ++zz) {
                for (int yy = 0; yy < 17; ++yy) {
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
                    float vala = val - yoffs[yy];
                    if (yy > 13) {
                        float slide = (yy - 13) / 3f;
                        vala = vala * (1 - slide) + -10 * slide;
                    }
                    noiseBuffer[p++] = vala;
                }
            }
        }
    }

    private void buildSurfaces(BaseFullChunk chunk, int chunkX, int chunkZ, ThreadData threadData) {
        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;
        float[] depthBuffer = threadData.depthBuffer;
        perlinNoise3.generateNoiseOctaves(depthBuffer, baseX, 0, baseZ, 16, 16, 1, 2, 2, 2);
        int seaLevel = HEIGHT - 64;
        for (int localX = 0; localX < 16; ++localX) {
            int worldX = baseX | localX;
            for (int localZ = 0; localZ < 16; ++localZ) {
                int worldZ = baseZ | localZ;
                float depthValue = depthBuffer[localZ << 4 | localZ];
                float sandNoise = materialNoise.getValue(worldX, worldZ, 0);
                float gravelNoise = materialNoise.getValue(worldX, 109, worldZ);
                boolean sand = sandNoise + random.nextFloat() * 0.2f > 0;
                boolean gravel = gravelNoise + random.nextFloat() * 0.2f > 0;
                int runDepth = (int) (depthValue / 3 + 3 + random.nextFloat() * 0.25f);
                int run = -1;
                int top = Block.NETHERRACK;
                int material = Block.NETHERRACK;
                int maxY = HEIGHT - 1;
                for (int y = maxY; y >= 0; --y) {
                    int randUpperHeight = maxY - random.nextInt(5);
                    int randLowerHeight = random.nextInt(5);
                    if (y >= randUpperHeight || y <= randLowerHeight) {
                        chunk.setBlock(0, localX, y, localZ, Block.BEDROCK);
                        continue;
                    }
                    int block = chunk.getBlockId(0, localX, y, localZ);
                    if (block == Block.AIR) {
                        run = -1;
                        continue;
                    }
                    if (block != Block.NETHERRACK) {
                        continue;
                    }
                    if (run == -1) {
                        if (runDepth > 0) {
                            if (y >= seaLevel - 4 && y <= seaLevel + 1) {
                                top = Block.NETHERRACK;
                                material = Block.NETHERRACK;
                                if (sand) {
                                    top = Block.SOUL_SAND;
                                    material = Block.SOUL_SAND;
                                } else if (gravel) {
                                    top = Block.GRAVEL;
                                }
                            }
                        } else {
                            top = Block.AIR;
                            material = Block.NETHERRACK;
                        }
                        if (y < seaLevel) {
                            if (top == Block.AIR) {
                                top = Block.LAVA;
                            }
                        }
                        run = runDepth;
                        chunk.setBlock(0, localX, y, localZ, y < seaLevel - 1 ? material : top);
                    } else if (run > 0) {
                        --run;
                        chunk.setBlock(0, localX, y, localZ, material);
                    }
                }
            }
        }
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

    @Override
    public Vector3 getSpawn() {
        return new Vector3(0.5, 64, 0.5);
    }

    private record ThreadData(
            float[] depthBuffer,
            float[] noiseBuffer,
            float[] noiseRegionPrimary,
            float[] noiseRegionA,
            float[] noiseRegionB,
            float[] yoffs
    ) {
        static ThreadData create() {
            return new ThreadData(
                    new float[16 * 16],
                    new float[5 * 17 * 5],
                    new float[5 * 17 * 5],
                    new float[5 * 17 * 5],
                    new float[5 * 17 * 5],
                    new float[17]
            );
        }
    }
}
