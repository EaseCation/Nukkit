package cn.nukkit.level.generator;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Map;

//TODO
public class End extends Generator {
    private final Map<String, Object> options;
    private ChunkManager level;

    public End() {
        this(new Object2ObjectOpenHashMap<>(1));
    }

    public End(Map<String, Object> options) {
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
        return TYPE_END;
    }

    @Override
    public int getDimension() {
        return Level.DIMENSION_THE_END;
    }

    @Override
    public String getName() {
        return "end";
    }

    @Override
    public Vector3 getSpawn() {
        return new Vector3(100, 50, 0);
    }

    @Override
    public void init(ChunkManager level, NukkitRandom random) {
        this.level = level;
    }

    @Override
    public void generateChunk(int chunkX, int chunkZ) {
        //TODO
        if (chunkX >= -1 && chunkX < 1 && chunkZ >= -1 && chunkZ < 1) {
            BaseFullChunk chunk = getChunkManager().getChunk(chunkX, chunkZ);
            for (int y = 16; y < 48; y++) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        chunk.setBlock(0, x, y, z, END_STONE);
                    }
                }
            }
        }
    }

    @Override
    public void populateChunk(int chunkX, int chunkZ) {
        //TODO
    }
}
