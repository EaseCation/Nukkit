package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockGlassStainedGray extends BlockGlassStained {
    BlockGlassStainedGray() {

    }

    @Override
    public int getId() {
        return GRAY_STAINED_GLASS;
    }

    @Override
    public String getName() {
        return "Gray Stained Glass";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.GRAY;
    }
}
