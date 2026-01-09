package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.RandomSource;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ObjectOakTree extends ObjectTree {
    private int treeHeight = 7;
    protected float beehiveProbability;

    public ObjectOakTree() {
        this(0);
    }

    public ObjectOakTree(float beehiveProbability) {
        this.beehiveProbability = beehiveProbability;
    }

    @Override
    public int getTrunkBlock() {
        return Block.OAK_LOG;
    }

    @Override
    public int getLeafBlock() {
        return Block.OAK_LEAVES;
    }

    @Override
    public int getTreeHeight() {
        return this.treeHeight;
    }

    @Override
    public void placeObject(ChunkManager level, int x, int y, int z, RandomSource random) {
        this.treeHeight = random.nextBoundedInt(3) + 4;
        super.placeObject(level, x, y, z, random);
    }

    @Override
    protected boolean canPlaceBeehive(RandomSource random) {
        return beehiveProbability != 0 && random.nextFloat() < beehiveProbability;
    }
}
