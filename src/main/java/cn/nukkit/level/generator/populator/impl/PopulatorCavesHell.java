package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.Mth;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.RandomSource;

public class PopulatorCavesHell extends Populator {
    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, RandomSource random, FullChunk chunk) {
        long seed = level.getSeed();
        RandomSource genRandom = new NukkitRandom(seed);
        long xScale = 2 * (genRandom.nextInt() / 2) + 1;
        long zScale = 2 * (genRandom.nextInt() / 2) + 1;

        for (int x = chunkX - 8; x <= chunkX + 8; x++) {
            for (int z = chunkZ - 8; z <= chunkZ + 8; z++) {
                genRandom.setSeed(seed ^ zScale * z + xScale * x);
                addFeature(chunk, genRandom, x, z);
            }
        }
    }

    private static void addFeature(FullChunk chunk, RandomSource random, int chunkX, int chunkZ) {
        int caves = random.nextInt(random.nextInt(random.nextInt(10) + 1) + 1);
        if (random.nextInt(5) != 0) {
            return;
        }
        int worldX = chunkX << 4;
        int worldZ = chunkZ << 4;
        for (int cave = 0; cave < caves; ++cave) {
            int z = random.nextInt(16) | worldZ;
            int y = random.nextInt(128);
            int x = random.nextInt(16) | worldX;
            int tunnels = 1;
            if (random.nextInt(4) == 0) {
                addRoom(chunk, random, x, y, z);
                tunnels = random.nextInt(4) + 1;
            }
            for (int i = 0; i < tunnels; ++i) {
                float yRot = random.nextFloat() * Mth.PI * 2;
                float xRot = (random.nextFloat() - 0.5f) * 2 / 8;
                float thickness = 2 * random.nextFloat() + random.nextFloat();
                addTunnel(chunk, random, x, y, z, 2 * thickness, yRot, xRot, 0, 0, 0.5f);
            }
        }
    }

    private static void addRoom(FullChunk chunk, RandomSource random, float x, float y, float z) {
        addTunnel(chunk, random, x, y, z, random.nextFloat() * 6 + 1, 0, 0, -1, -1, 0.5f);
    }

    private static void addTunnel(FullChunk chunk, RandomSource randomIn, float x, float y, float z, float thickness, float yRot, float xRot, int step, int dist, float yScale) {
        int worldX = chunk.getX() << 4;
        int worldZ = chunk.getZ() << 4;
        int xMid = worldX | 8;
        int zMid = worldZ | 8;
        float yRota = 0;
        float xRota = 0;
        RandomSource random = new NukkitRandom(randomIn.nextInt());
        if (dist <= 0) {
            dist = 112 - random.nextInt(28);
        }
        boolean singleStep = false;
        if (step == -1) {
            step = dist / 2;
            singleStep = true;
        }
        int splitPoint = dist / 4 + random.nextInt(dist / 2);
        boolean steep = random.nextInt(6) == 0;
        for (; step < dist; step++) {
            float rad = Mth.sin(step * Mth.PI / dist) * thickness + 1.5f;
            float yRad = rad * yScale;
            float xc = Mth.cos(xRot);
            float xs = Mth.sin(xRot);
            x += Mth.cos(yRot) * xc;
            y += xs;
            z += Mth.sin(yRot) * xc;
            xRot = 0.1f * xRota + (steep ? 0.92f : 0.7f) * xRot;
            yRot += 0.1f * yRota;
            xRota = random.nextGaussianFloat() * random.nextFloat() * 2 + 0.9f * xRota;
            yRota = random.nextGaussianFloat() * random.nextFloat() * 4 + 0.75f * yRota;
            if (!singleStep && step == splitPoint && thickness > 1) {
                addTunnel(chunk, random, x, y, z, random.nextFloat() * 0.5f + 0.5f, yRot - Mth.PI / 2, xRot / 3, step, dist, 1);
                addTunnel(chunk, random, x, y, z, random.nextFloat() * 0.5f + 0.5f, yRot + Mth.PI / 2, xRot / 3, step, dist, 1);
                return;
            }
            if (!singleStep && random.nextInt(4) == 0) {
                continue;
            }
            if (Mth.square(x - xMid) + Mth.square(z - zMid) - Mth.square(dist - step) > Mth.square(thickness + 2 + 16)) {
                return;
            }
            int x0 = Mth.floor(x - rad) - worldX - 1;
            int x1 = Mth.floor(x + rad) - worldX + 1;
            int y0 = Mth.floor(y - rad) - 1;
            int y1 = Mth.floor(y + rad) + 1;
            int z0 = Mth.floor(z - rad) - worldZ - 1;
            int z1 = Mth.floor(z + rad) - worldZ + 1;
            if (x0 < 0) {
                x0 = 0;
            }
            if (x1 > 16) {
                x1 = 16;
            }
            if (y0 < 1) {
                y0 = 1;
            }
            if (y1 > 120) {
                y1 = 120;
            }
            if (z0 < 0) {
                z0 = 0;
            }
            if (z1 > 16) {
                z1 = 16;
            }
            boolean detectedLava = false;
            for (int xx = x0; !detectedLava && xx < x1; ++xx) {
                boolean xf = xx != x0 && xx != x1 - 1;
                for (int zz = z0; !detectedLava && zz < z1; ++zz) {
                    boolean zf = zz != z0 && zz != z1 - 1;
                    for (int yy = y1 + 1; !detectedLava && yy >= y0 - 1; --yy) {
                        if (yy < 128) {
                            int block = chunk.getBlockId(0, xx, yy, zz);
                            if (block == Block.LAVA || block == Block.FLOWING_LAVA) {
                                detectedLava = true;
                            }
                            if (yy != y0 - 1 && xf && zf) {
                                yy = y0;
                            }
                        }
                    }
                }
            }
            if (detectedLava) {
                continue;
            }
            for (int xx = x0; xx < x1; ++xx) {
                float xds = Mth.square(((worldX | xx) + 0.5f - x) / rad);
                for (int zz = z0; zz < z1; ++zz) {
                    float zds = Mth.square(((worldZ | zz) + 0.5f - z) / rad);
                    for (int yy = y1 - 1; yy >= y0; --yy) {
                        float yd = (yy + 0.5f - y) / yRad;
                        if (yd > -0.7f && xds + Mth.square(yd) + zds < 1) {
                            if (chunk.getBlockId(0, xx, yy, zz) == Block.NETHERRACK) {
                                chunk.setBlock(0, xx, yy, zz, Block.AIR);
                            }
                        }
                    }
                }
            }
            if (singleStep) {
                return;
            }
        }
    }
}
