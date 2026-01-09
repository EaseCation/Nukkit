package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.TheEnd;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.Mth;
import cn.nukkit.math.RandomSource;

public class PopulatorEndIsland extends Populator {
    private final TheEnd end;

    public PopulatorEndIsland(TheEnd end) {
        this.end = end;
    }

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, RandomSource random, FullChunk chunk) {
        if (Mth.square(chunkX) + Mth.square(chunkZ) <= 4096) {
            return;
        }
        if (end.getIslandHeightValue(chunkX, chunkZ, 1, 1) >= -20) {
            return;
        }
        if (random.nextInt(14) != 0) {
            return;
        }

        int x = chunkX << 4;
        int z = chunkZ << 4;
        place(level, random, x, z);

        if (random.nextInt(4) == 0) {
            place(level, random, x, z);
        }
    }

    private static void place(ChunkManager level, RandomSource random, int x, int z) {
        x += random.nextInt(16);
        int y = random.nextInt(16) + 55;
        z += random.nextInt(16);

        float size = random.nextInt(3) + 4;
        for (int yd = 0; size > 0.5; --yd) {
            float ys = Mth.square(size + 1);
            for (int xd = Mth.floor(-size); xd <= Mth.ceil(size); ++xd) {
                int xs = Mth.square(xd);
                for (int zd = Mth.floor(-size); zd <= Mth.ceil(size); ++zd) {
                    if (ys >= xs + Mth.square(zd)) {
                        level.setBlockAt(0, x + xd, y + yd, z + zd, Block.END_STONE);
                    }
                }
            }
            size -= random.nextInt(2) + 0.5f;
        }
    }
}
