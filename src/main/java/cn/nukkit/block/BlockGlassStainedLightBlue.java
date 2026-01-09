package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockGlassStainedLightBlue extends BlockGlassStained {
    BlockGlassStainedLightBlue() {

    }

    @Override
    public int getId() {
        return LIGHT_BLUE_STAINED_GLASS;
    }

    @Override
    public String getName() {
        return "Light Blue Stained Glass";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.LIGHT_BLUE;
    }
}
