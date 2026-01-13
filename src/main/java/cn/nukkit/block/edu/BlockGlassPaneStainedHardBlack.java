package cn.nukkit.block.edu;

import cn.nukkit.utils.DyeColor;

public class BlockGlassPaneStainedHardBlack extends BlockGlassPaneStainedHard {
    protected BlockGlassPaneStainedHardBlack() {

    }

    @Override
    public int getId() {
        return HARD_BLACK_STAINED_GLASS_PANE;
    }

    @Override
    public String getName() {
        return "Hardened Black Stained Glass Pane";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BLACK;
    }
}
