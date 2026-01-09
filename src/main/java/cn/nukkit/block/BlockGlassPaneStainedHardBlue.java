package cn.nukkit.block;

import cn.nukkit.block.edu.BlockGlassPaneStainedHard;
import cn.nukkit.utils.DyeColor;

public class BlockGlassPaneStainedHardBlue extends BlockGlassPaneStainedHard {
    BlockGlassPaneStainedHardBlue() {

    }

    @Override
    public int getId() {
        return HARD_BLUE_STAINED_GLASS_PANE;
    }

    @Override
    public String getName() {
        return "Hardened Blue Stained Glass Pane";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BLUE;
    }
}
