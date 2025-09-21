package cn.nukkit.level.generator;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Map;

public class Void implements Generator {
    private final Map<String, Object> options;
    private ChunkManager level;

    public Void() {
        this(new Object2ObjectOpenHashMap<>(1));
    }

    public Void(Map<String, Object> options) {
        this.options = options;
    }

    @Override
    public int getId() {
        return GeneratorID.VOID;
    }

    @Override
    public void init(ChunkManager level, NukkitRandom random, GeneratorOptions generatorOptions) {
        this.level = level;
    }

    @Override
    public void generateChunk(int chunkX, int chunkZ) {
    }

    @Override
    public void populateChunk(int chunkX, int chunkZ) {
    }

    @Override
    public Map<String, Object> getSettings() {
        return options;
    }

    @Override
    public String getName() {
        return "void";
    }

    @Override
    public Vector3 getSpawn() {
        return new Vector3(0.5, 4, 0.5);
    }

    @Override
    public ChunkManager getChunkManager() {
        return level;
    }
}
