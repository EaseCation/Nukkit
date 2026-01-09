package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.type.PopulatorCount;
import cn.nukkit.math.RandomSource;

public class PopulatorLavaTrap extends PopulatorCount {
    @Override
    public void populateCount(ChunkManager level, int chunkX, int chunkZ, RandomSource random, FullChunk chunk) {
        int x = random.nextInt(1, 15);
        int y = random.nextInt(10, 118);
        int z = random.nextInt(1, 15);

        int block = chunk.getBlockId(0, x, y, z);
        if (block != Block.AIR && block != Block.NETHERRACK) {
            return;
        }

        if (chunk.getBlockId(0, x, y + 1, z) != Block.NETHERRACK) {
            return;
        }
        if (chunk.getBlockId(0, x, y - 1, z) != Block.NETHERRACK) {
            return;
        }
        if (chunk.getBlockId(0, x + 1, y, z) != Block.NETHERRACK) {
            return;
        }
        if (chunk.getBlockId(0, x - 1, y, z) != Block.NETHERRACK) {
            return;
        }
        if (chunk.getBlockId(0, x, y, z + 1) != Block.NETHERRACK) {
            return;
        }
        if (chunk.getBlockId(0, x, y, z - 1) != Block.NETHERRACK) {
            return;
        }

        chunk.setBlock(0, x, y, z, Block.FLOWING_LAVA);
        chunk.setBlockLight(x, y, z, Block.light[Block.FLOWING_LAVA]);
    }
}
