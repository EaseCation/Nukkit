package cn.nukkit.block.edu;

import cn.nukkit.utils.DyeColor;

public class BlockGlassStainedHardLightGray extends BlockGlassStainedHard {
    protected BlockGlassStainedHardLightGray() {

    }

    @Override
    public int getId() {
        return HARD_LIGHT_GRAY_STAINED_GLASS;
    }

    @Override
    public String getName() {
        return "Hardened Light Gray Stained Glass";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.LIGHT_GRAY;
    }
}
