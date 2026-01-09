package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockGlassPaneStainedRed extends BlockGlassPaneStained {
    BlockGlassPaneStainedRed() {

    }

    @Override
    public int getId() {
        return RED_STAINED_GLASS_PANE;
    }

    @Override
    public String getName() {
        return "Red Stained Glass Pane";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.RED;
    }
}
