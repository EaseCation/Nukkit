package cn.nukkit.level.generator.populator.type;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.HeightRange;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.helper.PopulatorHelpers;
import cn.nukkit.math.RandomSource;

/**
 * @author DaPorkchop_
 *
 * A populator that populates a single block type.
 */
public abstract class PopulatorSurfaceBlock extends PopulatorCount {
    @Override
    protected void populateCount(ChunkManager level, int chunkX, int chunkZ, RandomSource random, FullChunk chunk) {
        int x = random.nextBoundedInt(16);
        int z = random.nextBoundedInt(16);
        int y = getHighestWorkableBlock(level, x, z, chunk);
        if (y != Integer.MIN_VALUE && canStay(x, y, z, chunk)) {
            placeBlock(x, y, z, getBlockId(x, z, random, chunk), getBlockMeta(x, z, random, chunk), chunk, random);
        }
    }

    protected abstract boolean canStay(int x, int y, int z, FullChunk chunk);

    protected abstract int getBlockId(int x, int z, RandomSource random, FullChunk chunk);

    protected int getBlockMeta(int x, int z, RandomSource random, FullChunk chunk) {
        return 0;
    }

    @Override
    protected int getHighestWorkableBlock(ChunkManager level, int x, int z, FullChunk chunk) {
        HeightRange heightRange = level.getHeightRange();
        int y;
        //start at max-2 because we add one afterwards
        for (y = heightRange.getMaxY() - 1 - 1; y >= heightRange.getMinY(); --y) {
            if (!PopulatorHelpers.isNonSolid(chunk.getBlockId(0, x, y, z))) {
                break;
            }
        }

        return y == heightRange.getMinY() ? Integer.MIN_VALUE : ++y;
    }

    protected void placeBlock(int x, int y, int z, int id, int meta, FullChunk chunk, RandomSource random) {
        chunk.setBlock(0, x, y, z, id, meta);
    }
}
