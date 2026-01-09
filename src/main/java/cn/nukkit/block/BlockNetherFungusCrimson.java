package cn.nukkit.block;

import cn.nukkit.level.generator.object.tree.ObjectCrimsonTree;
import cn.nukkit.math.LocalRandom;
import cn.nukkit.utils.BlockColor;

public class BlockNetherFungusCrimson extends BlockNetherFungus {
    BlockNetherFungusCrimson() {

    }

    @Override
    public int getId() {
        return CRIMSON_FUNGUS;
    }

    @Override
    public String getName() {
        return "Crimson Fungus";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.NETHER_BLOCK_COLOR;
    }

    @Override
    protected boolean grow() {
        if (down().getId() != CRIMSON_NYLIUM) {
            return false;
        }

        new ObjectCrimsonTree().placeObject(this.level, this.getFloorX(), this.getFloorY(), this.getFloorZ(), new LocalRandom());
        return true;
    }
}
