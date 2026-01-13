package cn.nukkit.block.edu;

import cn.nukkit.utils.DyeColor;

public class BlockGlassPaneStainedHardWhite extends BlockGlassPaneStainedHard {
    protected BlockGlassPaneStainedHardWhite() {

    }

    @Override
    public int getId() {
        return HARD_WHITE_STAINED_GLASS_PANE;
    }

    @Override
    public String getName() {
        return "Hardened White Stained Glass Pane";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.WHITE;
    }
}
