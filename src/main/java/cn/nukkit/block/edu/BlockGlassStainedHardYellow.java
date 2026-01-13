package cn.nukkit.block.edu;

import cn.nukkit.utils.DyeColor;

public class BlockGlassStainedHardYellow extends BlockGlassStainedHard {
    protected BlockGlassStainedHardYellow() {

    }

    @Override
    public int getId() {
        return HARD_YELLOW_STAINED_GLASS;
    }

    @Override
    public String getName() {
        return "Hardened Yellow Stained Glass";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.YELLOW;
    }
}
