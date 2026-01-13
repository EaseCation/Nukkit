package cn.nukkit.block.edu;

import cn.nukkit.utils.DyeColor;

public class BlockGlassStainedHardGreen extends BlockGlassStainedHard {
    protected BlockGlassStainedHardGreen() {

    }

    @Override
    public int getId() {
        return HARD_GREEN_STAINED_GLASS;
    }

    @Override
    public String getName() {
        return "Hardened Green Stained Glass";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.GREEN;
    }
}
