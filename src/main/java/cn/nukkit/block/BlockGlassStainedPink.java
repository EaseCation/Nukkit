package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockGlassStainedPink extends BlockGlassStained {
    BlockGlassStainedPink() {

    }

    @Override
    public int getId() {
        return PINK_STAINED_GLASS;
    }

    @Override
    public String getName() {
        return "Pink Stained Glass";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.PINK;
    }
}
