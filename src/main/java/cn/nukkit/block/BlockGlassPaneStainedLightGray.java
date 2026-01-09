package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockGlassPaneStainedLightGray extends BlockGlassPaneStained {
    BlockGlassPaneStainedLightGray() {

    }

    @Override
    public int getId() {
        return LIGHT_GRAY_STAINED_GLASS_PANE;
    }

    @Override
    public String getName() {
        return "Light Gray Stained Glass Pane";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.LIGHT_GRAY;
    }
}
