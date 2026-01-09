package cn.nukkit.level.generator.object;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.RandomSource;

public abstract class BasicGenerator {

    //also autism, see below
    public abstract boolean generate(ChunkManager level, RandomSource rand, BlockVector3 position);

    public void setDecorationDefaults() {
    }

    protected void setBlockAndNotifyAdequately(ChunkManager level, BlockVector3 pos, Block state) {
        setBlock(level, pos, state);
    }

    //what autism is this? why are we using floating-point vectors for setting block IDs?
    protected void setBlock(ChunkManager level, BlockVector3 v, Block b) {
        level.setBlockAt(0, v.x, v.y, v.z, b.getId(), b.getDamage());
    }
}
