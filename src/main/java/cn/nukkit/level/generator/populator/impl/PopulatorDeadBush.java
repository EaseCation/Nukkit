package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.helper.EnsureBelow;
import cn.nukkit.level.generator.populator.helper.EnsureCover;
import cn.nukkit.level.generator.populator.type.PopulatorSurfaceBlock;
import cn.nukkit.math.RandomSource;

/**
 * @author DaPorkchop_
 */
public class PopulatorDeadBush extends PopulatorSurfaceBlock {

    @Override
    protected boolean canStay(int x, int y, int z, FullChunk chunk) {
        return EnsureCover.ensureCover(x, y, z, chunk) && EnsureBelow.ensureBelow(x, y, z, id -> id == SAND || id == RED_SAND, chunk);
    }

    @Override
    protected int getBlockId(int x, int z, RandomSource random, FullChunk chunk) {
        return DEADBUSH;
    }
}
