package cn.nukkit.block.edu;

import cn.nukkit.utils.DyeColor;

public class BlockGlassStainedHardWhite extends BlockGlassStainedHard {
    protected BlockGlassStainedHardWhite() {

    }

    @Override
    public int getId() {
        return HARD_WHITE_STAINED_GLASS;
    }

    @Override
    public String getName() {
        return "Hardened White Stained Glass";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.WHITE;
    }
}
