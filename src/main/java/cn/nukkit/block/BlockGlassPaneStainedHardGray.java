package cn.nukkit.block;

import cn.nukkit.block.edu.BlockGlassPaneStainedHard;
import cn.nukkit.utils.DyeColor;

public class BlockGlassPaneStainedHardGray extends BlockGlassPaneStainedHard {
    BlockGlassPaneStainedHardGray() {

    }

    @Override
    public int getId() {
        return HARD_GRAY_STAINED_GLASS_PANE;
    }

    @Override
    public String getName() {
        return "Hardened Gray Stained Glass Pane";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.GRAY;
    }
}
