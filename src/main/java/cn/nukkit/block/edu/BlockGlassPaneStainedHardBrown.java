package cn.nukkit.block.edu;

import cn.nukkit.utils.DyeColor;

public class BlockGlassPaneStainedHardBrown extends BlockGlassPaneStainedHard {
    protected BlockGlassPaneStainedHardBrown() {

    }

    @Override
    public int getId() {
        return HARD_BROWN_STAINED_GLASS_PANE;
    }

    @Override
    public String getName() {
        return "Hardened Brown Stained Glass Pane";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BROWN;
    }
}
