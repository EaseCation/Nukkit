package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.NukkitRandom;

/**
 * @author Niall Lindsay (Niall7459)
 * <p>
 * Nukkit Project
 * </p>
 */
public class PopulatorTallSugarcane extends PopulatorSugarcane {
    @Override
    protected void placeBlock(int x, int y, int z, int id, int meta, FullChunk chunk, NukkitRandom random) {
        int height = random.nextBoundedInt(3) + 1;
        if (y + height >= chunk.getHeightRange().getMaxY()) return;
        for (int i = 0; i < height; i++)    {
            chunk.setBlock(0, x, y + i, z, id, meta);
        }
    }
}
