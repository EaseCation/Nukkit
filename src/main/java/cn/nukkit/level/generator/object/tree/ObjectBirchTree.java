package cn.nukkit.level.generator.object.tree;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockWood;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.RandomSource;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ObjectBirchTree extends ObjectTree {
    protected int treeHeight = 7;
    protected float beehiveProbability;

    public ObjectBirchTree() {
        this(0);
    }

    public ObjectBirchTree(float beehiveProbability) {
        this.beehiveProbability = beehiveProbability;
    }

    @Override
    public int getTrunkBlock() {
        return Block.BIRCH_LOG;
    }

    @Override
    public int getLeafBlock() {
        return Block.LEAVES;
    }

    @Override
    public int getType() {
        return BlockWood.BIRCH;
    }

    @Override
    public int getTreeHeight() {
        return this.treeHeight;
    }

    @Override
    public void placeObject(ChunkManager level, int x, int y, int z, NukkitRandom random) {
        this.treeHeight = random.nextBoundedInt(2) + 5;
        super.placeObject(level, x, y, z, random);
    }

    @Override
    protected boolean canPlaceBeehive(RandomSource random) {
        return beehiveProbability != 0 && random.nextFloat() < beehiveProbability;
    }
}
