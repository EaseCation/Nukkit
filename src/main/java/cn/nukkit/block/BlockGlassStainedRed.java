package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockGlassStainedRed extends BlockGlassStained {
    BlockGlassStainedRed() {

    }

    @Override
    public int getId() {
        return RED_STAINED_GLASS;
    }

    @Override
    public String getName() {
        return "Red Stained Glass";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.RED;
    }
}
