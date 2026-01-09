package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockGlassPaneStainedBrown extends BlockGlassPaneStained {
    BlockGlassPaneStainedBrown() {

    }

    @Override
    public int getId() {
        return BROWN_STAINED_GLASS_PANE;
    }

    @Override
    public String getName() {
        return "Brown Stained Glass Pane";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BROWN;
    }
}
