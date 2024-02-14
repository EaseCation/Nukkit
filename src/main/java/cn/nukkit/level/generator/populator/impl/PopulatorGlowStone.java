package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.NukkitRandom;

public class PopulatorGlowStone extends Populator {
    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        int x = random.nextRange(chunkX << 4, (chunkX << 4) + 15);
        int z = random.nextRange(chunkZ << 4, (chunkZ << 4) + 15);
        int y = this.getHighestWorkableBlock(chunk, x & 0xF, z & 0xF);
        if (y != Integer.MIN_VALUE && chunk.getBlockId(0, x & 0xf, y, z & 0xf) != NETHERRACK) {
            int count = random.nextRange(40, 60);
            for (int i = 0; i < count; i++) {
                level.setBlockAt(0, x + (random.nextBoundedInt(7) - 3), y + (random.nextBoundedInt(9) - 4), z + (random.nextBoundedInt(7) - 3), GLOWSTONE);
            }
        }
    }

    private int getHighestWorkableBlock(FullChunk chunk, int x, int z) {
        int y;
        //start scanning a bit lower down to allow space for placing on top
        for (y = 120; y >= 0; y--) {
            int b = chunk.getBlockId(0, x, y, z);
            if (b == Block.AIR) {
                break;
            }
        }
        return y == 0 ? Integer.MIN_VALUE : y;
    }
}
