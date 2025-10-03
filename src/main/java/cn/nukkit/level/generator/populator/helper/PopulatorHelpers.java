package cn.nukkit.level.generator.populator.helper;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.FullChunk;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

/**
 * @author DaPorkchop_
 */
public final class PopulatorHelpers implements BlockID {
    private static final IntSet nonSolidBlocks = new IntOpenHashSet();

    static {
        nonSolidBlocks.add(AIR);
        nonSolidBlocks.add(LEAVES);
        nonSolidBlocks.add(LEAVES2);
        nonSolidBlocks.add(SHORT_GRASS);
        nonSolidBlocks.add(DEADBUSH);
        nonSolidBlocks.add(DANDELION);
        nonSolidBlocks.add(RED_FLOWER);
        nonSolidBlocks.add(BROWN_MUSHROOM);
        nonSolidBlocks.add(RED_MUSHROOM);
        nonSolidBlocks.add(SNOW_LAYER);
        nonSolidBlocks.add(CACTUS);
        nonSolidBlocks.add(BLOCK_REEDS);
        nonSolidBlocks.add(VINE);
        nonSolidBlocks.add(DOUBLE_PLANT);
    }

    private PopulatorHelpers() {
    }

    public static boolean canGrassStay(int x, int y, int z, FullChunk chunk) {
        return EnsureCover.ensureCover(x, y, z, chunk) && EnsureGrassBelow.ensureGrassBelow(x, y, z, chunk);
    }

    public static boolean isNonSolid(int id)   {
        return nonSolidBlocks.contains(id);
    }
}
