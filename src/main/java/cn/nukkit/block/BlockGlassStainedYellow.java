package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockGlassStainedYellow extends BlockGlassStained {
    BlockGlassStainedYellow() {

    }

    @Override
    public int getId() {
        return YELLOW_STAINED_GLASS;
    }

    @Override
    public String getName() {
        return "Yellow Stained Glass";
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.YELLOW;
    }
}
