package cn.nukkit.block;

import cn.nukkit.block.edu.BlockGlassStainedHard;
import cn.nukkit.utils.DyeColor;

public class BlockGlassStainedHardRed extends BlockGlassStainedHard {
    BlockGlassStainedHardRed() {

    }

    @Override
    public int getId() {
        return HARD_RED_STAINED_GLASS;
    }

    @Override
    public String getName() {
        return "Hardened Red Stained Glass";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.RED;
    }
}
