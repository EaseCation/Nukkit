package cn.nukkit.block;

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

        //TODO: huge fungus
        return true;
    }
}
