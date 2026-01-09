package cn.nukkit.block;

import cn.nukkit.block.edu.BlockGlassPaneStainedHard;
import cn.nukkit.utils.DyeColor;

public class BlockGlassPaneStainedHardYellow extends BlockGlassPaneStainedHard {
    BlockGlassPaneStainedHardYellow() {

    }

    @Override
    public int getId() {
        return HARD_YELLOW_STAINED_GLASS_PANE;
    }

    @Override
    public String getName() {
        return "Hardened Yellow Stained Glass Pane";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.YELLOW;
    }
}
