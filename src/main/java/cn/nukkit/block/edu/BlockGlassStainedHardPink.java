package cn.nukkit.block.edu;

import cn.nukkit.utils.DyeColor;

public class BlockGlassStainedHardPink extends BlockGlassStainedHard {
    protected BlockGlassStainedHardPink() {

    }

    @Override
    public int getId() {
        return HARD_PINK_STAINED_GLASS;
    }

    @Override
    public String getName() {
        return "Hardened Pink Stained Glass";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.PINK;
    }
}
