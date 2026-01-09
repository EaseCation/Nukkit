package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockGlassStainedLightGray extends BlockGlassStained {
    BlockGlassStainedLightGray() {

    }

    @Override
    public int getId() {
        return LIGHT_GRAY_STAINED_GLASS;
    }

    @Override
    public String getName() {
        return "Light Gray Stained Glass";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.LIGHT_GRAY;
    }
}
