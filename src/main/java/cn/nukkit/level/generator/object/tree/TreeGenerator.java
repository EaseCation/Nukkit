package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Level;
import cn.nukkit.level.generator.object.BasicGenerator;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.RandomSource;

public abstract class TreeGenerator extends BasicGenerator {

    /**
     * returns whether or not a tree can grow into a block.
     * For example, a tree will not grow into stone.
     */
    protected boolean canGrowInto(int id) {
        switch (id) {
            case Block.AIR:
            case Block.GRASS_BLOCK:
            case Block.DIRT:
            case Block.VINE:
                return true;
        }
        Block ub = Block.getUnsafe(id);
        return ub.isLog() || ub.isLeaves() || ub.isSapling();
    }

    public void generateSaplings(Level level, RandomSource random, BlockVector3 pos) {
    }

    /**
     * sets dirt at a specific location if it isn't already dirt.
     */
    protected void setDirtAt(ChunkManager level, BlockVector3 pos) {
        if (level.getBlockIdAt(0, pos.x, pos.y, pos.z) != Block.DIRT) {
            this.setBlockAndNotifyAdequately(level, pos, Block.get(BlockID.DIRT));
        }
    }
}
