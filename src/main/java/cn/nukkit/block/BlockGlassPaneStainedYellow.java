package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockGlassPaneStainedYellow extends BlockGlassPaneStained {
    BlockGlassPaneStainedYellow() {

    }

    @Override
    public int getId() {
        return YELLOW_STAINED_GLASS_PANE;
    }

    @Override
    public String getName() {
        return "Yellow Stained Glass Pane";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.YELLOW;
    }
}
