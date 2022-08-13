package cn.nukkit.level;

import cn.nukkit.level.format.generic.BaseFullChunk;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface ChunkManager {

    int getBlockIdAt(int layer, int x, int y, int z);

    void setBlockFullIdAt(int layer, int x, int y, int z, int fullId);

    void setBlockIdAt(int layer, int x, int y, int z, int id);

    default void setBlockAt(int layer, int x, int y, int z, int id) {
        setBlockAt(layer, x, y, z, id, 0);
    }

    void setBlockAt(int layer, int x, int y, int z, int id, int data);

    int getBlockDataAt(int layer, int x, int y, int z);

    void setBlockDataAt(int layer, int x, int y, int z, int data);

    BaseFullChunk getChunk(int chunkX, int chunkZ);

    void setChunk(int chunkX, int chunkZ);

    void setChunk(int chunkX, int chunkZ, BaseFullChunk chunk);

    long getSeed();
}
