package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockBedrock;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.HeightRange;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3f;

public class PopulatorEndPodium extends Populator {
    private final boolean activated;

    public PopulatorEndPodium() {
        this(false);
    }

    public PopulatorEndPodium(boolean activated) {
        this.activated = activated;
    }

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        if (chunkX < -1 || chunkX > 0 || chunkZ < -1 || chunkZ > 0) {
            return;
        }
        for (int cx = -1; cx < 1; cx++) {
            for (int cz = -1; cz < 1; cz++) {
                if (cx == chunkX && cz == chunkZ) {
                    continue;
                }
                if (!isChunkGenerated(level, cx, cz)) {
                    return;
                }
            }
        }

        int lx = 0;
        int lz = 0;
        if (chunkX == -1) {
            lx = 15;
        }
        if (chunkZ == -1) {
            lz = 15;
        }
        int top = getHighestWorkableBlock(level, lx, lz, chunk);
        if (level.getBlockIdAt(0, 0, top - 1, 0) != Block.END_STONE) {
            return;
        }
        level.setBlockAt(0, 0, top, 0, Block.BEDROCK, BlockBedrock.INFINIBURN_BIT);

        Vector3f origin = new Vector3f(0, top, 0);
        for (int x = -4; x <= 4; x++) {
            for (int z = -4; z <= 4; z++) {
                for (int yd = -1; yd <= 32; yd++) {
                    int y = top + yd;
                    float distSqr = origin.distanceSquared(x, y, z);
                    if (distSqr > 12.25f) {
                        continue;
                    }
                    if (y < top) {
                        if (distSqr <= 6.25f) {
                            level.setBlockAt(0, x, y, z, Block.BEDROCK, BlockBedrock.INFINIBURN_BIT);
                        } else {
                            level.setBlockAt(0, x, y, z, Block.END_STONE);
                        }
                    } else if (y > top) {
                        level.setBlockAt(0, x, y, z, Block.AIR);
                    } else if (distSqr > 6.25f) {
                        level.setBlockAt(0, x, y, z, Block.BEDROCK, BlockBedrock.INFINIBURN_BIT);
                    } else if (activated) {
                        level.setBlockAt(0, x, y, z, Block.END_PORTAL);
                    } else {
                        level.setBlockAt(0, x, y, z, Block.AIR);
                    }
                }
            }
        }
        for (int yd = 0; yd < 4; ++yd) {
            level.setBlockAt(0, 0, top + yd, 0, Block.BEDROCK, BlockBedrock.INFINIBURN_BIT);
        }
        int centerOfPillar = top + 2;
        level.setBlockAt(0, 1, centerOfPillar, 0, Block.TORCH, 1);
        level.setBlockAt(0, -1, centerOfPillar, 0, Block.TORCH, 2);
        level.setBlockAt(0, 0, centerOfPillar, 1, Block.TORCH, 3);
        level.setBlockAt(0, 0, centerOfPillar, -1, Block.TORCH, 4);
    }

    @Override
    protected int getHighestWorkableBlock(ChunkManager level, int x, int z, FullChunk chunk) {
        HeightRange heightRange = level.getHeightRange();
        for (int y = heightRange.getMaxY() - 2; y >= heightRange.getMinY(); y--) {
            if (level.getBlockIdAt(0, 0, y, 0) != Block.AIR) {
                return y + 1;
            }
        }
        return heightRange.getMinY() + 1;
    }

    private static boolean isChunkGenerated(ChunkManager level, int chunkX, int chunkZ) {
        HeightRange heightRange = level.getHeightRange();
        int x = chunkX << 4;
        int z = chunkZ << 4;
        for (int y = heightRange.getMinY(); y < heightRange.getMaxY(); y++) {
            for (int xd = 0; xd < 16; xd++) {
                int wx = x | xd;
                for (int zd = 0; zd < 16; zd++) {
                    if (level.getBlockIdAt(0, wx, y, z | zd) != Block.AIR) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
