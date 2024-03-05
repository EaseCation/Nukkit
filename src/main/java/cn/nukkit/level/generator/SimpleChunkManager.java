package cn.nukkit.level.generator;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class SimpleChunkManager implements ChunkManager {

    protected long seed;

    public SimpleChunkManager(long seed) {
        this.seed = seed;
    }

    @Override
    public int getBlockIdAt(int layer, int x, int y, int z) {
        FullChunk chunk = this.getChunk(x >> 4, z >> 4);
        if (chunk != null) {
            return chunk.getBlockId(layer, x & 0xf, y, z & 0xf);
        }
        return 0;
    }

    @Override
    public void setBlockIdAt(int layer, int x, int y, int z, int id) {
        FullChunk chunk = this.getChunk(x >> 4, z >> 4);
        if (chunk != null) {
            chunk.setBlockId(layer, x & 0xf, y, z & 0xf, id);
        }
    }

    @Override
    public void setBlockAt(int layer, int x, int y, int z, int id, int data) {
        FullChunk chunk = this.getChunk(x >> 4, z >> 4);
        if (chunk != null) {
            chunk.setBlock(layer, x & 0xf, y, z & 0xf, id, data);
        }
    }


    @Override
    public void setBlockFullIdAt(int layer, int x, int y, int z, int fullId) {
        FullChunk chunk = this.getChunk(x >> 4, z >> 4);
        if (chunk != null) {
            chunk.setFullBlockId(layer, x & 0xf, y, z & 0xf, fullId);
        }
    }

    @Override
    public int getBlockDataAt(int layer, int x, int y, int z) {
        FullChunk chunk = this.getChunk(x >> 4, z >> 4);
        if (chunk != null) {
            return chunk.getBlockData(layer, x & 0xf, y, z & 0xf);
        }
        return 0;
    }

    @Override
    public void setBlockDataAt(int layer, int x, int y, int z, int data) {
        FullChunk chunk = this.getChunk(x >> 4, z >> 4);
        if (chunk != null) {
            chunk.setBlockData(layer, x & 0xf, y, z & 0xf, data);
        }
    }

    @Override
    public void setChunk(int chunkX, int chunkZ) {
        this.setChunk(chunkX, chunkZ, null);
    }

    @Override
    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public void cleanChunks(long seed) {
        this.seed = seed;
    }
}
