package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockWoolBlue extends BlockWool {
    BlockWoolBlue() {

    }

    @Override
    public String getName() {
        return "Blue Wool";
    }

    @Override
    public int getId() {
        return BLUE_WOOL;
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BLUE;
    }
}
