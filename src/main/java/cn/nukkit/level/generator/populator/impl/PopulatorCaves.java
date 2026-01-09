package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.biome.Biomes;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.biome.type.CoveredBiome;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.Mth;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.RandomSource;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class PopulatorCaves extends Populator {

    private static final int checkAreaSize = 8;

    private static final int caveRarity = 7;
    private static final int caveFrequency = 40;
    private static final int caveMinAltitude = 8;
    private static final int caveMaxAltitude = 67;
    private static final int individualCaveRarity = 25;
    private static final int caveSystemFrequency = 1;
    private static final int caveSystemPocketChance = 0;
    private static final int caveSystemPocketMinSize = 0;
    private static final int caveSystemPocketMaxSize = 4;
    private static final boolean evenCaveDistribution = false;

    private static final int worldHeightCap = 128;

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, RandomSource random, FullChunk chunk) {
        RandomSource localRandom = new NukkitRandom(level.getSeed());
        long worldLong1 = localRandom.nextLong();
        long worldLong2 = localRandom.nextLong();

        int size = checkAreaSize;

        for (int x = chunkX - size; x <= chunkX + size; x++)
            for (int z = chunkZ - size; z <= chunkZ + size; z++) {
                long randomX = x * worldLong1;
                long randomZ = z * worldLong2;
                localRandom.setSeed(randomX ^ randomZ ^ level.getSeed());
                generateChunk(x, z, chunk, localRandom);
            }
    }

    protected void generateLargeCaveNode(long seed, RandomSource random, FullChunk chunk, double x, double y, double z) {
        generateCaveNode(seed, chunk, x, y, z, 1.0F + random.nextFloat() * 6.0F, 0.0F, 0.0F, -1, -1, 0.5D);
    }

    protected void generateCaveNode(long seed, FullChunk chunk, double x, double y, double z, float radius, float angelOffset, float angel, int angle, int maxAngle, double scale) {
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();

        double realX = chunkX * 16 + 8;
        double realZ = chunkZ * 16 + 8;

        float f1 = 0.0F;
        float f2 = 0.0F;

        RandomSource localRandom = new NukkitRandom(seed);

        if (maxAngle <= 0) {
            int checkAreaSize = PopulatorCaves.checkAreaSize * 16 - 16;
            maxAngle = checkAreaSize - localRandom.nextInt(checkAreaSize / 4);
        }
        boolean isLargeCave = false;

        if (angle == -1) {
            angle = maxAngle / 2;
            isLargeCave = true;
        }

        int randomAngel = localRandom.nextInt(maxAngle / 2) + maxAngle / 4;
        boolean bigAngel = localRandom.nextInt(6) == 0;

        for (; angle < maxAngle; angle++) {
            double offsetXZ = 1.5D + Mth.sin(angle * 3.141593F / maxAngle) * radius * 1.0F;
            double offsetY = offsetXZ * scale;

            float cos = Mth.cos(angel);
            float sin = Mth.sin(angel);
            x += Mth.cos(angelOffset) * cos;
            y += sin;
            z += Mth.sin(angelOffset) * cos;

            if (bigAngel)
                angel *= 0.92F;
            else {
                angel *= 0.7F;
            }
            angel += f2 * 0.1F;
            angelOffset += f1 * 0.1F;

            f2 *= 0.9F;
            f1 *= 0.75F;
            f2 += (localRandom.nextFloat() - localRandom.nextFloat()) * localRandom.nextFloat() * 2.0F;
            f1 += (localRandom.nextFloat() - localRandom.nextFloat()) * localRandom.nextFloat() * 4.0F;

            if ((!isLargeCave) && (angle == randomAngel) && (radius > 1.0F) && (maxAngle > 0)) {
                generateCaveNode(localRandom.nextLong(), chunk, x, y, z, localRandom.nextFloat() * 0.5F + 0.5F, angelOffset - 1.570796F, angel / 3.0F, angle, maxAngle, 1.0D);
                generateCaveNode(localRandom.nextLong(), chunk, x, y, z, localRandom.nextFloat() * 0.5F + 0.5F, angelOffset + 1.570796F, angel / 3.0F, angle, maxAngle, 1.0D);
                return;
            }
            if ((!isLargeCave) && (localRandom.nextInt(4) == 0)) {
                continue;
            }

            // Check if distance to working point (x and z) too larger than working radius (maybe ??)
            double distanceX = x - realX;
            double distanceZ = z - realZ;
            double angelDiff = maxAngle - angle;
            double newRadius = radius + 2.0F + 16.0F;
            if (distanceX * distanceX + distanceZ * distanceZ - angelDiff * angelDiff > newRadius * newRadius) {
                return;
            }

            //Boundaries check.
            if ((x < realX - 16.0D - offsetXZ * 2.0D) || (z < realZ - 16.0D - offsetXZ * 2.0D) || (x > realX + 16.0D + offsetXZ * 2.0D) || (z > realZ + 16.0D + offsetXZ * 2.0D))
                continue;


            int xFrom = Mth.floor(x - offsetXZ) - chunkX * 16 - 1;
            int xTo = Mth.floor(x + offsetXZ) - chunkX * 16 + 1;

            int yFrom = Mth.floor(y - offsetY) - 1;
            int yTo = Mth.floor(y + offsetY) + 1;

            int zFrom = Mth.floor(z - offsetXZ) - chunkZ * 16 - 1;
            int zTo = Mth.floor(z + offsetXZ) - chunkZ * 16 + 1;

            if (xFrom < 0)
                xFrom = 0;
            if (xTo > 16)
                xTo = 16;

            if (yFrom < 1)
                yFrom = 1;
            if (yTo > worldHeightCap - 8) {
                yTo = worldHeightCap - 8;
            }
            if (zFrom < 0)
                zFrom = 0;
            if (zTo > 16)
                zTo = 16;

            // Search for water
            boolean waterFound = false;
            for (int xx = xFrom; (!waterFound) && (xx < xTo); xx++) {
                for (int zz = zFrom; (!waterFound) && (zz < zTo); zz++) {
                    for (int yy = yTo + 1; (!waterFound) && (yy >= yFrom - 1); yy--) {
                        if (yy >= 0 && yy < worldHeightCap) {
                            int block = chunk.getBlockId(0, xx, yy, zz);
                            if (block == Block.FLOWING_WATER || block == Block.WATER) {
                                waterFound = true;
                            }
                            if ((yy != yFrom - 1) && (xx != xFrom) && (xx != xTo - 1) && (zz != zFrom) && (zz != zTo - 1))
                                yy = yFrom;
                        }
                    }
                }
            }

            if (waterFound) {
                continue;
            }

            // Generate cave
            for (int xx = xFrom; xx < xTo; xx++) {
                double modX = (xx + chunkX * 16 + 0.5D - x) / offsetXZ;
                for (int zz = zFrom; zz < zTo; zz++) {
                    double modZ = (zz + chunkZ * 16 + 0.5D - z) / offsetXZ;

                    boolean grassFound = false;
                    if (modX * modX + modZ * modZ < 1.0D) {
                        for (int yy = yTo; yy > yFrom; yy--) {
                            double modY = ((yy - 1) + 0.5D - y) / offsetY;
                            if ((modY > -0.7D) && (modX * modX + modY * modY + modZ * modZ < 1.0D)) {
                                Biome biome = Biomes.get(chunk.getBiomeId(xx, zz));
                                if (!(biome instanceof CoveredBiome)) {
                                    continue;
                                }

                                int material = chunk.getBlockId(0, xx, yy, zz);
//                                int materialAbove = chunk.getBlockId(0, xx, yy + 1, zz);
                                if (material == Block.GRASS_BLOCK || material == Block.MYCELIUM) {
                                    grassFound = true;
                                }
                                //TODO: check this
//								if (this.isSuitableBlock(material, materialAbove, biome))
                                {
                                    if (yy - 1 < 10) {
                                        chunk.setBlock(0, xx, yy, zz, Block.FLOWING_LAVA);
                                    } else {
                                        chunk.setBlock(0, xx, yy, zz, Block.AIR);

                                        // If grass was just deleted, try to
                                        // move it down
                                        if (grassFound && (chunk.getBlockId(0, xx, yy - 1, zz) == Block.DIRT)) {
                                            chunk.setBlock(0, xx, yy - 1, zz, ((CoveredBiome) biome).getSurfaceBlock(yy - 1));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (isLargeCave) {
                break;
            }
        }
    }

    protected void generateChunk(int chunkX, int chunkZ, FullChunk generatingChunkBuffer, RandomSource random) {
        int i = random.nextInt(random.nextInt(random.nextInt(caveFrequency) + 1) + 1);
        if (evenCaveDistribution)
            i = caveFrequency;
        if (random.nextInt(100) >= caveRarity)
            i = 0;

        for (int j = 0; j < i; j++) {
            double x = chunkX * 16 + random.nextInt(16);

            double y;

            if (evenCaveDistribution)
                y = random.nextIntInclusive(caveMinAltitude, caveMaxAltitude);
            else
                y = random.nextInt(random.nextInt(caveMaxAltitude - caveMinAltitude + 1) + 1) + caveMinAltitude;

            double z = chunkZ * 16 + random.nextInt(16);

            int count = caveSystemFrequency;
            boolean largeCaveSpawned = false;
            if (random.nextInt(100) <= individualCaveRarity) {
                generateLargeCaveNode(random.nextLong(), random, generatingChunkBuffer, x, y, z);
                largeCaveSpawned = true;
            }

            if ((largeCaveSpawned) || (random.nextInt(100) <= caveSystemPocketChance)) {
                count += random.nextIntInclusive(caveSystemPocketMinSize, caveSystemPocketMaxSize);
            }
            while (count > 0) {
                count--;
                float f1 = random.nextFloat() * 3.141593F * 2.0F;
                float f2 = (random.nextFloat() - 0.5F) * 2.0F / 8.0F;
                float f3 = random.nextFloat() * 2.0F + random.nextFloat();

                generateCaveNode(random.nextLong(), generatingChunkBuffer, x, y, z, f3, f1, f2, 0, 0, 1.0D);
            }
        }
    }

    public static boolean isDiggable(int block, int above) {
        switch (block) {
            case SAND:
            case RED_SAND:
            case GRAVEL:
                if (above == WATER) {
                    return false;
                }
            case DIRT:
            case COARSE_DIRT:
            case GRASS_BLOCK:
            case SANDSTONE:
            case RED_SANDSTONE:
            case MYCELIUM:
            case PODZOL:
            case SNOW_LAYER:
                return true;
        }
        Block ub = Block.getUnsafe(block);
        return ub.isStone() || ub.isTerracotta();
    }
}
