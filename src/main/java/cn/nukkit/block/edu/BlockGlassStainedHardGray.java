package cn.nukkit.block.edu;

import cn.nukkit.utils.DyeColor;

public class BlockGlassStainedHardGray extends BlockGlassStainedHard {
    protected BlockGlassStainedHardGray() {

    }

    @Override
    public int getId() {
        return HARD_GRAY_STAINED_GLASS;
    }

    @Override
    public String getName() {
        return "Hardened Gray Stained Glass";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.GRAY;
    }
}
