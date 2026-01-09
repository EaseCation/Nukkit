package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockGlassStainedPurple extends BlockGlassStained {
    BlockGlassStainedPurple() {

    }

    @Override
    public int getId() {
        return PURPLE_STAINED_GLASS;
    }

    @Override
    public String getName() {
        return "Purple Stained Glass";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.PURPLE;
    }
}
