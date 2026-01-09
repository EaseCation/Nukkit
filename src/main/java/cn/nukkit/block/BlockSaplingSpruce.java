package cn.nukkit.block;

import cn.nukkit.math.LocalRandom;
import cn.nukkit.level.generator.object.tree.ObjectTree;

public class BlockSaplingSpruce extends BlockSapling {
    BlockSaplingSpruce() {

    }

    @Override
    public int getId() {
        return SPRUCE_SAPLING;
    }

    @Override
    public String getName() {
        return "Spruce Sapling";
    }

    @Override
    protected void grow() {
        ObjectTree.growTree(level, getFloorX(), getFloorY(), getFloorZ(), new LocalRandom(), SPRUCE);
    }
}
