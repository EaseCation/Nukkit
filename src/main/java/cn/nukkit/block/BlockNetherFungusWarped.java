package cn.nukkit.block;

import cn.nukkit.level.generator.object.tree.ObjectWarpedTree;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.utils.BlockColor;

public class BlockNetherFungusWarped extends BlockNetherFungus {
    public BlockNetherFungusWarped() {
    }

    @Override
    public int getId() {
        return WARPED_FUNGUS;
    }

    @Override
    public String getName() {
        return "Warped Fungus";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CYAN_BLOCK_COLOR;
    }

    @Override
    protected boolean grow() {
        if (down().getId() != WARPED_NYLIUM) {
            return false;
        }

        new ObjectWarpedTree().placeObject(this.level, this.getFloorX(), this.getFloorY(), this.getFloorZ(), NukkitRandom.current());
        return true;
    }
}
