package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockGlassStainedGreen extends BlockGlassStained {
    BlockGlassStainedGreen() {

    }

    @Override
    public int getId() {
        return GREEN_STAINED_GLASS;
    }

    @Override
    public String getName() {
        return "Green Stained Glass";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.GREEN;
    }
}
