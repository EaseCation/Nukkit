package cn.nukkit.block;

import cn.nukkit.level.generator.object.tree.ObjectCrimsonTree;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.utils.BlockColor;

public class BlockNetherFungusCrimson extends BlockNetherFungus {
    public BlockNetherFungusCrimson() {
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

        new ObjectCrimsonTree().placeObject(this.level, this.getFloorX(), this.getFloorY(), this.getFloorZ(), NukkitRandom.current());
        return true;
    }
}
