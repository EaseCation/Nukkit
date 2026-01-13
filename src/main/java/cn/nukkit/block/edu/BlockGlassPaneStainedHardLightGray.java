package cn.nukkit.block.edu;

import cn.nukkit.utils.DyeColor;

public class BlockGlassPaneStainedHardLightGray extends BlockGlassPaneStainedHard {
    protected BlockGlassPaneStainedHardLightGray() {

    }

    @Override
    public int getId() {
        return HARD_LIGHT_GRAY_STAINED_GLASS_PANE;
    }

    @Override
    public String getName() {
        return "Hardened Light Gray Stained Glass Pane";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.LIGHT_GRAY;
    }
}
