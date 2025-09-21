package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.NukkitRandom;

public class PopulatorEndPlatform extends Populator {
    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        if (chunkX != 6 || chunkZ < -1 ||  chunkZ > 0) {
            return;
        }

        if (chunkZ == 0) {
            for (int x = 2; x <= 6; x++) {
                for (int z = 0; z <= 2; z++) {
                    chunk.setBlock(0, x, 49, z, Block.OBSIDIAN);
                    for (int yd = 0; yd < 3; yd++) {
                        chunk.setBlock(0, x, 50 + yd, z, Block.AIR);
                    }
                }
            }
        } else {
            for (int x = 2; x <= 6; x++) {
                for (int z = 14; z <= 15; z++) {
                    chunk.setBlock(0, x, 49, z, Block.OBSIDIAN);
                    for (int yd = 0; yd < 3; yd++) {
                        chunk.setBlock(0, x, 50 + yd, z, Block.AIR);
                    }
                }
            }
        }
    }
}
