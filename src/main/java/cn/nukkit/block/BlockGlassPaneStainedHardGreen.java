package cn.nukkit.block;

import cn.nukkit.block.edu.BlockGlassPaneStainedHard;
import cn.nukkit.utils.DyeColor;

public class BlockGlassPaneStainedHardGreen extends BlockGlassPaneStainedHard {
    BlockGlassPaneStainedHardGreen() {

    }

    @Override
    public int getId() {
        return HARD_GREEN_STAINED_GLASS_PANE;
    }

    @Override
    public String getName() {
        return "Hardened Green Stained Glass Pane";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.GREEN;
    }
}
