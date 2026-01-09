package cn.nukkit.block;

import cn.nukkit.block.edu.BlockGlassPaneStainedHard;
import cn.nukkit.utils.DyeColor;

public class BlockGlassPaneStainedHardOrange extends BlockGlassPaneStainedHard {
    BlockGlassPaneStainedHardOrange() {

    }

    @Override
    public int getId() {
        return HARD_ORANGE_STAINED_GLASS_PANE;
    }

    @Override
    public String getName() {
        return "Hardened Orange Stained Glass Pane";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.ORANGE;
    }
}
