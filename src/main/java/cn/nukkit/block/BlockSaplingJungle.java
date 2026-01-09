package cn.nukkit.block;

import cn.nukkit.level.generator.object.BasicGenerator;
import cn.nukkit.level.generator.object.tree.NewJungleTree;
import cn.nukkit.level.generator.object.tree.ObjectJungleBigTree;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.LocalRandom;

public class BlockSaplingJungle extends BlockSapling {
    BlockSaplingJungle() {

    }

    @Override
    public int getId() {
        return JUNGLE_SAPLING;
    }

    @Override
    public String getName() {
        return "Jungle Sapling";
    }

    @Override
    protected void grow() {
        BasicGenerator generator = null;
        boolean bigTree = false;

        int x;
        int z = 0;

        loop:
        for (x = 0; x >= -1; --x) {
            for (z = 0; z >= -1; --z) {
                if (findSaplings(x, z)) {
                    generator = new ObjectJungleBigTree(10, 20, get(JUNGLE_LOG), get(JUNGLE_LEAVES));
                    bigTree = true;
                    break loop;
                }
            }
        }

        if (generator == null) {
            x = 0;
            z = 0;
            generator = new NewJungleTree(4, 7);
        }

        if (bigTree) {
            level.setBlock(add(x, 0, z), get(AIR), true, false);
            level.setBlock(add(x + 1, 0, z), get(AIR), true, false);
            level.setBlock(add(x, 0, z + 1), get(AIR), true, false);
            level.setBlock(add(x + 1, 0, z + 1), get(AIR), true, false);
        } else {
            level.setBlock(this, get(AIR), true, false);
        }

        if (!generator.generate(level, new LocalRandom(), new BlockVector3(getFloorX() + x, getFloorY(), getFloorZ() + z))) {
            if (bigTree) {
                level.setBlock(add(x, 0, z), clone(), true, false);
                level.setBlock(add(x + 1, 0, z), clone(), true, false);
                level.setBlock(add(x, 0, z + 1), clone(), true, false);
                level.setBlock(add(x + 1, 0, z + 1), clone(), true, false);
            } else {
                level.setBlock(this, this, true, false);
            }
        }
    }
}
