package cn.nukkit.level.generator.populator.impl.tree;

import cn.nukkit.block.BlockSapling;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.object.tree.ObjectSwampTree;
import cn.nukkit.level.generator.populator.impl.PopulatorTree;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.RandomSource;

public class SwampTreePopulator extends Populator {
    private int randomAmount;
    private int baseAmount;

    private final int type;

    public SwampTreePopulator() {
        this(BlockSapling.OAK);
    }

    public SwampTreePopulator(int type) {
        this.type = type;
    }

    public void setRandomAmount(int randomAmount) {
        this.randomAmount = randomAmount;
    }

    public void setBaseAmount(int baseAmount) {
        this.baseAmount = baseAmount;
    }

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, RandomSource random, FullChunk chunk) {
        int amount = random.nextBoundedInt(this.randomAmount + 1) + this.baseAmount;
        BlockVector3 v = new BlockVector3();

        for (int i = 0; i < amount; ++i) {
            int x = random.nextRange(chunkX << 4, (chunkX << 4) + 15);
            int z = random.nextRange(chunkZ << 4, (chunkZ << 4) + 15);
            int y = PopulatorTree.getHighestWorkableBlock(level, x, z);
            if (y == Integer.MIN_VALUE) {
                continue;
            }
            new ObjectSwampTree().generate(level, random, v.setComponents(x, y, z));
        }
    }
}
