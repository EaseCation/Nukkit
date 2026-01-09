package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockChorusFlower;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.TheEnd;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.Mth;
import cn.nukkit.math.RandomSource;

public class PopulatorChorusFlower extends Populator {
    private final TheEnd end;

    public PopulatorChorusFlower(TheEnd end) {
        this.end = end;
    }

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, RandomSource random, FullChunk chunk) {
        if (Mth.square(chunkX) + Mth.square(chunkZ) <= 4096) {
            return;
        }
        if (end.getIslandHeightValue(chunkX, chunkZ, 1, 1) <= 40) {
            return;
        }

        int chorusPlantCount = random.nextInt(5);
        for (int i = 0; i < chorusPlantCount; ++i) {
            int x = chunkX << 4 | random.nextInt(16);
            int z = chunkZ << 4 | random.nextInt(16);
            int y = getHighestWorkableBlock(level, x, z, chunk);
            if (y > 0 && level.getBlockIdAt(0, x, y, z) == Block.AIR && level.getBlockIdAt(0, x, y - 1, z) == Block.END_STONE) {
                generatePlant(level, x, y, z, random, 8);
            }
        }
    }

    @Override
    protected int getHighestWorkableBlock(ChunkManager level, int x, int z, FullChunk chunk)    {
        return chunk.getHeightMap(x & 0xf, z & 0xf);
    }

    private static void generatePlant(ChunkManager level, int x, int y, int z, RandomSource random, int maxHorizontalSpread) {
        level.setBlockAt(0, x, y, z, Block.CHORUS_PLANT);
        growTreeRecursive(level, x, y, z, x, z, random, maxHorizontalSpread, 0);
    }

    private static void growTreeRecursive(ChunkManager level, int currentX, int currentY, int currentZ, int startX, int startZ, RandomSource random, int maxHorizontalSpread, int depth) {
        int height = random.nextInt(4) + 1;
        if (depth == 0) {
            ++height;
        }
        for (int i = 0; i < height; ++i) {
            int y = currentY + i + 1;
            if (!allNeighborsEmpty(level, currentX, y, currentZ, -1)) {
                return;
            }
            level.setBlockAt(0, currentX, y, currentZ, Block.CHORUS_PLANT);
        }

        boolean placedStem = false;
        if (depth < 4) {
            int stems = random.nextInt(4);
            if (depth == 0) {
                ++stems;
            }
            for (int i = 0; i < stems; ++i) {
                int x = currentX;
                int z = currentZ;
                int direction = random.nextInt(4);
                switch (direction) {
                    case 0 -> z += 1;
                    case 1 -> x -= 1;
                    case 2 -> z -= 1;
                    case 3 -> x += 1;
                }
                int y = currentY + height;
                if (Math.abs(x - startX) < maxHorizontalSpread && Math.abs(z - startZ) < maxHorizontalSpread
                        && level.getBlockIdAt(0, x, y, z) == Block.AIR && level.getBlockIdAt(0, x, y - 1, z) == Block.AIR
                        && allNeighborsEmpty(level, x, y, z, direction)) {
                    placedStem = true;
                    level.setBlockAt(0, x, y, z, Block.CHORUS_PLANT);
                    growTreeRecursive(level, x, y, z, startX, startZ, random, maxHorizontalSpread, depth + 1);
                }
            }
        }
        if (!placedStem) {
            level.setBlockAt(0, currentX, currentY + height, currentZ, Block.CHORUS_FLOWER, BlockChorusFlower.MAX_AGE);
        }
    }

    private static boolean allNeighborsEmpty(ChunkManager level, int x, int y, int z, int direction) {
        if (direction != 0 && level.getBlockIdAt(0, x, y, z - 1) != Block.AIR) {
            return false;
        }
        if (direction != 1 && level.getBlockIdAt(0, x + 1, y, z) != Block.AIR) {
            return false;
        }
        if (direction != 2 && level.getBlockIdAt(0, x, y, z + 1) != Block.AIR) {
            return false;
        }
        return direction == 3 || level.getBlockIdAt(0, x - 1, y, z) == Block.AIR;
    }
}
