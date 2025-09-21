package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockBedrock;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.HeightRange;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.TheEnd;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.Mth;
import cn.nukkit.math.NukkitRandom;

public class PopulatorEndGateway extends Populator {
    private final TheEnd end;

    public PopulatorEndGateway(TheEnd end) {
        this.end = end;
    }

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        if (Mth.square(chunkX) + Mth.square(chunkZ) <= 4096) {
            return;
        }
        if (end.getIslandHeightValue(chunkX, chunkZ, 1, 1) <= 40) {
            return;
        }
        if (random.nextInt(700) != 0) {
            return;
        }

        int xc = random.nextInt(1, 15);
        int zc = random.nextInt(1, 15);
        int top = getHighestWorkableBlock(level, xc, zc, chunk);
        if (top <= 0) {
            return;
        }
        int yc = random.nextBoundedInt(7) + top + 3;
        HeightRange heightRange = chunk.getHeightRange();
        if (yc <= 1 || yc >= heightRange.getMaxY() - 2) {
            return;
        }
        for (int yd = -2; yd <= 2; yd++) {
            for (int xd = -1; xd <= 1; xd++) {
                for (int zd = -1; zd <= 1; zd++) {
                    int x = xc + xd;
                    int y = yc + yd;
                    int z = zc + zd;
                    boolean xMid = x == xc;
                    boolean yMid = y == yc;
                    boolean zMid = z == zc;
                    boolean edge = Math.abs(yd) == 2;
                    if (xMid && yMid && zMid) {
                        chunk.setBlock(0, x, y, z, Block.END_GATEWAY);
                        int worldX = chunkX << 4 | x;
                        int worldZ = chunkZ << 4 | z;
                        //TODO: spawn gateway block entity
                    } else if (yMid) {
                        chunk.setBlock(0, x, y, z, Block.AIR);
                    } else if (edge && xMid && zMid) {
                        chunk.setBlock(0, x, y, z, Block.BEDROCK, BlockBedrock.INFINIBURN_BIT);
                    } else if ((xMid || zMid) && !edge) {
                        chunk.setBlock(0, x, y, z, Block.BEDROCK, BlockBedrock.INFINIBURN_BIT);
                    } else {
                        chunk.setBlock(0, x, y, z, Block.AIR);
                    }
                }
            }
        }
    }
}
