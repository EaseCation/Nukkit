package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;

public class BlockWoolLightGray extends BlockWool {
    BlockWoolLightGray() {

    }

    @Override
    public String getName() {
        return "Light Gray Wool";
    }

    @Override
    public int getId() {
        return LIGHT_GRAY_WOOL;
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.LIGHT_GRAY;
    }
}
