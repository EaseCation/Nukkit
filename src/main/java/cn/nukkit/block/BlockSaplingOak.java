package cn.nukkit.block;

import cn.nukkit.math.LocalRandom;
import cn.nukkit.level.generator.object.tree.ObjectTree;

public class BlockSaplingOak extends BlockSapling {
    BlockSaplingOak() {

    }

    @Override
    public int getId() {
        return OAK_SAPLING;
    }

    @Override
    public String getName() {
        return "Oak Sapling";
    }

    @Override
    protected void grow() {
        ObjectTree.growTree(level, getFloorX(), getFloorY(), getFloorZ(), new LocalRandom(), OAK);
    }
}
