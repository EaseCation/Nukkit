package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockSapling;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.HeightRange;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.object.tree.ObjectTree;
import cn.nukkit.level.generator.populator.type.PopulatorCount;
import cn.nukkit.math.NukkitRandom;

/**
 * author: DaPorkchop_
 * Nukkit Project
 */
public class PopulatorTree extends PopulatorCount {

    private final int type;

    public PopulatorTree() {
        this(BlockSapling.OAK);
    }

    public PopulatorTree(int type) {
        this.type = type;
    }

    @Override
    public void populateCount(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        int x = random.nextRange(chunkX << 4, (chunkX << 4) + 15);
        int z = random.nextRange(chunkZ << 4, (chunkZ << 4) + 15);
        int y = getHighestWorkableBlock(level, x, z);
        if (y == Integer.MIN_VALUE || y < 3 + level.getHeightRange().getMinY()) {
            return;
        }
        ObjectTree.growTree(level, x, y, z, random, this.type);
    }

    public static int getHighestWorkableBlock(ChunkManager level, int x, int z) {
        HeightRange heightRange = level.getHeightRange();
        for (int y = heightRange.getMaxY() - 1 - 1; y > heightRange.getMinY(); --y) {
            int b = level.getBlockIdAt(0, x, y, z);
            if (b == Block.DIRT || b == Block.GRASS) {
                return y + 1;
            } else if (b != Block.AIR && b != Block.SNOW_LAYER) {
                return Integer.MIN_VALUE;
            }
        }
        return Integer.MIN_VALUE;
    }
}
