package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockGlassStainedOrange extends BlockGlassStained {
    BlockGlassStainedOrange() {

    }

    @Override
    public int getId() {
        return ORANGE_STAINED_GLASS;
    }

    @Override
    public String getName() {
        return "Orange Stained Glass";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.ORANGE;
    }
}
