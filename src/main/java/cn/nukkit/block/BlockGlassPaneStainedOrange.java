package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockGlassPaneStainedOrange extends BlockGlassPaneStained {
    BlockGlassPaneStainedOrange() {

    }

    @Override
    public int getId() {
        return ORANGE_STAINED_GLASS_PANE;
    }

    @Override
    public String getName() {
        return "Orange Stained Glass Pane";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.ORANGE;
    }
}
