package cn.nukkit.level.generator;

import cn.nukkit.block.*;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.biome.Biomes;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.generator.noise.nukkit.f.SimplexF;
import cn.nukkit.level.generator.object.ore.OreType;
import cn.nukkit.level.generator.populator.impl.PopulatorGlowStone;
import cn.nukkit.level.generator.populator.impl.PopulatorGroundFire;
import cn.nukkit.level.generator.populator.impl.PopulatorLava;
import cn.nukkit.level.generator.populator.impl.PopulatorOre;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Nether implements Generator {
    private final Map<String, Object> options;
    private ChunkManager level;
    private NukkitRandom nukkitRandom;

    private static final int lavaHeight = 32;
    private static final int bedrockDepth = 5;

    private final SimplexF[] noiseGen = new SimplexF[3];
    private final List<Populator> populators = new ObjectArrayList<>();
    private final List<Populator> generationPopulators = new ObjectArrayList<>();

    private long localSeed1;
    private long localSeed2;

    public Nether() {
        this(new Object2ObjectOpenHashMap<>(1));
    }

    public Nether(Map<String, Object> options) {
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
        return this.options;
    }

    @Override
    public ChunkManager getChunkManager() {
        return level;
    }

    @Override
    public void init(ChunkManager level, NukkitRandom random, GeneratorOptions generatorOptions) {
        this.level = level;
        this.nukkitRandom = random;
        this.nukkitRandom.setSeed(this.level.getSeed());

        for (int i = 0; i < noiseGen.length; i++)   {
            noiseGen[i] = new SimplexF(nukkitRandom, 4, 1 / 4f, 1 / 64f);
        }

        this.nukkitRandom.setSeed(this.level.getSeed());
        Random localRandom = ThreadLocalRandom.current();
        this.localSeed1 = localRandom.nextLong();
        this.localSeed2 = localRandom.nextLong();

        PopulatorOre ores = new PopulatorOre(
                new OreType(Block.get(BlockID.QUARTZ_ORE), 20, 16, 0, 128, NETHERRACK),
                new OreType(Block.get(BlockID.SOUL_SAND), 5, 64, 0, 128, NETHERRACK),
                new OreType(Block.get(BlockID.GRAVEL), 5, 64, 0, 128, NETHERRACK),
                new OreType(Block.get(BlockID.FLOWING_LAVA), 1, 16, 0, lavaHeight, NETHERRACK)
        );
        this.populators.add(ores);

        PopulatorGroundFire groundFire = new PopulatorGroundFire();
        groundFire.setBaseAmount(1);
        groundFire.setRandomAmount(1);
        this.populators.add(groundFire);

        PopulatorLava lava = new PopulatorLava();
        lava.setBaseAmount(1);
        lava.setRandomAmount(2);
        this.populators.add(lava);

        PopulatorGlowStone glowStone = new PopulatorGlowStone();
        glowStone.setBaseAmount(10);
        glowStone.setRandomAmount(10);
        this.populators.add(glowStone);

        PopulatorOre ore = new PopulatorOre(
                new OreType(Block.get(BlockID.QUARTZ_ORE), 40, 16, 0, 128, NETHERRACK),
                new OreType(Block.get(BlockID.SOUL_SAND), 1, 64, 30, 35, NETHERRACK),
                new OreType(Block.get(BlockID.FLOWING_LAVA), 32, 1, 0, 32, NETHERRACK),
                new OreType(Block.get(BlockID.MAGMA), 32, 16, 26, 37, NETHERRACK)
        );
        this.populators.add(ore);
    }

    @Override
    public void generateChunk(int chunkX, int chunkZ) {
        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;
        this.nukkitRandom.setSeed(chunkX * localSeed1 ^ chunkZ * localSeed2 ^ this.level.getSeed());

        BaseFullChunk chunk = level.getChunk(chunkX, chunkZ);

        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                chunk.setBlock(0, x, 0, z, Block.BEDROCK);
                for (int y = 115; y < 127; ++y) {
                    chunk.setBlock(0, x, y, z, Block.NETHERRACK);
                }
                chunk.setBlock(0, x, 127, z, Block.BEDROCK);
                for (int y = 1; y < 127; ++y) {
                    if (getNoise(baseX | x, y, baseZ | z) > 0) {
                        chunk.setBlock(0, x, y, z, Block.NETHERRACK);
                    } else if (y <= lavaHeight) {
                        chunk.setBlock(0, x, y, z, Block.LAVA);
                        chunk.setBlockLight(x, y + 1, z, 15);
                    }
                }
            }
        }

        for (Populator populator : this.generationPopulators) {
            populator.populate(this.level, chunkX, chunkZ, this.nukkitRandom, chunk);
        }
    }

    @Override
    public void populateChunk(int chunkX, int chunkZ) {
        BaseFullChunk chunk = level.getChunk(chunkX, chunkZ);
        this.nukkitRandom.setSeed(0xdeadbeef ^ (chunkX << 8) ^ chunkZ ^ this.level.getSeed());

        chunk.fillBiome(BiomeID.HELL);

        for (Populator populator : this.populators) {
            populator.populate(this.level, chunkX, chunkZ, this.nukkitRandom, chunk);
        }

        Biome biome = Biomes.get(chunk.getBiomeId(7, 7));
        biome.populateChunk(this.level, chunkX, chunkZ, this.nukkitRandom);
    }

    public Vector3 getSpawn() {
        return new Vector3(0.5, 64, 0.5);
    }

    public float getNoise(int x, int y, int z)  {
        float val = 0f;
        for (int i = 0; i < noiseGen.length; i++)   {
            val += noiseGen[i].noise3D(x >> i, y, z >> i, true);
        }
        return val;
    }
}
