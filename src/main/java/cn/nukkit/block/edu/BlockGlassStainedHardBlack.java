package cn.nukkit.block.edu;

import cn.nukkit.utils.DyeColor;

public class BlockGlassStainedHardBlack extends BlockGlassStainedHard {
    protected BlockGlassStainedHardBlack() {

    }

    @Override
    public int getId() {
        return HARD_BLACK_STAINED_GLASS;
    }

    @Override
    public String getName() {
        return "Hardened Black Stained Glass";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BLACK;
    }
}
