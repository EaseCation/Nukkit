package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.BlockDoublePlant;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.helper.EnsureCover;
import cn.nukkit.level.generator.populator.helper.EnsureGrassBelow;
import cn.nukkit.level.generator.populator.type.PopulatorSurfaceBlock;
import cn.nukkit.math.RandomSource;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public class PopulatorDoublePlant extends PopulatorSurfaceBlock {
    private final int type;

    public PopulatorDoublePlant(int type)    {
        this.type = type;
    }

    @Override
    protected boolean canStay(int x, int y, int z, FullChunk chunk) {
        return EnsureCover.ensureCover(x, y, z, chunk) && EnsureCover.ensureCover(x, y + 1, z, chunk) && EnsureGrassBelow.ensureGrassBelow(x, y, z, chunk);
    }

    @Override
    protected int getBlockId(int x, int z, RandomSource random, FullChunk chunk) {
        return type;
    }

    @Override
    protected void placeBlock(int x, int y, int z, int id, int meta, FullChunk chunk, RandomSource random) {
        super.placeBlock(x, y, z, id, 0, chunk, random);
        chunk.setBlock(0, x, y + 1, z, id, BlockDoublePlant.UPPER_BLOCK_BIT);
    }
}
