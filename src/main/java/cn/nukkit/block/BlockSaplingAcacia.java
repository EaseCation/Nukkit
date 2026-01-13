package cn.nukkit.block;

import cn.nukkit.math.LocalRandom;
import cn.nukkit.level.generator.object.tree.ObjectSavannaTree;

public class BlockSaplingAcacia extends BlockSapling {
    BlockSaplingAcacia() {

    }

    @Override
    public int getId() {
        return ACACIA_SAPLING;
    }

    @Override
    public String getName() {
        return "Acacia Sapling";
    }

    @Override
    protected void grow() {
        level.setBlock(this, get(AIR), true, false);

        if (!new ObjectSavannaTree().generate(level, new LocalRandom(), asBlockVector3())) {
            level.setBlock(this, this, true, false);
        }
    }

    @Override
    public String getDescriptionId() {
        return "tile.sapling.acacia.name";
    }
}
