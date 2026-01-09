package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockGlassStainedBlue extends BlockGlassStained {
    BlockGlassStainedBlue() {

    }

    @Override
    public int getId() {
        return BLUE_STAINED_GLASS;
    }

    @Override
    public String getName() {
        return "Blue Stained Glass";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BLUE;
    }
}
