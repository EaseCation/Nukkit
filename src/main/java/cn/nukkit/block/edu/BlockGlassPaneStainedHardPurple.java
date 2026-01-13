package cn.nukkit.block.edu;

import cn.nukkit.utils.DyeColor;

public class BlockGlassPaneStainedHardPurple extends BlockGlassPaneStainedHard {
    protected BlockGlassPaneStainedHardPurple() {

    }

    @Override
    public int getId() {
        return HARD_PURPLE_STAINED_GLASS_PANE;
    }

    @Override
    public String getName() {
        return "Hardened Purple Stained Glass Pane";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.PURPLE;
    }
}
