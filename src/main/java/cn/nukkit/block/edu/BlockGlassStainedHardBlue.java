package cn.nukkit.block.edu;

import cn.nukkit.utils.DyeColor;

public class BlockGlassStainedHardBlue extends BlockGlassStainedHard {
    protected BlockGlassStainedHardBlue() {

    }

    @Override
    public int getId() {
        return HARD_BLUE_STAINED_GLASS;
    }

    @Override
    public String getName() {
        return "Hardened Blue Stained Glass";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BLUE;
    }
}
