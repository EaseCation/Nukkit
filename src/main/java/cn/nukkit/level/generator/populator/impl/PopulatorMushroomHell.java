package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.HeightRange;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.type.PopulatorCount;
import cn.nukkit.math.NukkitRandom;

public class PopulatorMushroomHell extends PopulatorCount {
    private final int type;

    public PopulatorMushroomHell(int type)    {
        this.type = type;
    }

    @Override
    public void populateCount(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        HeightRange heightRange = chunk.getHeightRange();
        int x = random.nextInt(16);
        int y = random.nextInt(128);
        int z = random.nextInt(16);
        int worldX = chunkX << 4 | x;
        int worldZ = chunkZ << 4 | z;
        for (int i = 0; i < 64; i++) {
            int randZ = random.nextGaussianInt(8);
            int randY = random.nextGaussianInt(4);
            int randX = random.nextGaussianInt(8);
            int posX = worldX + randX;
            int posY = y + randY;
            int posZ = worldZ + randZ;
            if (posY < heightRange.getMinY() || posY >= heightRange.getMaxY()) {
                continue;
            }
            if (level.getBlockIdAt(0, posX, posY, posZ) != Block.AIR) {
                continue;
            }
            if (Block.transparent[level.getBlockIdAt(0, posX, posY - 1, posZ)]) {
                continue;
            }
            level.setBlockAt(0, posX, posY, posZ, type);
        }
    }
}
