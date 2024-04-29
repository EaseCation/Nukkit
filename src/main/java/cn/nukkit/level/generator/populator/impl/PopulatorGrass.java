package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.BlockTallGrass;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.helper.PopulatorHelpers;
import cn.nukkit.level.generator.populator.type.PopulatorSurfaceBlock;
import cn.nukkit.math.NukkitRandom;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public class PopulatorGrass extends PopulatorSurfaceBlock {
    @Override
    protected boolean canStay(int x, int y, int z, FullChunk chunk) {
        return PopulatorHelpers.canGrassStay(x, y, z, chunk);
    }

    @Override
    protected int getBlockId(int x, int z, NukkitRandom random, FullChunk chunk) {
        return SHORT_GRASS;
    }

    @Override
    protected int getBlockMeta(int x, int z, NukkitRandom random, FullChunk chunk) {
        return BlockTallGrass.TYPE_GRASS;
    }
}
