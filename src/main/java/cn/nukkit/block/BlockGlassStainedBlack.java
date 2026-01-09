package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockGlassStainedBlack extends BlockGlassStained {
    BlockGlassStainedBlack() {

    }

    @Override
    public int getId() {
        return BLACK_STAINED_GLASS;
    }

    @Override
    public String getName() {
        return "Black Stained Glass";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BLACK;
    }
}
