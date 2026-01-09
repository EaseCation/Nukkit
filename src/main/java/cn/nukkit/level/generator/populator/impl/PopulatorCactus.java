package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.helper.EnsureBelow;
import cn.nukkit.level.generator.populator.helper.EnsureCover;
import cn.nukkit.level.generator.populator.type.PopulatorSurfaceBlock;
import cn.nukkit.math.RandomSource;

/**
 * @author DaPorkchop_
 */
public class PopulatorCactus extends PopulatorSurfaceBlock {

    @Override
    protected void populateCount(ChunkManager level, int chunkX, int chunkZ, RandomSource random, FullChunk chunk) {
        int x = 1 + random.nextBoundedInt(14);
        int z = 1 + random.nextBoundedInt(14);
        int y = getHighestWorkableBlock(level, x, z, chunk);
        if (y != Integer.MIN_VALUE && canStay(x, y, z, chunk)) {
            placeBlock(x, y, z, getBlockId(x, z, random, chunk), getBlockMeta(x, z, random, chunk), chunk, random);
        }
    }

    @Override
    protected boolean canStay(int x, int y, int z, FullChunk chunk) {
        return EnsureCover.ensureCover(x, y, z, chunk) && EnsureBelow.ensureBelow(x, y, z, id -> id == SAND || id == RED_SAND, chunk)
                && chunk.getBlockId(0, x + 1, y, z) == AIR
                && chunk.getBlockId(0, x - 1, y, z) == AIR
                && chunk.getBlockId(0, x, y, z + 1) == AIR
                && chunk.getBlockId(0, x, y, z - 1) == AIR;
    }

    @Override
    protected int getBlockId(int x, int z, RandomSource random, FullChunk chunk) {
        return CACTUS;
    }

    @Override
    protected int getBlockMeta(int x, int z, RandomSource random, FullChunk chunk) {
        return 1;
    }
}
