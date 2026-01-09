package cn.nukkit.block;

import cn.nukkit.level.generator.object.tree.ObjectCherryTree;
import cn.nukkit.math.LocalRandom;
import cn.nukkit.utils.BlockColor;

public class BlockSaplingCherry extends BlockSapling {
    BlockSaplingCherry() {

    }

    @Override
    public int getId() {
        return CHERRY_SAPLING;
    }

    @Override
    public String getName() {
        return "Cherry Sapling";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PINK_BLOCK_COLOR;
    }

    @Override
    protected void grow() {
        new ObjectCherryTree().placeObject(this.level, this.getFloorX(), this.getFloorY(), this.getFloorZ(), new LocalRandom());
    }
}
