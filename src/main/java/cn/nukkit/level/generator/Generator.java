package cn.nukkit.level.generator;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Level;
import cn.nukkit.math.RandomSource;
import cn.nukkit.math.Vector3;

import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface Generator extends BlockID {
    int getId();

    default int getDimension() {
        return Level.DIMENSION_OVERWORLD;
    }

    void init(ChunkManager level, RandomSource random, GeneratorOptions generatorOptions);

    void generateChunk(int chunkX, int chunkZ);

    void populateChunk(int chunkX, int chunkZ);

    Map<String, Object> getSettings();

    String getName();

    Vector3 getSpawn();

    ChunkManager getChunkManager();
}
