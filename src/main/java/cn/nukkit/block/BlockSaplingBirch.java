package cn.nukkit.block;

import cn.nukkit.math.LocalRandom;
import cn.nukkit.level.generator.object.tree.ObjectTree;

public class BlockSaplingBirch extends BlockSapling {
    BlockSaplingBirch() {

    }

    @Override
    public int getId() {
        return BIRCH_SAPLING;
    }

    @Override
    public String getName() {
        return "Birch Sapling";
    }

    @Override
    protected void grow() {
        ObjectTree.growTree(level, getFloorX(), getFloorY(), getFloorZ(), new LocalRandom(), BIRCH);
    }
}
