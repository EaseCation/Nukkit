package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.BlockDoublePlant;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.helper.EnsureCover;
import cn.nukkit.level.generator.populator.helper.EnsureGrassBelow;
import cn.nukkit.level.generator.populator.type.PopulatorSurfaceBlock;
import cn.nukkit.math.NukkitRandom;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Angelic47, Niall Lindsay (Niall7459)
 * <p>
 * Nukkit Project
 * </p>
 */
public class PopulatorFlower extends PopulatorSurfaceBlock {
    private final List<int[]> flowerTypes = new ArrayList<>();

    public void addType(int a, int b) {
        int[] c = new int[2];
        c[0] = a;
        c[1] = b;
        this.flowerTypes.add(c);
    }

    public List<int[]> getTypes() {
        return this.flowerTypes;
    }

    @Override
    protected void placeBlock(int x, int y, int z, int id, int meta, FullChunk chunk, NukkitRandom random) {
        if (!flowerTypes.isEmpty()) {
            int[] type = flowerTypes.get(random.nextBoundedInt(flowerTypes.size()));
            chunk.setBlock(0, x, y, z, type[0], type[1]);
            if (type[0] == DOUBLE_PLANT) {
                chunk.setBlock(0, x, y + 1, z, type[0], type[1] | BlockDoublePlant.TOP_HALF_BITMASK);
            }
        }
    }

    @Override
    protected boolean canStay(int x, int y, int z, FullChunk chunk) {
        return EnsureCover.ensureCover(x, y, z, chunk) && EnsureGrassBelow.ensureGrassBelow(x, y, z, chunk);
    }

    @Override
    protected int getBlockId(int x, int z, NukkitRandom random, FullChunk chunk) {
        return 0;
    }
}
