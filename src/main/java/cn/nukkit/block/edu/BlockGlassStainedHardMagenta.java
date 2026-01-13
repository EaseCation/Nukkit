package cn.nukkit.block.edu;

import cn.nukkit.utils.DyeColor;

public class BlockGlassStainedHardMagenta extends BlockGlassStainedHard {
    protected BlockGlassStainedHardMagenta() {

    }

    @Override
    public int getId() {
        return HARD_MAGENTA_STAINED_GLASS;
    }

    @Override
    public String getName() {
        return "Hardened Magenta Stained Glass";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.MAGENTA;
    }
}
