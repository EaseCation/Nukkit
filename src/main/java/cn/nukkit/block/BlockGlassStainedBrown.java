package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockGlassStainedBrown extends BlockGlassStained {
    BlockGlassStainedBrown() {

    }

    @Override
    public int getId() {
        return BROWN_STAINED_GLASS;
    }

    @Override
    public String getName() {
        return "Brown Stained Glass";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BROWN;
    }
}
