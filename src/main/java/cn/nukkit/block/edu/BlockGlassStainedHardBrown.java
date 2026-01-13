package cn.nukkit.block.edu;

import cn.nukkit.utils.DyeColor;

public class BlockGlassStainedHardBrown extends BlockGlassStainedHard {
    protected BlockGlassStainedHardBrown() {

    }

    @Override
    public int getId() {
        return HARD_BROWN_STAINED_GLASS;
    }

    @Override
    public String getName() {
        return "Hardened Brown Stained Glass";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BROWN;
    }
}
