package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.BlockDoublePlant;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.helper.EnsureCover;
import cn.nukkit.level.generator.populator.helper.EnsureGrassBelow;
import cn.nukkit.level.generator.populator.type.PopulatorSurfaceBlock;
import cn.nukkit.math.RandomSource;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

/**
 * @author Angelic47, Niall Lindsay (Niall7459)
 * <p>
 * Nukkit Project
 * </p>
 */
public class PopulatorFlower extends PopulatorSurfaceBlock {
    private final IntList flowerTypes = new IntArrayList();

    public void addType(int blockId) {
        addType(blockId, false);
    }

    public void addType(int blockId, boolean doublePlant) {
        this.flowerTypes.add(doublePlant ? -blockId : blockId);
    }

    @Override
    protected void placeBlock(int x, int y, int z, int id, int meta, FullChunk chunk, RandomSource random) {
        if (!flowerTypes.isEmpty()) {
            int type = flowerTypes.getInt(random.nextBoundedInt(flowerTypes.size()));
            if (type < 0) {
                type = -type;
                chunk.setBlock(0, x, y, z, type);
                chunk.setBlock(0, x, y + 1, z, type, BlockDoublePlant.UPPER_BLOCK_BIT);
            } else {
                chunk.setBlock(0, x, y, z, type);
            }
        }
    }

    @Override
    protected boolean canStay(int x, int y, int z, FullChunk chunk) {
        return EnsureCover.ensureCover(x, y, z, chunk) && EnsureGrassBelow.ensureGrassBelow(x, y, z, chunk);
    }

    @Override
    protected int getBlockId(int x, int z, RandomSource random, FullChunk chunk) {
        return AIR;
    }
}
