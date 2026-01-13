package cn.nukkit.block.edu;

import cn.nukkit.utils.DyeColor;

public class BlockGlassPaneStainedHardMagenta extends BlockGlassPaneStainedHard {
    protected BlockGlassPaneStainedHardMagenta() {

    }

    @Override
    public int getId() {
        return HARD_MAGENTA_STAINED_GLASS_PANE;
    }

    @Override
    public String getName() {
        return "Hardened Magenta Stained Glass Pane";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.MAGENTA;
    }
}
