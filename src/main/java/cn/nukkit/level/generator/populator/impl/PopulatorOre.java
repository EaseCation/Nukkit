package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.object.ore.OreType;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.NukkitRandom;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PopulatorOre extends Populator {
    private final OreType[] oreTypes;

    public PopulatorOre(OreType... oreTypes) {
        this.oreTypes = oreTypes;
    }

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        for (OreType type : this.oreTypes) {
            for (int i = 0; i < type.clusterCount; i++) {
                int x = random.nextInt(16);
                int z = random.nextInt(16);
                int y = random.nextRange(type.minHeight, type.maxHeight);
                if (chunk.getBlockId(0, x, y, z) != type.replaceBlockId) {
                    continue;
                }
                if (type.clusterSize == 1) {
                    chunk.setFullBlockId(0, x, y, z, type.fullId);
                } else {
                    type.spawn(chunk, random, x, y, z);
                }
            }
        }
    }
}
