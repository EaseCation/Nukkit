package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockSapling;
import cn.nukkit.level.ChunkManager;
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
        int y = this.getHighestWorkableBlock(level, x, z);
        if (y < 3) {
            return;
        }
        ObjectTree.growTree(level, x, y, z, random, this.type);
    }

    private int getHighestWorkableBlock(ChunkManager level, int x, int z) {
        int y;
        for (y = 254; y > 0; --y) {
            int b = level.getBlockIdAt(0, x, y, z);
            if (b == Block.DIRT || b == Block.GRASS_BLOCK) {
                break;
            } else if (b != Block.AIR && b != Block.SNOW_LAYER) {
                return -1;
            }
        }

        return ++y;
    }
}
