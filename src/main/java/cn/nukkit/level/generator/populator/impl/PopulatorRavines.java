package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.biome.EnumBiome;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.Mth;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.RandomSource;

/**
 * Canyon feature
 */
public class PopulatorRavines extends Populator {

    private static final int checkAreaSize = 8;

    private static final int ravineRarity = 1;//2
    private static final int ravineMinAltitude = 20;
    private static final int ravineMaxAltitude = 67;
    private static final int ravineMinLength = 85;
    private static final int ravineMaxLength = 111;

    private static final double ravineDepth = 3;

    private static final int worldHeightCap = 1 << 7;

    private final boolean allowMegaRavines;

    public PopulatorRavines() {
        this(true);
    }

    protected PopulatorRavines(boolean allowMegaRavines) {
        this.allowMegaRavines = allowMegaRavines;
    }

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        RandomSource localRandom = new NukkitRandom(level.getSeed());
        long worldLong1 = localRandom.nextLong();
        long worldLong2 = localRandom.nextLong();

        int i = checkAreaSize;

        for (int x = chunkX - i; x <= chunkX + i; x++)
            for (int z = chunkZ - i; z <= chunkZ + i; z++) {
                long l3 = x * worldLong1;
                long l4 = z * worldLong2;
                localRandom.setSeed(l3 ^ l4 ^ level.getSeed());
                generateChunk(x, z, chunk, localRandom);
            }
    }

    protected void generateChunk(int chunkX, int chunkZ, FullChunk chunk, RandomSource random) {
        if (random.nextInt(150) != 0) {
            return;
        }

        double x = (chunkX << 4) + random.nextInt(16);
        double y = random.nextIntInclusive(ravineMinAltitude, ravineMaxAltitude);
        double z = (chunkZ << 4) + random.nextInt(16);

        if (random.nextFloat() <= 0.2f) {
            y += 15;
        }

        for (int i = 0; i < 1; i++) {
            float yRot = random.nextFloat() * Mth.PI * 2.0F;
            float xRot = (random.nextFloat() - 0.5F) * 2.0F / 8.0F;
            float thickness = (random.nextFloat() * 3.0F + random.nextFloat()) * 3.0F;
            float height;

            if (allowMegaRavines && random.nextFloat() < 0.05f) {
                thickness *= 2;
                height = 40;
            } else {
                height = 4;
            }

            addTunnel(random.nextLong(), chunk, x, y, z, thickness, yRot, xRot, 0, 0, height);
        }
    }

    protected void addTunnel(long seed, FullChunk chunk, double startX, double startY, double startZ,
                             float thickness, float yRot, float xRot, int step, int dist, double yScale) {
        float[] rs = new float[128];
        RandomSource random = new NukkitRandom(seed);

        int x = chunk.getX() << 4;
        int z = chunk.getZ() << 4;

        double xMid = x + 8;
        double zMid = z + 8;

        float yRota = 0.0F;
        float xRota = 0.0F;

        if (dist <= 0) {
            dist = random.nextIntInclusive(ravineMinLength, ravineMaxLength);
        }

        boolean singleStep = false;
        if (step == -1) {
            step = dist / 2;
            singleStep = true;
        }

        float f = 1.0F;
        for (int i = 0; i < 128; i++) {
            if (i == 0 || random.nextInt(3) == 0) {
                f = 1.0F + random.nextFloat() * random.nextFloat();
            }
            rs[i] = f * f;
        }

        for (; step < dist; step++) {
            double rad = 1.5D + Mth.sin(step * Mth.PI / dist) * thickness;
            double yRad = rad * yScale;

            rad *= random.nextFloat() * 0.25D + 0.75D;
            yRad *= random.nextFloat() * 0.25D + 0.75D;

            float xc = Mth.cos(xRot);
            float xs = Mth.sin(xRot);
            startX += Mth.cos(yRot) * xc;
            startY += xs;
            startZ += Mth.sin(yRot) * xc;

            xRot *= 0.7F;

            xRot += xRota * 0.05F;
            yRot += yRota * 0.05F;

            xRota *= 0.8F;
            yRota *= 0.5F;
            xRota += random.nextGaussianFloat() * random.nextFloat() * 2.0F;
            yRota += random.nextGaussianFloat() * random.nextFloat() * 4.0F;

            if (!singleStep && random.nextInt(4) == 0) {
                continue;
            }

            double d5 = startX - xMid;
            double d6 = startZ - zMid;
            double d7 = dist - step;
            double d8 = thickness + 2.0F + 16.0F;
            if (d5 * d5 + d6 * d6 - d7 * d7 > d8 * d8) {
                return;
            }

            if (startX < xMid - 16.0D - rad * 2.0D || startZ < zMid - 16.0D - rad * 2.0D || startX > xMid + 16.0D + rad * 2.0D || startZ > zMid + 16.0D + rad * 2.0D) {
                continue;
            }

            int x0 = Mth.floor(startX - rad) - x - 1;
            int x1 = Mth.floor(startX + rad) - z + 1;

            int y0 = Mth.floor(startY - yRad) - 1;
            int y1 = Mth.floor(startY + yRad) + 1;

            int z0 = Mth.floor(startZ - rad) - x - 1;
            int z1 = Mth.floor(startZ + rad) - z + 1;

            if (x0 < 0) {
                x0 = 0;
            }
            if (x1 > 16) {
                x1 = 16;
            }

            y1 = Mth.clamp(y1, 1, 128 - 8);
            y0 = Mth.clamp(y0, 1, y1);

            if (z0 < 0) {
                z0 = 0;
            }
            if (z1 > 16) {
                z1 = 16;
            }

            if (!carve(chunk, startX, startY, startZ, x0, x1, y0, y1, z0, z1, rad, yRad, rs) || singleStep) {
                break;
            }
        }
    }

    protected boolean carve(FullChunk chunk, double startX, double startY, double startZ, int x0, int x1, int y0, int y1, int z0, int z1, double rad, double yRad, float[] rs) {
        if (detectWater(chunk, x0, x1, y0, y1, z0, z1)) {
            return true;
        }

        int x = chunk.getX() << 4;
        int z = chunk.getZ() << 4;

        boolean carved = false;
        for (int xx = x0; xx < x1; xx++) {
            double xd = (xx + x + 0.5 - startX) / rad;
            double xdSq = xd * xd;
            for (int zz = z0; zz < z1; zz++) {
                double zd = (zz + z + 0.5 - startZ) / rad;
                double zdSq = zd * zd;
                double xz = xdSq + zdSq;

                if (xz >= 1) {
                    continue;
                }

                boolean hasGrass = false;
                for (int yy = y1; yy >= y0; yy--) {
                    double yd = (yy - 1 + 0.5 - startY) / yRad;

                    if (xz * rs[yy - 1] + yd * yd / 6 >= 1) {
                        continue;
                    }

                    int block = chunk.getBlockId(0, xx, yy, zz);
                    int above = chunk.getBlockId(0, xx, yy + 1, zz);

                    if (block == GRASS_BLOCK) {
                        hasGrass = true;
                    }

                    if (!PopulatorCaves.isDiggable(block, above)) {
                        continue;
                    }

                    if (!carved && chunk.getBiomeId(Mth.floor(startX) & 0xf, Mth.floor(startZ) & 0xf) == EnumBiome.OCEAN.id) {
                        return false;
                    }
                    carved = true;

                    if (yy - 1 >= 10) {
                        chunk.setBlock(0, xx, yy, zz, AIR);

                        if (hasGrass && chunk.getBlockId(0, xx, yy - 1, zz) == DIRT) {
                            chunk.setBlock(0, xx, yy - 1, zz, GRASS_BLOCK);
                        }
                    } else {
                        chunk.setBlock(0, xx, yy, zz, LAVA);
                    }
                }
            }
        }
        return true;
    }

    protected boolean detectWater(FullChunk chunk, int x0, int x1, int y0, int y1, int z0, int z1) {
        for (int xx = x0; xx < x1; xx++) {
            for (int zz = z0; zz < z1; zz++) {
                for (int yy = y1 + 1; yy >= y0 - 1; yy--) {
                    int block = chunk.getBlockId(0, xx, yy, zz);
                    if (block == Block.FLOWING_WATER || block == Block.WATER) {
                        return true;
                    }

                    if (yy != y0 - 1 && xx != x0 && xx != x1 - 1 && zz != z0 && zz != z1 - 1) {
                        yy = y0;
                    }
                }
            }
        }
        return false;
    }
}
