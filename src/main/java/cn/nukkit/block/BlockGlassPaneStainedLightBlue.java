package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockGlassPaneStainedLightBlue extends BlockGlassPaneStained {
    BlockGlassPaneStainedLightBlue() {

    }

    @Override
    public int getId() {
        return LIGHT_BLUE_STAINED_GLASS_PANE;
    }

    @Override
    public String getName() {
        return "Light Blue Stained Glass Pane";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.LIGHT_BLUE;
    }
}
