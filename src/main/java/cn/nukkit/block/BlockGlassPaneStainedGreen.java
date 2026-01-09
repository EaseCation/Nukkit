package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockGlassPaneStainedGreen extends BlockGlassPaneStained {
    BlockGlassPaneStainedGreen() {

    }

    @Override
    public int getId() {
        return GREEN_STAINED_GLASS_PANE;
    }

    @Override
    public String getName() {
        return "Green Stained Glass Pane";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.GREEN;
    }
}
