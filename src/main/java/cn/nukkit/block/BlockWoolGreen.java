package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockWoolGreen extends BlockWool {
    BlockWoolGreen() {

    }

    @Override
    public String getName() {
        return "Green Wool";
    }

    @Override
    public int getId() {
        return GREEN_WOOL;
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.GREEN;
    }
}
