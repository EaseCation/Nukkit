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
        return id == Block.AIR
                || id == Block.LEAVES
                || id == Block.GRASS_BLOCK
                || id == Block.DIRT
                || id == Block.OAK_LOG
                || id == Block.SPRUCE_LOG
                || id == Block.BIRCH_LOG
                || id == Block.JUNGLE_LOG
                || id == Block.ACACIA_LOG
                || id == Block.DARK_OAK_LOG
                || id == Block.SAPLING
                || id == Block.VINE;
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
