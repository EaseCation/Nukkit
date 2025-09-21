package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.HeightRange;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.helper.EnsureBelow;
import cn.nukkit.level.generator.populator.helper.EnsureCover;
import cn.nukkit.level.generator.populator.type.PopulatorSurfaceBlock;
import cn.nukkit.math.NukkitRandom;

/**
 * @author DaPorkchop_
 */
public class PopulatorGroundFire extends PopulatorSurfaceBlock {
    @Override
    protected boolean canStay(int x, int y, int z, FullChunk chunk) {
        return EnsureCover.ensureCover(x, y, z, chunk) && EnsureBelow.ensureBelow(x, y, z, NETHERRACK, chunk);
    }

    @Override
    protected int getBlockId(int x, int z, NukkitRandom random, FullChunk chunk) {
        return FIRE;
    }

    @Override
    protected void placeBlock(int x, int y, int z, int id, int meta, FullChunk chunk, NukkitRandom random) {
        int below = chunk.getBlockId(0, x, y - 1, z);
        if (below == SOUL_SAND || below == SOUL_SOIL) {
            id = SOUL_FIRE;
        }

        super.placeBlock(x, y, z, id, meta, chunk, random);
        chunk.setBlockLight(x, y, z, Block.light[id]);
    }

    @Override
    protected int getHighestWorkableBlock(ChunkManager level, int x, int z, FullChunk chunk) {
        HeightRange heightRange = chunk.getHeightRange();
        for (int y = heightRange.getMinY() + 1 + 1; y < heightRange.getMaxY() - 1; ++y) {
            int b = chunk.getBlockId(0, x, y, z);
            if (b == Block.AIR) {
                return y;
            }
        }
        return Integer.MIN_VALUE;
    }
}
