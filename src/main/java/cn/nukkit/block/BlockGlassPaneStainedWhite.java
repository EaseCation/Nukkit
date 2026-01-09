package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockGlassPaneStainedWhite extends BlockGlassPaneStained {
    BlockGlassPaneStainedWhite() {

    }

    @Override
    public int getId() {
        return WHITE_STAINED_GLASS_PANE;
    }

    @Override
    public String getName() {
        return "White Stained Glass Pane";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.WHITE;
    }
}
