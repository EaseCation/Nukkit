package cn.nukkit.level.generator.populator.helper;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.FullChunk;

/**
 * @author DaPorkchop_
 */
public final class PopulatorHelpers implements BlockID {
    private PopulatorHelpers() {
    }

    public static boolean canGrassStay(int x, int y, int z, FullChunk chunk) {
        return EnsureCover.ensureCover(x, y, z, chunk) && EnsureGrassBelow.ensureGrassBelow(x, y, z, chunk);
    }

    public static boolean isNonSolid(int id)   {
        Block ub = Block.getUnsafe(id);
        return !ub.isSolid();
    }
}
