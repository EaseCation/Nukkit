package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockGlassStainedCyan extends BlockGlassStained {
    BlockGlassStainedCyan() {

    }

    @Override
    public int getId() {
        return CYAN_STAINED_GLASS;
    }

    @Override
    public String getName() {
        return "Cyan Stained Glass";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.CYAN;
    }
}
