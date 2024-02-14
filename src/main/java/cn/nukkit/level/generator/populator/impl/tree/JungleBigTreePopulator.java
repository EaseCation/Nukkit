package cn.nukkit.level.generator.populator.impl.tree;

import cn.nukkit.block.*;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.object.tree.ObjectJungleBigTree;
import cn.nukkit.level.generator.populator.impl.PopulatorTree;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.NukkitRandom;


public class JungleBigTreePopulator extends Populator {
    private int randomAmount;
    private int baseAmount;

    private final int type;

    public JungleBigTreePopulator() {
        this(BlockSapling.JUNGLE);
    }

    public JungleBigTreePopulator(int type) {
        this.type = type;
    }

    public void setRandomAmount(int randomAmount) {
        this.randomAmount = randomAmount;
    }

    public void setBaseAmount(int baseAmount) {
        this.baseAmount = baseAmount;
    }

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        int amount = random.nextBoundedInt(this.randomAmount + 1) + this.baseAmount;
        BlockVector3 v = new BlockVector3();

        for (int i = 0; i < amount; ++i) {
            int x = random.nextRange(chunkX << 4, (chunkX << 4) + 15);
            int z = random.nextRange(chunkZ << 4, (chunkZ << 4) + 15);
            int y = PopulatorTree.getHighestWorkableBlock(level, x, z);
            if (y == Integer.MIN_VALUE) {
                continue;
            }
            new ObjectJungleBigTree(10, 20, Block.get(BlockID.LOG, BlockWood.JUNGLE), Block.get(BlockID.LEAVES, BlockLeaves.JUNGLE)).generate(level, random, v.setComponents(x, y, z));
        }
    }
}
