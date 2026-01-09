package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockGlassPaneStainedPink extends BlockGlassPaneStained {
    BlockGlassPaneStainedPink() {

    }

    @Override
    public int getId() {
        return PINK_STAINED_GLASS_PANE;
    }

    @Override
    public String getName() {
        return "Pink Stained Glass Pane";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.PINK;
    }
}
