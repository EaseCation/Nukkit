package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.HeightRange;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.type.PopulatorCount;
import cn.nukkit.math.NukkitRandom;

public class PopulatorGlowStone extends PopulatorCount {
    @Override
    public void populateCount(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        int x = random.nextInt(16);
        int y = random.nextInt(1, 126);
        int z = random.nextInt(16);
        if (chunk.getBlockId(0, x, y, z) != Block.AIR) {
            return;
        }
        if (chunk.getBlockId(0, x, y + 1, z) != Block.NETHERRACK) {
            return;
        }
        chunk.setBlock(0, x, y, z, Block.GLOWSTONE);
        chunk.setBlockLight(x, y, z, Block.light[Block.GLOWSTONE]);

        HeightRange heightRange = chunk.getHeightRange();
        int worldX = chunkX << 4 | x;
        int worldZ = chunkZ << 4 | z;
        for (int i = 0; i < 1500; i++) {
            int randZ = random.nextGaussianInt(8);
            int randY = -random.nextInt(12);
            int randX = random.nextGaussianInt(8);
            int posX = worldX + randX;
            int posY = y + randY;
            int posZ = worldZ + randZ;
            if (posY < heightRange.getMinY()) {
                continue;
            }
            if (level.getBlockIdAt(0, posX, posY, posZ) != Block.AIR) {
                continue;
            }
            int count = 0;
            if (level.getBlockIdAt(0, posX, posY + 1, posZ) == Block.GLOWSTONE) {
                ++count;
            }
            if (level.getBlockIdAt(0, posX, posY - 1, posZ) == Block.GLOWSTONE && ++count > 1) {
                continue;
            }
            if (level.getBlockIdAt(0, posX + 1, posY, posZ) == Block.GLOWSTONE && ++count > 1) {
                continue;
            }
            if (level.getBlockIdAt(0, posX - 1, posY, posZ) == Block.GLOWSTONE && ++count > 1) {
                continue;
            }
            if (level.getBlockIdAt(0, posX, posY, posZ + 1) == Block.GLOWSTONE && ++count > 1) {
                continue;
            }
            if (level.getBlockIdAt(0, posX, posY, posZ - 1) == Block.GLOWSTONE && ++count > 1) {
                continue;
            }
            if (count != 1) {
                continue;
            }
            level.setBlockAt(0, posX, posY, posZ, GLOWSTONE);
//            level.setBlockLight(posX, posY, posZ, Block.light[Block.GLOWSTONE]); //TODO
        }
    }
}
