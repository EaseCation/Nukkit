package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockGlassPaneStainedMagenta extends BlockGlassPaneStained {
    BlockGlassPaneStainedMagenta() {

    }

    @Override
    public int getId() {
        return MAGENTA_STAINED_GLASS_PANE;
    }

    @Override
    public String getName() {
        return "Magenta Stained Glass Pane";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.MAGENTA;
    }
}
