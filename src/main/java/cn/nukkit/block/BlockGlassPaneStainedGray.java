package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockGlassPaneStainedGray extends BlockGlassPaneStained {
    BlockGlassPaneStainedGray() {

    }

    @Override
    public int getId() {
        return GRAY_STAINED_GLASS_PANE;
    }

    @Override
    public String getName() {
        return "Gray Stained Glass Pane";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.GRAY;
    }
}
