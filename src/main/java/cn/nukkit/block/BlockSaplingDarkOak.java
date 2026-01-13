package cn.nukkit.block;

import cn.nukkit.level.generator.object.tree.ObjectDarkOakTree;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.LocalRandom;

public class BlockSaplingDarkOak extends BlockSapling {
    BlockSaplingDarkOak() {

    }

    @Override
    public int getId() {
        return DARK_OAK_SAPLING;
    }

    @Override
    public String getName() {
        return "Dark Oak Sapling";
    }

    @Override
    protected void grow() {
        int x;
        int z = 0;

        loop:
        for (x = 0; x >= -1; --x) {
            for (z = 0; z >= -1; --z) {
                if (findSaplings(x, z)) {
                    break loop;
                }
            }
        }

        if (x == 0 && z == 0 && !findSaplings(0, 0)) {
            return;
        }

        level.setBlock(add(x, 0, z), get(AIR), true, false);
        level.setBlock(add(x + 1, 0, z), get(AIR), true, false);
        level.setBlock(add(x, 0, z + 1), get(AIR), true, false);
        level.setBlock(add(x + 1, 0, z + 1), get(AIR), true, false);

        if (!new ObjectDarkOakTree().generate(level, new LocalRandom(), new BlockVector3(getFloorX() + x, getFloorY(), getFloorZ() + z))) {
            level.setBlock(add(x, 0, z), clone(), true, false);
            level.setBlock(add(x + 1, 0, z), clone(), true, false);
            level.setBlock(add(x, 0, z + 1), clone(), true, false);
            level.setBlock(add(x + 1, 0, z + 1), clone(), true, false);
        }
    }

    @Override
    public String getDescriptionId() {
        return "tile.sapling.big_oak.name";
    }
}
