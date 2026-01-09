package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockGlassPaneStainedPurple extends BlockGlassPaneStained {
    BlockGlassPaneStainedPurple() {

    }

    @Override
    public int getId() {
        return PURPLE_STAINED_GLASS_PANE;
    }

    @Override
    public String getName() {
        return "Purple Stained Glass Pane";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.PURPLE;
    }
}
