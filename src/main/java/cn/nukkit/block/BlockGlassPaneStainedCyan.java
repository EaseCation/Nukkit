package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockGlassPaneStainedCyan extends BlockGlassPaneStained {
    BlockGlassPaneStainedCyan() {

    }

    @Override
    public int getId() {
        return CYAN_STAINED_GLASS_PANE;
    }

    @Override
    public String getName() {
        return "Cyan Stained Glass Pane";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.CYAN;
    }
}
