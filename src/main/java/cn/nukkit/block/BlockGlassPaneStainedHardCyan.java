package cn.nukkit.block;

import cn.nukkit.block.edu.BlockGlassPaneStainedHard;
import cn.nukkit.utils.DyeColor;

public class BlockGlassPaneStainedHardCyan extends BlockGlassPaneStainedHard {
    BlockGlassPaneStainedHardCyan() {

    }

    @Override
    public int getId() {
        return HARD_CYAN_STAINED_GLASS_PANE;
    }

    @Override
    public String getName() {
        return "Hardened Cyan Stained Glass Pane";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.CYAN;
    }
}
