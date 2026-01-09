package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockGlassPaneStainedLime extends BlockGlassPaneStained {
    BlockGlassPaneStainedLime() {

    }

    @Override
    public int getId() {
        return LIME_STAINED_GLASS_PANE;
    }

    @Override
    public String getName() {
        return "Lime Stained Glass Pane";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.LIME;
    }
}
