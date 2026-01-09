package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.HeightRange;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.object.mushroom.BigMushroom;
import cn.nukkit.level.generator.populator.type.PopulatorCount;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.RandomSource;

/**
 * @author DaPorkchop_
 */
public class MushroomPopulator extends PopulatorCount {
    private final int type;

    public MushroomPopulator() {
        this(-1);
    }

    public MushroomPopulator(int type) {
        this.type = type;
    }

    @Override
    public void populateCount(ChunkManager level, int chunkX, int chunkZ, RandomSource random, FullChunk chunk) {
        int x = (chunkX << 4) | random.nextBoundedInt(16);
        int z = (chunkZ << 4) | random.nextBoundedInt(16);
        int y = this.getHighestWorkableBlock(level, x, z, chunk);
        if (y != Integer.MIN_VALUE) {
            new BigMushroom(type).generate(level, random, new BlockVector3(x, y, z));
        }
    }

    @Override
    protected int getHighestWorkableBlock(ChunkManager level, int x, int z, FullChunk chunk) {
        x &= 0xF;
        z &= 0xF;
        HeightRange heightRange = level.getHeightRange();
        for (int y = heightRange.getMaxY() - 1 - 1; y > heightRange.getMinY(); --y) {
            int b = chunk.getBlockId(0, x, y, z);
            if (b == Block.DIRT || b == Block.GRASS_BLOCK) {
                return y + 1;
            } else if (b != Block.AIR && b != Block.SNOW_LAYER) {
                return Integer.MIN_VALUE;
            }
        }
        return Integer.MIN_VALUE;
    }
}