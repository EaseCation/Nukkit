package cn.nukkit.level.generator;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.HeightRange;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.generator.FlatGeneratorOptions.BlockLayer;
import cn.nukkit.level.generator.FlatGeneratorOptions.WorldVersion;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Flat extends Generator {
    private final Map<String, Object> options;
    private ChunkManager level;
    private NukkitRandom random;
    private FlatGeneratorOptions flatOptions;

    public Flat() {
        this(new Object2ObjectOpenHashMap<>(4));
    }

    public Flat(Map<String, Object> options) {
        this.options = options;
    }

    @Override
    public int getId() {
        return TYPE_FLAT;
    }

    @Override
    public String getName() {
        return "flat";
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
        this.random = random;
        this.flatOptions = generatorOptions.getFlatOptions();
    }

    @Override
    public void generateChunk(int chunkX, int chunkZ) {
        BaseFullChunk chunk = level.getChunk(chunkX, chunkZ);

        chunk.fillBiome(flatOptions.getBiomeId());

        HeightRange heightRange = chunk.getHeightRange();
        int maxY = heightRange.getMaxY();
        int y = heightRange.getMinY();
        for (BlockLayer layer : flatOptions.getBlockLayers()) {
            int blockId = layer.blockId();
            int blockData = layer.blockData();
            for (int i = 0; i < layer.numLayers(); i++) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        chunk.setBlock(0, x, y, z, blockId, blockData);
                    }
                }
                if (++y >= maxY) {
                    return;
                }
            }
        }
    }

    @Override
    public void populateChunk(int chunkX, int chunkZ) {
        BaseFullChunk chunk = level.getChunk(chunkX, chunkZ);
        this.random.setSeed(0xdeadbeefL ^ ((long) chunkX << 8) ^ chunkZ ^ this.level.getSeed());
        for (Populator populator : flatOptions.getStructures()) {
            populator.populate(this.level, chunkX, chunkZ, this.random, chunk);
        }
    }

    @Override
    public Vector3 getSpawn() {
        int offset = flatOptions.getWorldVersion() == WorldVersion.POST_1_18 ? -64 : 0;
        return new Vector3(0.5, flatOptions.getTotalLayers() + offset, 0.5);
    }
}
