package cn.nukkit.level.generator;

import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.math.Vector3;

import java.util.Map;

public class Old extends Normal {
    public Old() {
    }

    public Old(Map<String, Object> options) {
        super(options);
    }

    @Override
    public int getId() {
        return TYPE_OLD;
    }

    @Override
    public String getName() {
        return "old";
    }

    @Override
    public Vector3 getSpawn() {
        return new Vector3(127.5, 256, 127.5);
    }

    @Override
    public Biome pickBiome(int x, int z) {
        return super.pickBiome(x << 2, z << 2);
    }

    @Override
    protected void initUnderground() {
    }

    @Override
    public void generateChunk(int chunkX, int chunkZ) {
        if (isInvisibleBedrockChunk(chunkX, chunkZ)) {
            BaseFullChunk chunk = getChunkManager().getChunk(chunkX, chunkZ);
            for (int y = 0; y < /*128*/256; y++) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        chunk.setBlock(0, x, y, z, INVISIBLE_BEDROCK);
                    }
                }
            }
            return;
        }

        super.generateChunk(chunkX, chunkZ);
    }

    @Override
    public void populateChunk(int chunkX, int chunkZ) {
        if (isInvisibleBedrockChunk(chunkX, chunkZ)) {
            return;
        }

        super.populateChunk(chunkX, chunkZ);
    }

    private static boolean isInvisibleBedrockChunk(int chunkX, int chunkZ) {
        return chunkX < 0 || chunkX >= 16 || chunkZ < 0 || chunkZ >= 16;
    }
}
