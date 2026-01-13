package cn.nukkit.block.edu;

import cn.nukkit.utils.DyeColor;

public class BlockGlassStainedHardLime extends BlockGlassStainedHard {
    protected BlockGlassStainedHardLime() {

    }

    @Override
    public int getId() {
        return HARD_LIME_STAINED_GLASS;
    }

    @Override
    public String getName() {
        return "Hardened Lime Stained Glass";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.LIME;
    }
}
