package cn.nukkit.block;

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

        //TODO: huge fungus
        return true;
    }
}
