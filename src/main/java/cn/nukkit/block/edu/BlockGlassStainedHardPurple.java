package cn.nukkit.block.edu;

import cn.nukkit.utils.DyeColor;

public class BlockGlassStainedHardPurple extends BlockGlassStainedHard {
    protected BlockGlassStainedHardPurple() {

    }

    @Override
    public int getId() {
        return HARD_PURPLE_STAINED_GLASS;
    }

    @Override
    public String getName() {
        return "Hardened Purple Stained Glass";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.PURPLE;
    }
}
