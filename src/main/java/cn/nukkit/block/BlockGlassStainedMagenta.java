package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockGlassStainedMagenta extends BlockGlassStained {
    BlockGlassStainedMagenta() {

    }

    @Override
    public int getId() {
        return MAGENTA_STAINED_GLASS;
    }

    @Override
    public String getName() {
        return "Magenta Stained Glass";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.MAGENTA;
    }
}
