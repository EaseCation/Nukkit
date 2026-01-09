package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockGlassPaneStainedBlack extends BlockGlassPaneStained {
    BlockGlassPaneStainedBlack() {

    }

    @Override
    public int getId() {
        return BLACK_STAINED_GLASS_PANE;
    }

    @Override
    public String getName() {
        return "Black Stained Glass Pane";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BLACK;
    }
}
