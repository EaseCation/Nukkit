package cn.nukkit.block;

import cn.nukkit.block.edu.BlockGlassPaneStainedHard;
import cn.nukkit.utils.DyeColor;

public class BlockGlassPaneStainedHardRed extends BlockGlassPaneStainedHard {
    BlockGlassPaneStainedHardRed() {

    }

    @Override
    public int getId() {
        return HARD_RED_STAINED_GLASS_PANE;
    }

    @Override
    public String getName() {
        return "Hardened Red Stained Glass Pane";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.RED;
    }
}
