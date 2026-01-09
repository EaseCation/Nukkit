package cn.nukkit.block;

import cn.nukkit.block.edu.BlockGlassPaneStainedHard;
import cn.nukkit.utils.DyeColor;

public class BlockGlassPaneStainedHardPink extends BlockGlassPaneStainedHard {
    BlockGlassPaneStainedHardPink() {

    }

    @Override
    public int getId() {
        return HARD_PINK_STAINED_GLASS_PANE;
    }

    @Override
    public String getName() {
        return "Hardened Pink Stained Glass Pane";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.PINK;
    }
}
