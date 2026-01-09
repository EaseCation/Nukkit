package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockGlassPaneStainedBlue extends BlockGlassPaneStained {
    BlockGlassPaneStainedBlue() {

    }

    @Override
    public int getId() {
        return BLUE_STAINED_GLASS_PANE;
    }

    @Override
    public String getName() {
        return "Blue Stained Glass Pane";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BLUE;
    }
}
