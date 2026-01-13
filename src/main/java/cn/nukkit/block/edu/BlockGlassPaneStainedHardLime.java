package cn.nukkit.block.edu;

import cn.nukkit.utils.DyeColor;

public class BlockGlassPaneStainedHardLime extends BlockGlassPaneStainedHard {
    protected BlockGlassPaneStainedHardLime() {

    }

    @Override
    public int getId() {
        return HARD_LIME_STAINED_GLASS_PANE;
    }

    @Override
    public String getName() {
        return "Hardened Lime Stained Glass Pane";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.LIME;
    }
}
