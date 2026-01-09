package cn.nukkit.block;

import cn.nukkit.block.edu.BlockGlassPaneStainedHard;
import cn.nukkit.utils.DyeColor;

public class BlockGlassPaneStainedHardLightBlue extends BlockGlassPaneStainedHard {
    BlockGlassPaneStainedHardLightBlue() {

    }

    @Override
    public int getId() {
        return HARD_LIGHT_BLUE_STAINED_GLASS_PANE;
    }

    @Override
    public String getName() {
        return "Hardened Light Blue Stained Glass Pane";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.LIGHT_BLUE;
    }
}
